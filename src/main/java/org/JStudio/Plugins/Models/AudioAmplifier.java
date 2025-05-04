package org.JStudio.Plugins.Models;

import javax.sound.sampled.SourceDataLine;
import java.io.File;

public class AudioAmplifier extends Plugin {

    private File audioFile;
    private double[] processedAudioData;

    public byte[] buffer;
    public byte[] outputArray;

    private double amp;
    private SourceDataLine line;

    public AudioAmplifier(double amp) {
        convertAudioFileToByteArray();
        this.amp = amp;
    }



}
