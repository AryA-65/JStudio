package org.JStudio.Plugins;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static org.JStudio.Plugins.audioFilters.*;

public class audioFilterMain {
    public static void main(String[] args) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        String inputFile = "src/main/resources/test_inputs_music.wav";
        String outputFile = "filtered_output.wav";

        // Read WAV file
        File file = new File(inputFile);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioStream.getFormat();
        byte[] audioBytes = audioStream.readAllBytes();
        audioStream.close();

        // Convert byte[] to short[]
        short[] samples = audioFilters.bytesToShorts(audioBytes);

        // Apply your own low-pass filter
        float sampleRate = format.getSampleRate();
        float cutoffFrequency = 1000f; // in Hz
        applyHighPassFilter(samples, sampleRate, cutoffFrequency);

        // Convert short[] back to byte[]
        byte[] filteredBytes = shortsToBytes(samples);

        // Save filtered audio to new WAV file
        saveWavFile(outputFile, filteredBytes, format);
        System.out.println("Filtered audio saved as: " + outputFile);

        // Play filtered sound
        playAudio(filteredBytes, format);
    }
}
