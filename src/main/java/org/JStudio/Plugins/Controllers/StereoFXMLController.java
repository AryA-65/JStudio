package org.JStudio.Plugins.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import org.JStudio.Plugins.Models.*;

import javax.sound.sampled.*;
import java.io.*;

public class StereoFXMLController {
    @FXML private Button importButton, applyButton, exportButton;
    @FXML private RadioButton HAASStereoRadio, LFOStereoRadio, InvertedStereoRadio;
    @FXML private TextField delaySample, fileName;

    private ToggleGroup group;
    private String inputFile;
    private AudioFormat format;
    private short[] samples;
    private float sampleRate;
    private byte[] filteredBytes;

    @FXML
    public void initialize() {
        group = new ToggleGroup();
        HAASStereoRadio.setToggleGroup(group);
        LFOStereoRadio.setToggleGroup(group);
        InvertedStereoRadio.setToggleGroup(group);

        importButton.setOnAction(event -> importAudio());
        applyButton.setOnAction(event -> applyStereoEffect());
        exportButton.setOnAction(event -> exportAudio());
    }

    private void importAudio() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Mono WAV File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("WAV files", "*.wav"));
            File file = fileChooser.showOpenDialog(null);
            if (file == null) return;

            inputFile = file.getAbsolutePath();
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            format = audioStream.getFormat();

            if (format.getChannels() != 1) {
                System.err.println("Only mono audio files are supported.");
                return;
            }

            sampleRate = format.getSampleRate();
            byte[] audioBytes = audioStream.readAllBytes();
            samples = bytesToShorts(audioBytes);
            audioStream.close();

            System.out.println("Successfully imported: " + inputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyStereoEffect() {
        if (samples == null || format == null) {
            System.err.println("No audio loaded.");
            return;
        }

        float[] mono = shortsToFloats(samples);
        Stereoizer stereoizer;

        if (HAASStereoRadio.isSelected()) {
            int delay = parseDelaySample();
            stereoizer = new HAASStereo(sampleRate, delay);
        } else if (LFOStereoRadio.isSelected()) {
            stereoizer = new LFOStereo(sampleRate, 1.0f); // 1 Hz LFO
        } else if (InvertedStereoRadio.isSelected()) {
            stereoizer = new InvertedStereo(sampleRate);
        } else {
            System.err.println("No effect selected.");
            return;
        }

        float[][] stereoOutput = stereoizer.process(mono);
        short[] interleaved = interleaveStereo(stereoOutput);
        filteredBytes = shortsToBytes(interleaved);

        System.out.println("Effect applied successfully.");
    }

    private void exportAudio() {
        if (filteredBytes == null || format == null) {
            System.err.println("No processed audio to export.");
            return;
        }

        try {
            AudioFormat stereoFormat = new AudioFormat(
                    format.getEncoding(),
                    sampleRate,
                    16,
                    2,
                    4,
                    sampleRate,
                    format.isBigEndian()
            );

            File outFile = new File(fileName.getText() + ".wav");
            ByteArrayInputStream bais = new ByteArrayInputStream(filteredBytes);
            AudioInputStream stereoStream = new AudioInputStream(bais, stereoFormat, filteredBytes.length / stereoFormat.getFrameSize());
            AudioSystem.write(stereoStream, AudioFileFormat.Type.WAVE, outFile);

            System.out.println("Successfully exported to: " + outFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int parseDelaySample() {
        try {
            return Integer.parseInt(delaySample.getText());
        } catch (NumberFormatException e) {
            System.err.println("Invalid delay. Defaulting to 20 samples.");
            return 20;
        }
    }

    private short[] bytesToShorts(byte[] bytes) {
        short[] shorts = new short[bytes.length / 2];
        for (int i = 0; i < shorts.length; i++) {
            shorts[i] = (short) ((bytes[2 * i + 1] << 8) | (bytes[2 * i] & 0xFF));
        }
        return shorts;
    }

    private byte[] shortsToBytes(short[] shorts) {
        byte[] bytes = new byte[shorts.length * 2];
        for (int i = 0; i < shorts.length; i++) {
            bytes[2 * i] = (byte) (shorts[i] & 0xFF);
            bytes[2 * i + 1] = (byte) ((shorts[i] >> 8) & 0xFF);
        }
        return bytes;
    }

    private float[] shortsToFloats(short[] shorts) {
        float[] floats = new float[shorts.length];
        for (int i = 0; i < shorts.length; i++) {
            floats[i] = shorts[i] / 32768f;
        }
        return floats;
    }

    private short[] interleaveStereo(float[][] stereo) {
        int length = Math.min(stereo[0].length, stereo[1].length);
        short[] interleaved = new short[length * 2];
        for (int i = 0; i < length; i++) {
            interleaved[2 * i] = (short) (Math.max(-1.0f, Math.min(1.0f, stereo[0][i])) * 32767);
            interleaved[2 * i + 1] = (short) (Math.max(-1.0f, Math.min(1.0f, stereo[1][i])) * 32767);
        }
        return interleaved;
    }
}
