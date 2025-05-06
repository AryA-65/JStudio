package org.JStudio.Plugins.Models;

/**
 * Abstract class that defines a the logic for converting mono audio signals into stereo

 */
public abstract class Stereoizer {

    protected float sampleRate;
    protected float width = 1.0f;
    public float[][] output;

    /**
     * Constructs a Stereoizer with the given sample rate.
     *
     * @param sampleRate the sample rate in Hz
     */
    Stereoizer(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * Sets the stereo width parameter.
     *
     * @param width a float from 0.0 (mono) to 1.0
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Processes a mono audio buffer and converts it into stereo using the specific stereoization method
     *
     * @param mono a mono audio buffer
     * @return a 2D stereo buffer: output[0] = left, output[1] = right
     */
    public abstract float[][] process(float[] mono);

}
