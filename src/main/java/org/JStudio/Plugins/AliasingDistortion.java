package org.JStudio.Plugins;

public class AliasingDistortion extends Distortion {
    public AliasingDistortion(float gain, float mix) {
        super(gain, mix);
    }

    @Override
    public float[] processMono(float[] inputData) {
        float[] gainedData = applyGain(inputData);
        float[] output = new float[gainedData.length];
        for (int i = 0; i < gainedData.length; i++) {
            output[i] = (float) Math.sin(gainedData[i] * Math.PI);
        }
        return applyMixMono(inputData, output);
    }

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
}
