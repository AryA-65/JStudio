package org.JStudio.Plugins.Models;

public class LFOStereo extends Stereoizer {
    private float lfoFrequency;

    public LFOStereo(float sampleRate, float lfoFrequency) {
        super(sampleRate);
        this.lfoFrequency = lfoFrequency;
    }

    @Override
    public float[][] process(float[] mono) {
        output = new float[2][mono.length];

        for (int i = 0; i < mono.length; i++) {
            float lfo = (float) Math.sin(2 * Math.PI * lfoFrequency * i / sampleRate);
            output[0][i] = mono[i] * (1.0f + lfo * width);
            output[1][i] = mono[i] * (1.0f - lfo * width);
        }
        return output;
    }
}
