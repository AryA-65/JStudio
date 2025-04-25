package org.JStudio.Plugins.Controllers;


import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.JStudio.Plugins.Models.audioFilters;
import org.JStudio.Utils.AlertBox;

public class AudioFilterFXMLController {
    private final Map<MenuButton, String> filterType = new HashMap<>();
    public String inputFile;
    public String outputFile = "filtered_output.wav"; //todo create a field for the user to enter the output file name
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
    private Clip audioClip;
    private boolean isPlaying = false;

    @FXML
    public void initialize() {

        setupMenu(optionsCutOff);
        setDefaultMenuSelection(optionsCutOff);

        fieldFrequency.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                fieldFrequency.setText(oldValue);
            }
        });

        playBtn.setOnAction(event -> {
            try {
                FileChooser fileChooser = new FileChooser();
                File selectedFile = fileChooser.showOpenDialog(null);

                if (selectedFile == null) {
                    AlertBox.display("Input Error", "No audio file selected.");
                    return;
                }

                inputFile = selectedFile.getAbsolutePath();

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

                if (selectedFilter == null || selectedFilter.isEmpty()) {
                    AlertBox.display("Selection Error", "Please select a filter type.");
                    return;
                }

                File file = new File(inputFile);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = audioStream.getFormat();
                byte[] audioBytes = audioStream.readAllBytes();
                audioStream.close();

                short[] samples = audioFilters.bytesToShorts(audioBytes);
                float sampleRate = format.getSampleRate();

                if ("Low".equals(selectedFilter)) {
                    audioFilters.applyLowPassFilter(samples, sampleRate, userFrequency);
                } else if ("High".equals(selectedFilter)) {
                    audioFilters.applyHighPassFilter(samples, sampleRate, userFrequency);
                } else {
                    AlertBox.display("Filter Error", "Unsupported filter type selected.");
                    return;
                }

                byte[] filteredBytes = audioFilters.shortsToBytes(samples);

                Task<Void> playTask = new Task<>() {
                    @Override
                    protected Void call() throws LineUnavailableException {
                        if (isPlaying) {
                            audioClip.stop();
                            isPlaying = false;
                        } else {
                            audioFilters.playAudio(filteredBytes, format);
                            isPlaying = true;
                        }
                        return null;
                    }
                };
                Thread testThread = new Thread(playTask);
                testThread.start();

                saveBtn.setOnAction(event1 -> {
                    try {
                        audioFilters.saveWavFile(outputFile, filteredBytes, format);
                    } catch (IOException e) {
                        AlertBox.display("Save Error", "Failed to save audio file: " + e.getMessage());
                    }
                });

            } catch (UnsupportedAudioFileException | IOException e) {
                e.printStackTrace();
                AlertBox.display("Error", "An error occurred while processing the audio: " + e.getMessage());
            }
        });


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

    public void getText() {
        // https://www.squash.io/how-to-use-a-regex-to-only-accept-numbers-0-9/
        if (fieldFrequency.getText().matches("^[0-9]+$")) {
            userFrequency = (float) Double.parseDouble(fieldFrequency.getText());
        }
    }

}