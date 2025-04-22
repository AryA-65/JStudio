package org.JStudio.Plugins.Models;

public abstract class Stereoizer { //add mix to the other classes
    protected float sampleRate, width = 1.0f;

    Stereoizer(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public abstract void process(float[] mono);
//    public abstract float[][] process(float[] mono);

}

class HAASStereo extends Stereoizer {
    private final int delaySamples;

    HAASStereo(int delaySamples, float sampleRate) {
        super(delaySamples);
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

class FilteredStereo extends Stereoizer {
    FilteredStereo(float sampleRate) {
        super(sampleRate);
    }

    @Override
    public void process(float[] mono) {

    }
}

class LFOStereo extends Stereoizer {
    private float lfoQuantity;

    LFOStereo(float sampleRate, float lfoQuantity) {
        super(sampleRate);
        this.lfoQuantity = lfoQuantity;
    }

    @Override
    public void process(float[] mono) {
        float[] left = new float[mono.length];
        float[] right = new float[mono.length];
        for (int i = 0; i < mono.length; i++) {
            float lfo = (float) Math.sin(i * lfoQuantity);
            left[i] = mono[i] * (1.0f + lfo * width);
            right[i] = mono[i] * (1.0f - lfo * width);
        }
    }
}

class CombStereo extends Stereoizer {
    CombStereo(float sampleRate) {
        super(sampleRate);
    }

    @Override
    public void process(float[] mono) {

    }
}

class JitterStereo extends Stereoizer {
    JitterStereo(float sampleRate) {
        super(sampleRate);
    }

    @Override
    public void process(float[] mono) {

    }
}

class MidSideStereo extends Stereoizer {
    private float factor;

    MidSideStereo(float sampleRate, float factor) {
        super(sampleRate);
        this.factor = factor;
    }

    @Override
    public void process(float[] mono) {

    }
}