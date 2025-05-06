
package org.JStudio.Plugins;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.JStudio.Plugins.Models.BitCrush;

public class BitcrusherPlugin implements Plugin {
    private BitCrush bitCrush;

    public BitcrusherPlugin(int depth) {
        this.bitCrush = new BitCrush(depth);
    }

    @Override
    public float[] processMono(float[] input) {
        float[] output = new float[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = bitCrush.process(input[i]);
        }
        return output;
    }

    @Override
    public float[][] processStereo(float[][] inputData) {
        float[] left = processMono(inputData[0]);
        float[] right = processMono(inputData[1]);
        return new float[][] { left, right };
    }

    @Override
    public StringProperty getName() {
        return new SimpleStringProperty("BitCrusher");
    }
}
