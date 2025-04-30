package org.JStudio.Core;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class AudioClip extends Clip {
    private float[][] buffer;
    private int sampleRate;
    private double s_pos, e_pos; //s_pos (start) and e_pos (end) is for shifting/cutting audio clips

    AudioClip(double position) {
        super(position);
        this.buffer = new float[2][1024];
        super.setLength(1024 / 44100);
    }

    //test init class
    public AudioClip(double position, float[][] buff, int sampleRate) {
        super(position);
        this.buffer = buff;
        this.sampleRate = sampleRate;
        super.setLength(Math.max(buff[0].length, buff[1] == null ? 0 : buff[1].length) / this.sampleRate);
    }

    public void setS_pos(double s_pos) {
        this.s_pos = s_pos;
    }

    public void setE_pos(double e_pos) {
        this.e_pos = e_pos;
    }

    public double getS_pos() {
        return s_pos;
    }

    public double getE_pos() {
        return e_pos;
    }

    public String getBuffertoString() {
        return Arrays.deepToString(buffer);
    }

    public float[][] getBuffer() {return buffer;}

    public int getSampleRate() {
        return sampleRate;
    }
}
