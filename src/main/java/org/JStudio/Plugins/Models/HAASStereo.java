package org.JStudio.Plugins.Models;

public class HAASStereo extends Stereoizer {
    private final int delaySamples;

    public HAASStereo(float sampleRate, int delaySamples) {
        super(sampleRate);
        this.delaySamples = delaySamples;
    }

    @Override
    public float[][] process(float[] mono) {
        output = new float[2][mono.length + delaySamples];

        for (int i = 0; i < delaySamples; i++) {
            output[0][i] = mono[i];
            output[1][i] = (i >= delaySamples) ? mono[i - delaySamples] : 0f;
        }
        return output;
    }
}
