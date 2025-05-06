
package org.JStudio.Plugins;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.JStudio.Plugins.Models.BitCrush;


/**
 * Class used to change the resolution of audio data
 */
public class BitcrusherPlugin implements Plugin {
    private BitCrush bitCrush;

    /**
     * initializing the plugin
     * @param depth input for the resolution of the audio
     */
    public BitcrusherPlugin(int depth) {
        this.bitCrush = new BitCrush(depth);
    }

    /**
     * processes audio in mono sound
     * @param input input buffer (float)
     * @return returning the processed audio in mono sound
     */
    @Override
    public float[] processMono(float[] input) {
        float[] output = new float[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = bitCrush.process(input[i]);
        }
        return output;
    }

    /**
     * processes audio in stereo sound
     * @param inputData input buffer (stereo float)
     * @return returns the processed audio in stereo sound
     */
    @Override
    public float[][] processStereo(float[][] inputData) {
        float[] left = processMono(inputData[0]);
        float[] right = processMono(inputData[1]);
        return new float[][] { left, right };
    }

    /**
     * @return returns the name of the plugin (set manually)
     */
    @Override
    public StringProperty getName() {
        return new SimpleStringProperty("BitCrusher");
    }
}
