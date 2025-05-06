package org.JStudio.Plugins;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Clipping audio distortion that has 4 different effects
 */
public class ClippingDistortion extends Distortion {
    private float factor;
    private TYPE type;

    /**
     * type of distortion
     */
    public enum TYPE {
        HARD, SOFT, EXPO, REVERSE
    }

    /**
     * initializing the plugin
     * @param gain
     * @param mix
     * @param factor
     * @param type
     */
    public ClippingDistortion(float gain, float mix, float factor, TYPE type) {
        super(gain, mix);
        this.factor = factor;
        this.type = type;
    }

    /**
     * applying the clipping to the sample
     * @param sample
     * @return
     */
    private float applyClipping(float sample) {
        return switch (type) {
            case HARD -> Math.max(-factor, Math.min(factor, sample));
            case SOFT -> (float) (Math.tanh(factor * sample) / Math.tanh(factor));
            case EXPO -> (float) (sample / (1 + factor * Math.abs(sample)));
            case REVERSE -> Math.abs(sample) > factor ? -sample : sample;
        };
    }

    /**
     *
     * @param input input buffer (float)
     * @return returning the processed audio in mono sound
     */
    @Override
    public float[] processMono(float[] input) {
        return new float[0];
    }

    /**
     *
     * @param input input buffer (stereo float)
     * @return returns the processed audio in stereo sound
     */
    @Override
    public float[][] processStereo(float[][] input) {
        float[][] output = new float[2][input[0].length];
        for (int c = 0; c < 2; c++) {
            for (int i = 0; i < input[c].length; i++) {
                float dry = input[c][i];
                float wet = applyClipping(dry);
                output[c][i] = dry * (1 - mix) + wet * mix;
            }
        }
        return output;
    }

    /**
     * @return returns the name of the plugin (set manually)
     */
    @Override
    public StringProperty getName() {
        return new SimpleStringProperty("Clipping Distortion");
    }
}
