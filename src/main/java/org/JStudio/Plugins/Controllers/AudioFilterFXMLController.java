package org.JStudio.Plugins.Controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.JStudio.Controllers.SettingsController;
import org.JStudio.Plugins.Models.Plugin;
import org.JStudio.Plugins.Models.audioFilters;
import org.JStudio.Plugins.Views.SpectrographStage;
import org.JStudio.Utils.AlertBox;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller class of the base audio filters
 */
public class AudioFilterFXMLController extends Plugin {

    private Stage stage;
    private final Map<MenuButton, String> filterType = new HashMap<>();
    public String inputFile;
    @FXML
    private Label cutOffLabel;
    @FXML
    private TextField fieldFrequency;
    @FXML
    private MenuButton optionsCutOff;
    @FXML
    private Button saveBtn, playBtn;
    private float userFrequency;
    private String selectedFilter;

    private float sampleRate;
    private short[] samples;
    private byte[] audioBytes;
    private AudioFormat format;
    public byte[] filteredBytes;

    /**
     * Method that takes care of the main UI
     */
    @FXML
    public void initialize() {
        setupMenu(optionsCutOff);
        setDefaultMenuSelection(optionsCutOff);
        getFile();

        fieldFrequency.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                fieldFrequency.setText(oldValue);
            }
        });

        playBtn.setOnAction(event -> {
            stopAudio();
            if (fieldFrequency.getText().isEmpty()) {
                AlertBox.display("Input Error", "Please enter a frequency value.");
                return;
            }

            try {
                userFrequency = (float) Double.parseDouble(fieldFrequency.getText());
            } catch (NumberFormatException e) {
                AlertBox.display("Input Error", "Invalid frequency format. Please enter a number.");
                return;
            }
            //chose filter to apply
            if ("Low".equals(selectedFilter)) {
                audioFilters.applyLowPassFilter(samples, sampleRate, userFrequency);
            } else if ("High".equals(selectedFilter)) {
                audioFilters.applyHighPassFilter(samples, sampleRate, userFrequency);
            } else {
                AlertBox.display("Filter Error", "Unsupported filter type selected.");
                return;
            }

            filteredBytes = audioFilters.shortsToBytes(samples);

            playAudio(filteredBytes);

            if (filteredBytes != null) {
                playBtn.setDisable(true);
                playAudio(filteredBytes);

                PauseTransition delay = new javafx.animation.PauseTransition(Duration.seconds(3));
                delay.setOnFinished(e -> playBtn.setDisable(false));
                delay.play();
            }
        });

        saveBtn.setOnAction(event -> {
            stopAudio();
            export("Base Audio Filter");

            if (SettingsController.isTesting()) {
                stage.close();
                SpectrographStage spectrographStage = new SpectrographStage();
                try {
                    if (SpectrographStage.controller != null) {
                        SpectrographStage.controller.setArrays(shortsToBytes(samples), filteredBytes);
                    }
                } catch (Exception e) {
                    AlertBox.display("Export Error", "Failed to load Unit Testing interface.");
                }
                spectrographStage.show();
            }
        });
    }

    /**
     * Method to get the file
     */
    private void getFile() { // this
        try {
            setIsFileSelected(false);
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if (file == null) {
                return;
            }
            setIsFileSelected(true);
            inputFile = file.getAbsolutePath();
            setFilePathName(inputFile);
            fileName = file.getName();
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            format = audioStream.getFormat();
            audioBytes = audioStream.readAllBytes();
            audioStream.close();

            samples = audioFilters.bytesToShorts(audioBytes);
            sampleRate = format.getSampleRate();
        } catch (UnsupportedAudioFileException | IOException e) {
            AlertBox.display("File Error", "There was a problem during the file conversion:" + e.getMessage());
        }
    }

    /**
     * Method to setup the menu button
     *
     * @param menuButton the container for the choices
     */
    private void setupMenu(MenuButton menuButton) {
        for (MenuItem item : menuButton.getItems()) {
            item.setOnAction(event -> {
                menuButton.setText(item.getText());
                filterType.put(menuButton, item.getText());
                selectedFilter = item.getText();
            });
        }
    }

    /**
     * Method to set the default option to low filter
     *
     * @param menuButton the container of the choices
     */
    private void setDefaultMenuSelection(MenuButton menuButton) {
        for (MenuItem item : menuButton.getItems()) {
            if (item.getText().equals("Low")) {
                menuButton.setText(item.getText());
                selectedFilter = item.getText();
                break;
            }
        }
    }

    /**
     * Method that sets the stage
     *
     * @param stage the stage of the controller
     */
    public void setStage(Stage stage) {
        this.stage = stage;

        this.stage.setOnCloseRequest(event -> {
            stopAudio();
        });
    }

    /**
     * Method to export an audio file given a name
     *
     * @param pluginName the name of the exported audio file
     */
    public void export(String pluginName) { // this
        try {
            //create AudioInputStream from the byte array
            ByteArrayInputStream bais = new ByteArrayInputStream(finalAudio);
            AudioInputStream audioInputStream = new AudioInputStream(bais, format, finalAudio.length / format.getFrameSize());

            //make sure the file path exists
            File dir = new File(System.getProperty("user.home") + File.separator + "Music" + File.separator + "JStudio" + File.separator + "audio_Files" + File.separator + "Plugins");
            if (!dir.exists()) {
                Files.createDirectories(dir.toPath());
            }

            File wavFile;

            //save to wav file
            if (new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName + ".wav").exists()) {
                int i = 1;
                while (new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName + i + ".wav").exists()) {
                    i++;
                }
                wavFile = new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName + i + ".wav");
            } else {
                wavFile = new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName + ".wav");
            }

            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

}
