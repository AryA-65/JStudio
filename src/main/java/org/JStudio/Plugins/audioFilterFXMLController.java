package org.JStudio.Plugins;


import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.JStudio.Plugins.Models.audioFilters;

public class audioFilterFXMLController {
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

        playBtn.setOnAction(event -> {
            try {
                FileChooser fileChooser = new FileChooser();
                inputFile = fileChooser.showOpenDialog(null).getAbsolutePath();
                getText();
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
                }

                byte[] filteredBytes = audioFilters.shortsToBytes(samples);

                Thread testThread = null;

                Task<Void> playTask = new Task<>() {
                    // https://stackoverflow.com/questions/24924414/javafx-task-ending-and-javafx-threading
                    @Override
                    protected Void call() throws Exception {
                        if (isPlaying) {
                            // If playing, stop audio playback
                            System.out.println("Playing");
                            audioClip.stop();
                            isPlaying = false;
//                            testThread.stop();
                            System.out.println("Stopped");
                        } else {
                            // Start playing the filtered audio in background
                            audioFilters.playAudio(filteredBytes, format);
                            isPlaying = true;
                        }
                        return null;
                    }
                };
                testThread = new Thread(playTask);
                testThread.start();



                saveBtn.setOnAction(event1 -> {
                    try {
                        audioFilters.saveWavFile(outputFile, filteredBytes, format);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
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