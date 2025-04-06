package org.JStudio.Plugins;


public class audioFilters {

    /*
    removes high frequencies
     */
    private static void applyLowPassFilter(short[] samples, float sampleRate, float cutoffFreq) {
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
    private static void applyHighPassFilter(short[] samples, float sampleRate, float cutoffFreq) {
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

}
