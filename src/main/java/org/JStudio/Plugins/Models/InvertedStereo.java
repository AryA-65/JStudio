package org.JStudio.Plugins.Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * InvertedStereo is a stereo effect that outputs the original mono signal to the left channel and the phase-inverted signal to the right channel.
 */
public class InvertedStereo extends Stereoizer {
    private StringProperty name = new SimpleStringProperty("Inverted Stereo");

    /**
     * Constructs an InvertedStereo processor.
     *
     * @param sampleRate the audio sample rate in Hz
     */
    public InvertedStereo(float sampleRate) {
        super(sampleRate);
    }

    /**
     * Processes a mono input array into a stereo output where:
     *
     * @param mono the mono input signal
     * @return a 2D stereo array with phase-inverted right channel
     */
    @Override
    public float[][] process(float[] mono) {
        output = new float[2][mono.length];

        for (int i = 0; i < mono.length; i++) {
            output[0][i] = mono[i];     // Left channel: original
            output[1][i] = -mono[i];    // Right channel: inverted
        }

        return output;
    }
}
