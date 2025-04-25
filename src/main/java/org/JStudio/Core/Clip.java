package org.JStudio.Core;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Clip {
    private int position, s_pos, e_pos, length; //s_pos (start) and e_pos (end) is for shifting/cutting audio clips

    Clip(int position) {
        this.position = position;
    }

    Clip(int position, short sample_rate) {
        this.position = position;
        this.length = sample_rate * 2;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }

    public void setStartOffset(int start_offset) {
        this.s_pos = start_offset;
    }

    public void setEndOffset(int end_offset) {
        this.e_pos = end_offset;
    }

    public int getStartOffset() {
        return s_pos;
    }

    public int getEndOffset() {
        return e_pos;
    }

    public String getInfo() {
        return "Clip position: " + position + ", length: " + length;
    }
}

