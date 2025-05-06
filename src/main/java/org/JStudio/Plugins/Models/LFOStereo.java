package org.JStudio.Plugins.Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * LFOStereo is a stereoization effect that modulates the amplitude of the left and right channels using a low-frequency oscillator (LFO).
 */
public class LFOStereo extends Stereoizer {
    private StringProperty name = new SimpleStringProperty("LFO Stereo");
    private float lfoFrequency;

    /**
     * Constructs an LFOStereo processor.
     *
     * @param sampleRate   the sample rate of the audio (in Hz)
     * @param lfoFrequency the LFO frequency (in Hz)
     */
    public LFOStereo(float sampleRate, float lfoFrequency) {
        super(sampleRate);
        this.lfoFrequency = lfoFrequency;
    }

    /**
     * Applies LFO-based stereo to a mono input signal.
     *
     * @param mono the mono input signal
     * @return a 2D float array representing stereo output [left][right]
     */
    @Override
    public float[][] process(float[] mono) {
        output = new float[2][mono.length];

        for (int i = 0; i < mono.length; i++) {
            // Compute LFO value using a sine wave
            float lfo = (float) Math.sin(2 * Math.PI * lfoFrequency * i / sampleRate);

            // Apply LFO amplitude modulation with width factor
            output[0][i] = mono[i] * (1.0f + lfo * width);  // Left channel
            output[1][i] = mono[i] * (1.0f - lfo * width);  // Right channel
        }

        return output;
    }
}
