package org.JStudio.Plugins.Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class that determines all the audio filters
 */
public class audioFilters {
    private StringProperty name = new SimpleStringProperty("Audio Filters");

    /**
     * Method that remove high frequencies given a cut-off frequency
     * @param samples the array that the filter will be applied to
     * @param sampleRate the sample rate of the audio file
     * @param cutoffFreq the given cut-off frequency
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

    /**
     * Method that remove low frequencies given a cut-off frequency
     * @param samples the array that the filter will be applied to
     * @param sampleRate the sample rate of the audio file
     * @param cutoffFreq the given cut-off frequency
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

    /**
     * Convert byte array back to 16-bit PCM short array
     * @param audioBytes the given array of inputs
     * @return and array of shorts
     */
    public static short[] bytesToShorts(byte[] audioBytes) {
        short[] samples = new short[audioBytes.length / 2];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = (short) ((audioBytes[2 * i + 1] << 8) | (audioBytes[2 * i] & 0xFF));
        }
        return samples;
    }

    //

    /**
     * Convert short array back to 16-bit PCM byte array
     * @param samples the given array of inputs
     * @return and array of bytes
     */
    public static byte[] shortsToBytes(short[] samples) {
        byte[] audioBytes = new byte[samples.length * 2];
        for (int i = 0; i < samples.length; i++) {
            audioBytes[2 * i] = (byte) (samples[i] & 0xFF);
            audioBytes[2 * i + 1] = (byte) ((samples[i] >> 8) & 0xFF);
        }
        return audioBytes;
    }


    /**
     * Parent class that defines biquad filters
     */
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

        /**
         * Applies a biquad low-pass filter to the given audio sample array
         * This filter removes high-frequency components above the specified cutoff frequency
         *
         * @param samples     The input/output array of 16-bit PCM audio samples.
         * @param cutoffFreq  The cutoff frequency in Hz above which frequencies are attenuated.
         * @param q           The quality factor (Q), which controls the filter's resonance.
         * @param sampleRate  The sample rate of the audio data in Hz (e.g., 44100).
         */
        public static void applyBiquadLowPassFilter(short[] samples, float cutoffFreq, float q, double sampleRate) {
            BiquadFilter filter = new audioFilters().new BiquadFilter(); // Create an instance
            filter.lowPass(cutoffFreq, q, sampleRate);
            applyBiquad(samples, filter);
        }

        /**
         * Applies a biquad high-pass filter to the given audio sample array.
         * This filter attenuates frequencies below the cutoff frequency and allows higher frequencies
         *
         * @param samples     The input/output array
         * @param cutoffFreq  The cutoff frequency in Hz
         * @param q           The quality factor (Q), which affects the sharpness of the cutoff slope
         * @param sampleRate  The sample rate of the audio data in Hz
         */
        public static void applyBiquadHighPassFilter(short[] samples, float cutoffFreq, float q, double sampleRate) {
            BiquadFilter filter = new audioFilters().new BiquadFilter();
            filter.highPass(cutoffFreq, q, sampleRate);
            applyBiquad(samples, filter);
        }

        /**
         * Applies a biquad band-pass filter to the given audio sample array.
         * This filter allows frequencies around the center frequency to pass through and reduces all the others
         *
         * @param samples     The input/output array
         * @param centerFreq  The center frequency in Hz
         * @param q           The quality factor (Q), which determines the bandwidth around the center frequency
         * @param sampleRate  The sample rate of the audio data in Hz
         */
        public static void applyBiquadBandPassFilter(short[] samples, float centerFreq, float q, double sampleRate) {
            BiquadFilter filter = new audioFilters().new BiquadFilter();
            filter.bandPass(centerFreq, q, sampleRate);
            applyBiquad(samples, filter);
        }

        /**
         * Applies a biquad band-stop filter (notch filter) to the given audio sample array.
         * This filter reduces frequencies around the specified center frequency, but leaves other frequencies unaffected.
         *
         * @param samples     The input/output array
         * @param centerFreq  The center frequency in Hz
         * @param q           The quality factor (Q), which determines the width of the notch
         * @param sampleRate  The sample rate of the audio data in Hz
         */
        public static void applyBiquadBandStopFilter(short[] samples, float centerFreq, float q, double sampleRate) {
            BiquadFilter filter = new audioFilters().new BiquadFilter();
            filter.bandStop(centerFreq, q, sampleRate);
            applyBiquad(samples, filter);
        }

        /**
         * Applies the given biquad filter to an array of 16-bit PCM audio samples.
         * This method processes each sample through the provided filter instance and
         * updates the samples in-place with the filtered output.
         *
         * @param samples The input/output array of audio samples to be filtered
         * @param filter  The configured BiquadFilter instance to apply
         */
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


