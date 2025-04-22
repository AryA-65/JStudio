package org.JStudio.Plugins;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import org.JStudio.Plugins.Models.audioFilters;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;

public class ButterworthFxmlController {
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

                System.out.println("succesfully imported");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        applyButton.setOnAction(event -> {
            try {
                if (samples == null || samples.length == 0) {
                    System.out.println("No audio loaded.");
                    return;
                }

                float freq = Float.parseFloat(frequencyField.getText());
                float q = Float.parseFloat(qualityField.getText());

                RadioButton selected = (RadioButton) group.getSelectedToggle();
                if (selected == null) {
                    System.out.println("No filter type selected.");
                    return;
                }

                switch (selected.getId()) {
                    case "lowPassRadio":
                        audioFilters.BiquadFilter.applyBiquadLowPassFilter(samples, freq, q, sampleRate);
                        System.out.println("effect applied");
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
                        System.out.println("Invalid filter type.");
                }

                filteredBytes = audioFilters.shortsToBytes(samples);

            } catch (NumberFormatException e) {
                System.out.println("Invalid frequency or Q value.");
            } catch (Exception e) {
                e.printStackTrace();
            }

            exportButton.setOnAction(event1 -> {
                try {
                    audioFilters.saveWavFile(outputFileName.getText(), filteredBytes, format);
                    System.out.println("succesfully exported");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
