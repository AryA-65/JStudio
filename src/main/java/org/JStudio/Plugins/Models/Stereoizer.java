package org.JStudio.Plugins.Models;

public abstract class Stereoizer { //add mix to the other classes
    protected float sampleRate, width = 1.0f; // todo ask arya how we are not doing anything with left and right

    Stereoizer(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public abstract void process(float[] mono);
}

class HAASStereo extends Stereoizer {
    private final int delaySamples;

    HAASStereo(float sampleRate, int delaySamples) {
        super(sampleRate);
        this.delaySamples = delaySamples;
    }

    @Override
    public void process(float[] mono) {
        float[] left = new float[mono.length];
        float[] right = new float[mono.length];

        for (int i = 0; i < delaySamples; i++) {
            left[i] = mono[i];
            right[i] = (i >= delaySamples) ? mono[i - delaySamples] : 0f;
        }
    }
}

class LFOStereo extends Stereoizer {
    private float lfoFrequency;

    public LFOStereo(float sampleRate, float lfoFrequency) {
        super(sampleRate);
        this.lfoFrequency = lfoFrequency;
    }

    @Override
    public void process(float[] mono) {
        float[] left = new float[mono.length];
        float[] right = new float[mono.length];

        for (int i = 0; i < mono.length; i++) {
            float lfo = (float) Math.sin(2 * Math.PI * lfoFrequency * i / sampleRate);
            left[i] = mono[i] * (1.0f + lfo * width);
            right[i] = mono[i] * (1.0f - lfo * width);
        }
    }
}



class InvertedStereo extends Stereoizer {
    InvertedStereo(float sampleRate) {
        super(sampleRate);
    }

    @Override
    public void process(float[] mono) {
        float[] left = new float[mono.length];
        float[] right = new float[mono.length];
        for (int i = 0; i < mono.length; i++) {
            left[i] = mono[i];
            right[i] = -mono[i];
        }
    }
}
