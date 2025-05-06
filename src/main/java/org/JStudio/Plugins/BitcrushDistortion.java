package org.JStudio.Plugins;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.JStudio.Plugins.Models.BitCrush;

public class BitcrushDistortion extends Distortion {
    private BitCrush bitCrusher;

    public BitcrushDistortion(float gain, float mix, int depth) {
        super(gain, mix);
        this.bitCrusher = new BitCrush(depth);
    }

    @Override
    public float[] processMono(float[] inputData) {
        float[] gainedData = applyGain(inputData);
        float[] output = new float[gainedData.length];
        for (int i = 0; i < gainedData.length; i++) {
            output[i] = bitCrusher.process(gainedData[i]);
        }
        return applyMixMono(inputData, output);
    }

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

    @Override
    public StringProperty getName() {
        return new SimpleStringProperty("Bit Crush Distortion");
    }
}
