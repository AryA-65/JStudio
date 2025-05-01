package org.JStudio.Plugins.Models;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class audioFilters {
    private StringProperty name = new SimpleStringProperty("Audio Filters");

    private void amplifyAudio(double amplitudeFactor, String source) {
        new Thread(() -> {
            try {
                File audioFile = new File(source);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioStream.getFormat();
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = audioStream.read(buffer)) != -1) { // read raw audio data into the buffer
                    for (int i = 0; i < bytesRead - 1; i += 2) { // loop through the buffer 2 bytes at a time
                        short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF)); // combines low and high byte(+1)

                        // Apply amplitude factor
                        sample = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, sample * amplitudeFactor));

                        // Convert back to bytes
                        buffer[i] = (byte) (sample & 0xFF);
                        buffer[i + 1] = (byte) ((sample >> 8) & 0xFF);
                    }

                    line.write(buffer, 0, bytesRead);
                }

                line.drain();
                line.close();
                audioStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

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

    public class BiquadFilter {

        // These are calculated based on filter type and configuration
        private double b0, b1, b2; // Feedforward coefficients
        private double a0, a1, a2; // Feedback coefficients

        // Store the two most recent input (x) and output (y) samples
        private double x1, x2; // Previous input samples (x[n-1], x[n-2])
        private double y1, y2; // Previous output samples (y[n-1], y[n-2])

        private double sampleRate = 44100;

        /**
         * Constructor initializes filter history (delay buffers) to zero.
         */
        public BiquadFilter() {
            x1 = x2 = y1 = y2 = 0.0;
        }

        /**
         * Apply the filter to a single audio sample.
         *
         * @param x0 The current input sample
         * @return The filtered output sample
         */
        public double apply(double x0) {
            // Apply the biquad filter equation:
            // y[n] = b0*x[n] + b1*x[n-1] + b2*x[n-2] - a1*y[n-1] - a2*y[n-2]
            double y0 = b0 * x0 + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2;

            // Update the delay buffers for next sample
            x2 = x1;
            x1 = x0;
            y2 = y1;
            y1 = y0;

            return y0;
        }

        /**
         * Configure the filter as a low-pass filter (LPF).
         *
         * @param freq Cutoff frequency in Hz
         * @param Q Quality factor (controls the resonance/steepness)
         */
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

        /**
         * Configure the filter as a high-pass filter (HPF).
         *
         * @param freq Cutoff frequency in Hz
         * @param Q Quality factor (controls the resonance/steepness)
         */
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

        /**
         * Configure the filter as a band-pass filter (BPF).
         *
         * @param freq Center frequency in Hz
         * @param Q Quality factor (Q = center frequency / bandwidth)
         */
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

        /**
         * Configure the filter as a band-stop filter (Notch).
         *
         * @param freq Center frequency in Hz (the frequency to remove)
         * @param Q Quality factor (Q = center frequency / bandwidth)
         */
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

        /**
         * Normalize the filter coefficients so that a0 becomes 1.
         * This ensures stability and proper gain scaling.
         */
        private void normalize() {
            b0 /= a0;
            b1 /= a0;
            b2 /= a0;
            a1 /= a0;
            a2 /= a0;
            a0 = 1.0; // a0 is now normalized
        }

        /**
         * Resets the filter history (delay buffers) to zero.
         * Call this if you're starting a new stream or input.
         */
        public void reset() {
            x1 = x2 = y1 = y2 = 0.0;
        }

        public static void applyBiquadLowPassFilter(short[] samples, float cutoffFreq, float q, double sampleRate) {
            BiquadFilter filter = new audioFilters().new BiquadFilter(); // Create an instance
            filter.lowPass(cutoffFreq, q, sampleRate);
            applyBiquad(samples, filter);
        }

        public static void applyBiquadHighPassFilter(short[] samples, float cutoffFreq, float q, double sampleRate) {
            BiquadFilter filter = new audioFilters().new BiquadFilter();
            filter.highPass(cutoffFreq, q, sampleRate);
            applyBiquad(samples, filter);
        }

        public static void applyBiquadBandPassFilter(short[] samples, float centerFreq, float q, double sampleRate) {
            BiquadFilter filter = new audioFilters().new BiquadFilter();
            filter.bandPass(centerFreq, q, sampleRate);
            applyBiquad(samples, filter);
        }

        public static void applyBiquadBandStopFilter(short[] samples, float centerFreq, float q, double sampleRate) {
            BiquadFilter filter = new audioFilters().new BiquadFilter();
            filter.bandStop(centerFreq, q, sampleRate);
            applyBiquad(samples, filter);
        }

        // Shared utility method
        private static void applyBiquad(short[] samples, BiquadFilter filter) {
            filter.reset();
            for (int i = 0; i < samples.length; i++) {
                double input = samples[i];
                double output = filter.apply(input);
                output = Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, output)); // Clamp
                samples[i] = (short) output;
            }
        }
    }

}


