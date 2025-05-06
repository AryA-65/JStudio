package org.JStudio.Plugins.Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * HAASStereo is a stereo widening effect that uses the Haas effect.
 */
public class HAASStereo extends Stereoizer {
    private final int delaySamples;
    private StringProperty name = new SimpleStringProperty("HAAS Stereo");

    /**
     * Constructs a HAASStereo processor.
     *
     * @param sampleRate   the sample rate of the audio in Hz
     * @param delaySamples number of samples to delay the right channel
     */
    public HAASStereo(float sampleRate, int delaySamples) {
        super(sampleRate);
        this.delaySamples = delaySamples;
    }

    /**
     * Applies the Haas stereo effect by delaying the right channel.
     *
     * @param mono the mono input signal
     * @return a 2D stereo array
     */
    @Override
    public float[][] process(float[] mono) {
        output = new float[2][mono.length + delaySamples];

        // Left channel: original mono signal
        for (int i = 0; i < mono.length; i++) {
            output[0][i] = mono[i];
        }

        // Right channel: delayed version of mono signal
        for (int i = 0; i < mono.length; i++) {
            output[1][i + delaySamples] = mono[i];
        }

        return output;
    }
}
