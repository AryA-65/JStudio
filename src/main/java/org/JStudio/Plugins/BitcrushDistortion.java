package org.JStudio.Plugins;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.JStudio.Plugins.Models.BitCrush;

public class BitcrushDistortion extends Distortion {
    private BitCrush bitCrusher;

    /**
     * initializing the bitcrush distortion plugin
     * @param gain output gain of the plugin
     * @param mix mix between original sound and processed sound
     * @param depth resolution of processed audio
     */
    public BitcrushDistortion(float gain, float mix, int depth) {
        super(gain, mix);
        this.bitCrusher = new BitCrush(depth);
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
            output[i] = bitCrusher.process(gainedData[i]);
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
                output[ch][i] = bitCrusher.process(gainedData[ch][i]);
            }
        }
        return applyMixStereo(inputData, output);
    }

    /**
     * @return returns the name of the plugin (set manually)
     */
    @Override
    public StringProperty getName() {
        return new SimpleStringProperty("Bit Crush Distortion");
    }
}
