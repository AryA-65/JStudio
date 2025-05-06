package org.JStudio.Plugins.Models;

import org.JStudio.Plugins.Models.BitCrush;
import org.JStudio.Plugins.Plugin;

/**
 * Abstract base class for audio distortion effects.
 */
public abstract class Distortion implements Plugin {
    protected float gain, mix;

    /**
     * Creates a distortion effect with the given gain and mix values
     *
     * @param gain the amount of amplification applied before distortion
     * @param mix  the mix ratio between dry (original) and wet (distorted) signals
     */
    Distortion(float gain, float mix) {
        this.gain = gain;
        this.mix = mix;
    }

    /**
     * Applies gain to mono audio data
     *
     * @param monoInputData the input mono audio signal
     * @return a new mono array with gain applied
     */
    protected float[] applyGain(float[] monoInputData) {
        float[] outputData = new float[monoInputData.length];
        for (int i = 0; i < monoInputData.length; i++) {
            outputData[i] = monoInputData[i] * gain;
        }
        return outputData;
    }

    /**
     * Applies gain to stereo audio data
     *
     * @param stereoInputData a 2D float array
     * @return a new 2D array with gain applied to both channels
     */
    protected float[][] applyGain(float[][] stereoInputData) {
        float[][] outputData = new float[2][stereoInputData[0].length];
        for (byte ch = 0; ch < 2; ch++) {
            for (int i = 0; i < stereoInputData[ch].length; i++) {
                outputData[ch][i] = stereoInputData[ch][i] * gain;
            }
        }
        return outputData;
    }

    /**
     * Applies dry/wet mix to mono audio data
     *
     * @param dryData the original signal
     * @param wetData the processed signal
     * @return a new array combining dry and wet data based on the mix ratio
     */
    protected float[] applyMixMono(float[] dryData, float[] wetData) {
        float[] outputData = new float[dryData.length];
        for (int i = 0; i < dryData.length; i++) {
            outputData[i] = dryData[i] * (1 - mix) + wetData[i] * mix;
        }
        return outputData;
    }

    /**
     * Applies dry/wet mix to stereo audio data
     *
     * @param dryData the original stereo signal
     * @param wetData the processed stereo signal
     * @return a new 2D array combining dry and wet data for both channels
     */
    protected float[][] applyMixStereo(float[][] dryData, float[][] wetData) {
        float[][] outputData = new float[2][dryData[0].length];
        for (byte ch = 0; ch < 2; ch++) {
            for (int i = 0; i < dryData[ch].length; i++) {
                outputData[ch][i] = dryData[ch][i] * (1 - mix) + wetData[ch][i] * mix;
            }
        }
        return outputData;
    }

}
