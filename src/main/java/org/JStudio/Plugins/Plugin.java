package org.JStudio.Plugins;

import javafx.beans.property.StringProperty;

import java.io.Serializable;

public interface Plugin extends Serializable {
    float[] processMono(float[] input);

    float[][] processStereo(float[][] input);

    StringProperty getName();
}
