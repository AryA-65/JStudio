package org.JStudio.Plugins.Models;


import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class audioFilters {

    /*
    removes high frequencies
     */
    public static void applyLowPassFilter(short[] samples, float sampleRate, float cutoffFreq) {
        double RC = 1.0 / (2 * Math.PI * cutoffFreq);
        double dt = 1.0 / sampleRate;
        double alpha = dt / (RC + dt);
        short prevSample = 0;
        for (int i = 0; i < samples.length; i++) {
            samples[i] = (short) ((alpha * samples[i]) + ((1 - alpha) * prevSample));
            prevSample = samples[i];
        }
    }

    /*
    removes low frequencies
     */
    public static void applyHighPassFilter(short[] samples, float sampleRate, float cutoffFreq) {
        double RC = 1.0 / (2 * Math.PI * cutoffFreq);
        double dt = 1.0 / sampleRate;
        double alpha = RC / (RC + dt);
        short prevSample = 0;
        short prevFiltered = 0;
        for (int i = 0; i < samples.length; i++) {
            short newSample = (short) (alpha * (prevFiltered + samples[i] - prevSample));
            prevSample = samples [i];
            prevFiltered = newSample;
            samples[i] = newSample;
        }
    }

    public static short[] bytesToShorts(byte[] audioBytes) {
        short[] samples = new short[audioBytes.length / 2];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = (short) ((audioBytes[2 * i + 1] << 8) | (audioBytes[2 * i] & 0xFF));
        }
        return samples;
    }

    // Convert short array back to 16-bit PCM byte array
    public static byte[] shortsToBytes(short[] samples) {
        byte[] audioBytes = new byte[samples.length * 2];
        for (int i = 0; i < samples.length; i++) {
            audioBytes[2 * i] = (byte) (samples[i] & 0xFF);
            audioBytes[2 * i + 1] = (byte) ((samples[i] >> 8) & 0xFF);
        }
        return audioBytes;
    }

    // Save byte[] to WAV file
    public static void saveWavFile(String filename, byte[] audioBytes, AudioFormat format) throws IOException {
        File file = new File(filename);
        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        AudioInputStream audioStream = new AudioInputStream(bais, format, audioBytes.length / format.getFrameSize());
        AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, file);
    }

    // Play audio from byte[]
    public static void playAudio(byte[] audioBytes, AudioFormat format) throws LineUnavailableException {
        SourceDataLine line = AudioSystem.getSourceDataLine(format);
        line.open(format);
        line.start();
        line.write(audioBytes, 0, audioBytes.length);
        line.drain();
        line.close();
    }
}
