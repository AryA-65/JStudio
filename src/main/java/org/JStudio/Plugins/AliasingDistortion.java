package org.JStudio.Plugins;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Plugin that takes any values about half the sample rate and flips them (currently not working, in progress)
 */
public class AliasingDistortion extends Distortion {
    /**
     * initializing the plugin
     * @param gain output gain of the audio
     * @param mix mix between original sound and processed sound
     */
    public AliasingDistortion(float gain, float mix) {
        super(gain, mix);
    }

    /**
     * processes audio in mono sound
     * @param inputData input buffer (float)
     * @return returning the processed audio in mono sound
     */
    @Override
    public float[] processMono(float[] inputData) {
        float[] gainedData = applyGain(inputData);
        float[] output = new float[gainedData.length];
        for (int i = 0; i < gainedData.length; i++) {
            output[i] = (float) Math.sin(gainedData[i] * Math.PI);
        }
        return applyMixMono(inputData, output);
    }

    /**
     * processes audio in stereo sound
     * @param inputData input buffer (stereo float)
     * @return returns the processed audio in stereo sound
     */
    @Override
    public float[][] processStereo(float[][] inputData) {
        float[][] gainedData = applyGain(inputData);
        float[][] output = new float[2][gainedData[0].length];
        for (byte ch = 0; ch < 2; ch++) {
            for (int i = 0; i < inputData.length; i++) {
                output[ch][i] = (float) Math.sin(gainedData[ch][i] * Math.PI);
            }
        }
        return applyMixStereo(inputData, output);
    }

    /**
     * @return returns the name of the plugin (set manually)
     */
    @Override
    public StringProperty getName() {
        return new SimpleStringProperty("Aliasing Distortion");
    }
}
