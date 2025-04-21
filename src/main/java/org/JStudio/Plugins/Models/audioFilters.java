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


    /**
     * BiquadFilter filter = new BiquadFilter();
     * filter.lowPass(1000, 0.707, 44100);  // Low-pass filter at 1000Hz, Q=0.707, sample rate = 44.1kHz
     *
     * double[] signal = {1.0, 0.5, 0.25, 0.0};  // Example input signal
     * for (int i = 0; i < signal.length; i++) {
     *     double filteredSample = filter.apply(signal[i]);
     *     System.out.println(filteredSample);
     * }
     */

    /**
     * Inspired from https://webaudio.github.io/Audio-EQ-Cookbook/audio-eq-cookbook.html
         and https://webaudio.github.io/Audio-EQ-Cookbook/Audio-EQ-Cookbook.txt
     */
    public class BiquadFilter {

        // Filter coefficients
        private double b0, b1, b2;
        private double a0, a1, a2;

        // Delayed input and output samples
        private double x1, x2; // Previous input samples
        private double y1, y2; // Previous output samples

        // Constructor
        public BiquadFilter() {
            // Initialize delay buffers to zero
            x1 = x2 = y1 = y2 = 0.0;
        }

        // Apply the filter to a single sample
        public double apply(double x0) {
            double y0 = b0 * x0 + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2;

            // Shift the delay buffers
            x2 = x1;
            x1 = x0;
            y2 = y1;
            y1 = y0;

            return y0;
        }

        // Low-pass filter (LPF)
        public void lowPass(double freq, double Q, double sampleRate) {
            double w0 = 2 * Math.PI * freq / sampleRate;
            double cosW0 = Math.cos(w0);
            double alpha = Math.sin(w0) / (2 * Q);

            b0 = (1 - cosW0) / 2;
            b1 = 1 - cosW0;
            b2 = (1 - cosW0) / 2;
            a0 = 1 + alpha;
            a1 = -2 * cosW0;
            a2 = 1 - alpha;

            normalize();
        }

        // High-pass filter (HPF)
        public void highPass(double freq, double Q, double sampleRate) {
            double w0 = 2 * Math.PI * freq / sampleRate;
            double cosW0 = Math.cos(w0);
            double alpha = Math.sin(w0) / (2 * Q);

            b0 = (1 + cosW0) / 2;
            b1 = -(1 + cosW0);
            b2 = (1 + cosW0) / 2;
            a0 = 1 + alpha;
            a1 = -2 * cosW0;
            a2 = 1 - alpha;

            normalize();
        }

        // Band-pass filter (BPF)
        public void bandPass(double freq, double Q, double sampleRate) {
            double w0 = 2 * Math.PI * freq / sampleRate;
            double alpha = Math.sin(w0) / (2 * Q);
            double cosW0 = Math.cos(w0);

            b0 = alpha;
            b1 = 0;
            b2 = -alpha;
            a0 = 1 + alpha;
            a1 = -2 * cosW0;
            a2 = 1 - alpha;

            normalize();
        }

        // Band-stop filter (Notch)
        public void bandStop(double freq, double Q, double sampleRate) {
            double w0 = 2 * Math.PI * freq / sampleRate;
            double alpha = Math.sin(w0) / (2 * Q);
            double cosW0 = Math.cos(w0);

            b0 = 1;
            b1 = -2 * cosW0;
            b2 = 1;
            a0 = 1 + alpha;
            a1 = -2 * cosW0;
            a2 = 1 - alpha;

            normalize();
        }

        // Normalize the coefficients (so that a0 = 1)
        private void normalize() {
            b0 /= a0;
            b1 /= a0;
            b2 /= a0;
            a1 /= a0;
            a2 /= a0;
            a0 = 1.0;
        }

        // Optional: reset the filter history
        public void reset() {
            x1 = x2 = y1 = y2 = 0.0;
        }
    }
}


