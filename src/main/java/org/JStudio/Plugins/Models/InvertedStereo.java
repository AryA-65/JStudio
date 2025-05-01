package org.JStudio.Plugins.Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InvertedStereo extends Stereoizer {
    private StringProperty name = new SimpleStringProperty("Inverted Stereo");

    public InvertedStereo(float sampleRate) {
        super(sampleRate);
    }

    @Override
    public float[][] process(float[] mono) {
        output = new float[2][mono.length];
        for (int i = 0; i < mono.length; i++) {
            output[0][i] = mono[i];
            output[1][i] = -mono[i];
        }
        return output;
    }
}
