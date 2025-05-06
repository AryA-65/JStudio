package org.JStudio.Plugins.Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class that applies a third-order polynomial distortion effect to an audio signal, introducing harmonic overtones.
 */
public class HarmonicDistortion extends Distortion {
    /**
     * Constructs a new HarmonicDistortion plugin with the specified gain and mix values.
     *
     * @param gain the gain factor to be applied to the input before distortion
     * @param mix  the mix ratio between dry (original) and wet (processed) signals, from 0.0 to 1.0
     */
    public HarmonicDistortion(float gain, float mix) {
        super(gain, mix);
    }

    /**
     * Processes a mono audio signal using the harmonic distortion algorithm
     * Gain is applied first, then the cubic non-linear transformation, finally the dry/wet mix
     *
     * @param inputData the input mono audio buffer
     * @return a new mono buffer with harmonic distortion applied
     */
    @Override
    public float[] processMono(float[] inputData) {
        float[] gainedData = applyGain(inputData);
        float[] output = new float[gainedData.length];
        for (int i = 0; i < gainedData.length; i++) {
            float x = gainedData[i];
            output[i] = (float) (x - (1.0f / 3.0f) * Math.pow(x, 3));
        }
        return applyMixMono(inputData, output);
    }

    /**
     * Processes a stereo audio signal using the harmonic distortion algorithm
     * Applies gain to each channel (left, right), then the cubic non-linear transformation, to finish with a dry/wet mix.
     *
     * @param inputData a 2D float array where inputData[0] is the left channel and inputData[1] is the right channel
     * @return a 2D float array with harmonic distortion applied to both channels
     */
    @Override
    public float[][] processStereo(float[][] inputData) {
        float[][] gainedData = applyGain(inputData);
        float[][] output = new float[2][gainedData[0].length];
        for (byte ch = 0; ch < 2; ch++) {
            for (int i = 0; i < inputData.length; i++) {
                float x = gainedData[ch][i];
                output[ch][i] = (float) (x - (1.0f / 3.0f) * Math.pow(x, 3));
            }
        }
        return applyMixStereo(inputData, output);
    }

    @Override
    public StringProperty getName() {
        return new SimpleStringProperty("Harmonic Distortion");
    }
}
