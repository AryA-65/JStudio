package org.JStudio.Plugins.Models;

public abstract class Stereoizer {
    protected float sampleRate, width = 1.0f;

    public float[][] output;

    Stereoizer(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public abstract float[][] process(float[] mono);

}


