package org.JStudio.Plugins.Controllers;


import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.JStudio.Plugins.Models.Plugin;
import org.JStudio.Plugins.Models.audioFilters;
import org.JStudio.Utils.AlertBox;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
            getProcessedAudio();
        });
    }

    private void getFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile == null) {
            AlertBox.display("Input Error", "No audio file selected.");
            return;
        }

        inputFile = selectedFile.getAbsolutePath();
        setFilePathName(inputFile);
        try {
            File file = new File(inputFile);
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

    private void setupMenu(MenuButton menuButton) {
        for (MenuItem item : menuButton.getItems()) {
            item.setOnAction(event -> {
                menuButton.setText(item.getText());
                filterType.put(menuButton, item.getText());
                selectedFilter = item.getText();
            });
        }
    }

    private void setDefaultMenuSelection(MenuButton menuButton) {
        for (MenuItem item : menuButton.getItems()) {
            if (item.getText().equals("Low")) {
                menuButton.setText(item.getText());
                selectedFilter = item.getText();
                break;
            }
        }
    }

    public byte[] getProcessedAudio() {
        if (filteredBytes == null || filteredBytes.length == 0) {
            AlertBox.display("Export Error", "No processed audio to export.");
            return null;
        }
        return filteredBytes;
    }

    public void setStage(Stage stage) {
        this.stage = stage;

        this.stage.setOnCloseRequest(event -> {
            stopAudio();
        });
    }

}