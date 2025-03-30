package org.JStudio.Plugins;

public class BitCrush {
    private int depth;

    BitCrush(int depth) {
        this.depth = depth;
    }

    public float applyBitcrush(float input) {
        float scale = (float) Math.pow(2, depth);
        return Math.round(scale * input) / scale;
    }
}
