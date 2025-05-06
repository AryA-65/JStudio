package org.JStudio.Plugins;

import javafx.beans.property.StringProperty;

import java.io.Serializable;

public interface Plugin extends Serializable {
    /**
     * process audio in mono sound
     * @param input input buffer (float)
     * @return returns the processed audio in mono sound
     */
    float[] processMono(float[] input);

    /**
     * process audio in stereo sound
     * @param input input buffer (stereo float)
     * @return returns the processed audio in stereo sound
     */
    float[][] processStereo(float[][] input);

    /**
     * @return gets the name of the plugin
     */
    StringProperty getName();
}
