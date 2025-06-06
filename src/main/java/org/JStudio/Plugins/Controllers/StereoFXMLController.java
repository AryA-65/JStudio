package org.JStudio.Plugins.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.JStudio.Plugins.Models.*;
import org.JStudio.Utils.AlertBox;

import javax.sound.sampled.*;
import java.io.*;

/**
 * Class that determines the logic of stereo plugin
 */
public class StereoFXMLController extends Plugin {
    private Stage stage;
    @FXML private Button playButton, exportButton;
    @FXML private RadioButton HAASStereoRadio, LFOStereoRadio, InvertedStereoRadio;
    @FXML private TextField delaySample, fileName;

    private ToggleGroup group;
    private String inputFile;
    private AudioFormat format;
    private short[] samples;
    private float sampleRate;
    private byte[] filteredBytes;
    private byte[] audioBytes;
    private int delay;
    private float[][] processedStereoOutput;

    /**
     * Method that implements the logic of the controller
     */
    @FXML
    public void initialize() {
        group = new ToggleGroup();
        HAASStereoRadio.setToggleGroup(group);
        LFOStereoRadio.setToggleGroup(group);
        InvertedStereoRadio.setToggleGroup(group);

        importAudio();
        playButton.setOnAction(event -> applyStereoEffect());
        exportButton.setOnAction(event -> {
            stopAudio();
            getProcessedAudio();
        });
    }

    /**
     * Import the audio file
     */
    private void importAudio() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Mono WAV File");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("WAV Files", "*.wav"),
                    new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"),
                    new FileChooser.ExtensionFilter("All Audio Files", "*.wav", "*.mp3"));
            File file = fileChooser.showOpenDialog(null);
            if (file == null) return;

            inputFile = file.getAbsolutePath();
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            format = audioStream.getFormat();

            if (format.getChannels() != 1) {
                AlertBox.display("Import Error", "Only mono audio files are supported.");
                return;
            }

            sampleRate = format.getSampleRate();
            audioBytes = audioStream.readAllBytes();
            samples = bytesToShorts(audioBytes);
            audioStream.close();

            AlertBox.display("Import Successful", "Successfully imported: " + file.getName());
        } catch (Exception e) {
            AlertBox.display("Import Error", "Failed to import audio:\n" + e.getMessage());
        }
    }

    /**
     * Method that applies the stereo affect
     */
    private void applyStereoEffect() {
        stopAudio();
        if (samples == null || format == null) {
            AlertBox.display("Apply Error", "No audio loaded. Please import a mono WAV file first.");
            return;
        }

        float[] mono = shortsToFloats(samples);
        Stereoizer stereoizer;

        if (HAASStereoRadio.isSelected()) {
            delay = parseDelaySample();
            stereoizer = new HAASStereo(sampleRate, delay);
        } else if (LFOStereoRadio.isSelected()) {
            stereoizer = new LFOStereo(sampleRate, 1.0f);
        } else if (InvertedStereoRadio.isSelected()) {
            stereoizer = new InvertedStereo(sampleRate);
        } else {
            AlertBox.display("Effect Selection", "Please select a stereo effect.");
            return;
        }

        try {
            processedStereoOutput = stereoizer.process(mono); // <-- Store processed float[][]
            short[] interleaved = interleaveStereo(processedStereoOutput);
            filteredBytes = shortsToBytes(interleaved);
            playAudio(filteredBytes);
        } catch (Exception e) {
            AlertBox.display("Processing Error", "Failed to apply stereo effect:\n" + e.getMessage());
        }
    }

    public byte[] getProcessedAudio() {
        if (filteredBytes == null || filteredBytes.length == 0) {
            AlertBox.display("Export Error", "No processed audio to export.");
            return null;
        }
        return filteredBytes;
    }

    /**
     * Parses the delay sample value from the associated input field.
     *
     * @return The delay value in samples as an integer.
     */
    private int parseDelaySample() {
        try {
            return Integer.parseInt(delaySample.getText());
        } catch (NumberFormatException e) {
            AlertBox.display("Invalid Delay", "Invalid delay value. Defaulting to 20 samples.");
            return 20;
        }
    }

    /**
     * Interleaves two stereo channels (left and right) into a single array of short values.
     *
     * @param stereo A 2D float array representing the left and right stereo channels.
     *               stereo[0] is the left channel and stereo[1] is the right channel.
     * @return A short array containing the interleaved stereo samples.
     */
    private short[] interleaveStereo(float[][] stereo) {
        int length = Math.min(stereo[0].length, stereo[1].length);
        short[] interleaved = new short[length * 2];
        for (int i = 0; i < length; i++) {
            interleaved[2 * i] = (short) (Math.max(-1.0f, Math.min(1.0f, stereo[0][i])) * 32767);
            interleaved[2 * i + 1] = (short) (Math.max(-1.0f, Math.min(1.0f, stereo[1][i])) * 32767);
        }
        return interleaved;
    }

    /**
     * Method that sets the stage
     * @param stage the stage of the plugin
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setOnCloseRequest(event -> stopAudio());
    }
}
