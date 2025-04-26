package org.JStudio.Plugins.Controllers;


import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private final Map<MenuButton, String> filterType = new HashMap<>();
    public String inputFile;
    @FXML
    private TextField fieldFrequency;
    @FXML
    private MenuButton optionsCutOff;
    @FXML
    private Button saveBtn, playBtn;
    private float userFrequency;
    private String selectedFilter;

    public byte[] readSamples;
    public short[] appliedSamples;
    @FXML
    public void initialize() {
        setupMenu(optionsCutOff);
        setDefaultMenuSelection(optionsCutOff);

        fieldFrequency.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                fieldFrequency.setText(oldValue);
            }
        });

        convertAudioFileToByteArray();

        playBtn.setOnAction(event -> {
            getText();
            stopAudio();
            try {
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
                readSamples = audioStream.readAllBytes();
                audioStream.close();

                appliedSamples = audioFilters.bytesToShorts(readSamples);
                float sampleRate = format.getSampleRate();

                if ("Low".equals(selectedFilter)) {
                    audioFilters.applyLowPassFilter(appliedSamples, sampleRate, userFrequency);
                } else if ("High".equals(selectedFilter)) {
                    audioFilters.applyHighPassFilter(appliedSamples, sampleRate, userFrequency);
                } else {
                    AlertBox.display("Filter Error", "Unsupported filter type selected.");
                    return;
                }

                playAudio(shortToByte(appliedSamples));
            } catch (UnsupportedAudioFileException | IOException e) {
                AlertBox.display("Error", "An error occurred while processing the audio: " + e.getMessage());
            }
        });

        saveBtn.setOnAction(event1 -> {
            stopAudio();
            getProcessedAudio();
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

    public double[] getProcessedAudio() {
        if (appliedSamples == null || appliedSamples.length == 0) {
            AlertBox.display("Export Error", "No processed audio to export.");
            return null;
        }
        return shortToDouble(appliedSamples);
    }

}