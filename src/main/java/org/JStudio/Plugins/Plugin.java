package org.JStudio.Plugins;

public interface Plugin {
    public float[] processMono(float[] input);

    public float[][] processStereo(float[][] input);
}
