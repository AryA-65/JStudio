package org.JStudio.Plugins;

public class HarmonicDistortion extends Distortion {
    public HarmonicDistortion(float gain, float mix) {
        super(gain, mix);
    }

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
}
