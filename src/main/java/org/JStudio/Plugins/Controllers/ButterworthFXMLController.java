package org.JStudio.Plugins.Controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.JStudio.Plugins.Models.Plugin;
import org.JStudio.Plugins.Models.audioFilters;
import org.JStudio.Utils.AlertBox;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Class that applies the butterworth audio filter effect
 */
public class ButterworthFXMLController extends Plugin {

    private Stage stage;

    @FXML
    private Button playButton, exportButton;

    @FXML
    private RadioButton lowPassRadio, highPassRadio, bandPassRadio, bandStopRadio;

    @FXML
    private TextField frequencyField, qualityField, outputFileName;

    private ToggleGroup group;

    private String inputFile;

    public short[] samples;
    public float sampleRate;
    public byte[] filteredBytes;

    public AudioFormat format;

    /**
     * Method that determines the logic of the plugin
     */
    public void initialize() {
        group = new ToggleGroup();
        lowPassRadio.setToggleGroup(group);
        highPassRadio.setToggleGroup(group);
        bandPassRadio.setToggleGroup(group);
        bandStopRadio.setToggleGroup(group);

        frequencyField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                frequencyField.setText(oldValue);
            }
        });

        qualityField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                qualityField.setText(oldValue);
            }
        });

        getFile();

        playButton.setOnMouseClicked(event -> {
            stopAudio();
            try {
                if (samples == null || samples.length == 0) {
                    AlertBox.display("Error", "No audio file loaded. Please import an audio file first.");
                    return;
                }

                if (frequencyField.getText().isEmpty() || qualityField.getText().isEmpty()) {
                    AlertBox.display("Input Error", "Please enter both Frequency and Q values.");
                    return;
                }

                float freq = Float.parseFloat(frequencyField.getText());
                float q = Float.parseFloat(qualityField.getText());

                RadioButton selected = (RadioButton) group.getSelectedToggle();
                if (selected == null) {
                    AlertBox.display("Selection Error", "Please select a filter type.");
                    return;
                }

                switch (selected.getId()) {
                    case "lowPassRadio":
                        audioFilters.BiquadFilter.applyBiquadLowPassFilter(samples, freq, q, sampleRate);
                        break;
                    case "highPassRadio":
                        audioFilters.BiquadFilter.applyBiquadHighPassFilter(samples, freq, q, sampleRate);
                        break;
                    case "bandPassRadio":
                        audioFilters.BiquadFilter.applyBiquadBandPassFilter(samples, freq, q, sampleRate);
                        break;
                    case "bandStopRadio":
                        audioFilters.BiquadFilter.applyBiquadBandStopFilter(samples, freq, q, sampleRate);
                        break;
                    default:
                        AlertBox.display("Error", "Invalid filter selection.");
                        return;
                }

                filteredBytes = audioFilters.shortsToBytes(samples);

                if (filteredBytes != null) {
                    playButton.setDisable(true);
                    playAudio(filteredBytes);

                    PauseTransition delay = new javafx.animation.PauseTransition(Duration.seconds(3));
                    delay.setOnFinished(e -> playButton.setDisable(false));
                    delay.play();
                }

            } catch (NumberFormatException e) {
                AlertBox.display("Input Error", "Invalid number format in Frequency or Q field.");
            } catch (Exception e) {
                AlertBox.display("Error", "An unexpected error occurred: " + e.getMessage());
            }
        });

        exportButton.setOnMouseClicked(event -> {
            stopAudio();
//            getProcessedAudio();
            export("ButterWorthFilter");
        });
    }

    /**
     * Method that gets the file and analyzes it
     */
    public void getFile() {
        try {
            FileChooser fileChooser = new FileChooser();
            inputFile = fileChooser.showOpenDialog(null).getAbsolutePath();
            setFilePathName(inputFile);
            File file = new File(inputFile);
            fileName = file.getName();
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            format = audioStream.getFormat();
            byte[] audioBytes = audioStream.readAllBytes();
            audioStream.close();

            samples = audioFilters.bytesToShorts(audioBytes);
            sampleRate = format.getSampleRate();

        } catch (Exception e) {
            AlertBox.display("File Error", "There was a problem during the file conversion:" + e.getMessage());
        }
    }

    /**
     * Method that returns the processes audio
     * @return the array with the applied plugin effect
     */
    public byte[] getProcessedAudio() {
        if (filteredBytes == null || filteredBytes.length == 0) {
            AlertBox.display("Export Error", "No processed audio to export.");
            return null;
        }
        return filteredBytes;
    }

    /**
     * Method that sets the stage
     * @param stage the current stage of the plugin
     */
    public void setStage(Stage stage) {
        this.stage = stage;

        this.stage.setOnCloseRequest(event -> {
            stopAudio();
        });
    }

    /**
     * Method to export an audio file given a name
     * @param pluginName the name of the exported audio file
     */
    public void export(String pluginName){
        try {
            //create AudioInputStream from the byte array
            ByteArrayInputStream bais = new ByteArrayInputStream(filteredBytes);
            AudioInputStream audioInputStream = new AudioInputStream(bais, format, filteredBytes.length / format.getFrameSize());

            //make sure the file path exists
            File dir = new File(System.getProperty("user.home") + File.separator + "Music" + File.separator + "JStudio" + File.separator + "audio_Files" + File.separator + "Plugins");
            if (!dir.exists()) {
                Files.createDirectories(dir.toPath());
            }

            File wavFile;

            //save to wav file
            if (new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName +".wav").exists()) {
                int i = 1;
                while(new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName + i +".wav").exists()){
                    i++;
                }
                wavFile = new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName + i +".wav");
            } else {
                wavFile = new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName +".wav");
            }

            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
