package org.JStudio.Models.Core;

import java.io.Serial;
import java.util.Arrays;

public class AudioClip extends Clip {
    @Serial
    private static final long serialVersionUID = 5L;

    private float[][] buffer;
    private int sampleRate;
    private double s_pos, e_pos; //s_pos (start) and e_pos (end) is for shifting/cutting audio clips

    /**
     * initializing an empty audio clip
     * @param position position of the clip in seconds
     */
    public AudioClip(double position) {
        super(position);
        this.buffer = new float[2][1024];
        super.setLength((double) 1024 / 44100);
    }

    /**
     * initializing an audio clip using a buffer
     * @param position position of the clip in seconds
     * @param buff input buffer, from drag and drop
     * @param sampleRate sample rate of the audio
     */
    public AudioClip(double position, float[][] buff, int sampleRate) {
        super(position);
        this.buffer = buff;
        this.sampleRate = sampleRate;
        super.setLength((double) Math.max(buff[0].length, buff[1] == null ? 0 : buff[1].length) / this.sampleRate);
    }

    // Setters
    public void setS_pos(double s_pos) {
        this.s_pos = s_pos;
    }

    public void setE_pos(double e_pos) {
        this.e_pos = e_pos;
    }

    // Getters
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
