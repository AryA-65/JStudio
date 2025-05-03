package org.JStudio.Plugins.Models;

import javax.sound.sampled.SourceDataLine;
import java.io.File;

public class AudioAmplifier extends Plugin {

    private double[] processedAudioData;

    public byte[] buffer;

    private double amp;

    public AudioAmplifier(double amp) {
        convertAudioFileToByteArray();
        this.amp = amp;
    }
}
