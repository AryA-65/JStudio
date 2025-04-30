package org.JStudio.Plugins;

public abstract class Distortion implements Plugin {
    protected float gain, mix;

    Distortion(float gain, float mix) {
        this.gain = gain;
        this.mix = mix;
    }

//    public abstract float[] processMono(float[] inputData);

//    public abstract float[][] processStereo(float[][] inputData);

    protected float[] applyGain(float[] monoInputData) {
        float[] outputData = new float[monoInputData.length];
        for (int i = 0; i < monoInputData.length; i++) {
            outputData[i] = monoInputData[i] * gain;
        }
        return outputData;
    }

    protected float[][] applyGain(float[][] stereoInputData) {
        float[][] outputData = new float[2][stereoInputData[0].length];
        for (byte ch = 0; ch < 2; ch++) {
            for (int i = 0; i < stereoInputData.length; i++) {
                outputData[ch][i] = stereoInputData[ch][i] * gain;
            }
        }
        return outputData;
    }

    protected float[] applyMixMono(float[] dryData, float[] wetData) {
        float[] outputData = new float[dryData.length];
        for (int i = 0; i < dryData.length; i++) {
            outputData[i] = dryData[i] * (1 - mix) + wetData[i] * mix;
        }
        return outputData;
    }

    protected float[][] applyMixStereo(float[][] dryData, float[][] wetData) {
        float[][] outputData = new float[2][dryData[0].length];
        for (byte ch = 0; ch < 2; ch++) {
            for (int i = 0; i < dryData.length; i++) {
                outputData[ch][i] = dryData[ch][i] * (1 - mix) + wetData[ch][i] * mix;
            }
        }
        return outputData;
    }
}

class ClippingDistortion extends Distortion {
    private float threshold, softness, expoFactor; //threshold being the max volume, softness being the clipping factor and expo being how fast it adds the clipping
    private TYPE type;

    public enum TYPE {
        HARD, SOFT, EXPO, REVERSE
    }

    public ClippingDistortion(float gain, float mix, float threshold, float softness, float expoFactor, TYPE type) {
        super(gain, mix);
        this.threshold = threshold;
        this.softness = softness;
        this.expoFactor = expoFactor;
        this.type = type;
    }

    private float applyClipping(float sample) {
        return switch (type) {
            case HARD -> Math.max(-threshold, Math.min(threshold, sample));
            case SOFT -> (float) (Math.tanh(softness * sample) / Math.tanh(softness));
            case EXPO -> (float) (sample / (1 + expoFactor * Math.abs(sample)));
            case REVERSE -> Math.abs(sample) > threshold ? (threshold - (sample - threshold)) * -1 : sample;
            default -> sample;
        };
    }

    @Override
    public float[] processMono(float[] inputData) {
        float[] gainedData = applyGain(inputData);
        float[] output = new float[gainedData.length];
        for (int i = 0; i < gainedData.length; i++) {
            output[i] = applyClipping(gainedData[i]);
        }
        return applyMixMono(inputData, output);
    }

    @Override
    public float[][] processStereo(float[][] inputData) {
        float[][] gainedData = applyGain(inputData);
        float[][] output = new float[2][gainedData[0].length];
        for (byte ch = 0; ch < 2; ch++) {
            for (int i = 0; i < inputData.length; i++) {
                output[ch][i] = applyClipping(gainedData[ch][i]);
            }
        }
        return applyMixStereo(inputData, output);
    }
}

class BitcrushDistortion extends Distortion {
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
}
