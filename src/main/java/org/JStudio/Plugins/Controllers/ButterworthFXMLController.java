package org.JStudio.Plugins.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import org.JStudio.Plugins.Models.audioFilters;
import org.JStudio.Utils.AlertBox;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;

public class ButterworthFXMLController {
    @FXML
    private Button importButton, applyButton, exportButton;

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

    public void initialize() {
        group = new ToggleGroup();
        lowPassRadio.setToggleGroup(group);
        highPassRadio.setToggleGroup(group);
        bandPassRadio.setToggleGroup(group);
        bandStopRadio.setToggleGroup(group);

        frequencyField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                /*
                \\d* all entries before . numbers
                (\\.\\d*)? all entries after . numbers
                 */
                frequencyField.setText(oldValue);
            }
        });

        qualityField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                qualityField.setText(oldValue);
            }
        });

        importButton.setOnAction(event -> {
            try {
                FileChooser fileChooser = new FileChooser();
                inputFile = fileChooser.showOpenDialog(null).getAbsolutePath();
                File file = new File(inputFile);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                format = audioStream.getFormat();
                byte[] audioBytes = audioStream.readAllBytes();
                audioStream.close();

                samples = audioFilters.bytesToShorts(audioBytes);
                sampleRate = format.getSampleRate();

                AlertBox.display("Success", "Audio file successfully imported.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        applyButton.setOnAction(event -> {
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
                System.out.println("Effect applied.");

            } catch (NumberFormatException e) {
                AlertBox.display("Input Error", "Invalid number format in Frequency or Q field.");
            } catch (Exception e) {
                e.printStackTrace();
                AlertBox.display("Error", "An unexpected error occurred: " + e.getMessage());
            }
        });

    }
}
