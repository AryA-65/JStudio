package org.JStudio.Core;

import java.util.HashMap;
import java.util.Map;

public class Note {
    private double default_l = 0.25, length;
    private int note, position;
    private float volume;

    Note(short sample_rate, int note, int position) {
        this.length = sample_rate * default_l;
        this.note = note;
        this.position = position;
        this.volume = 1f;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setLength(double length, int sample_rate) {
        this.length = length * sample_rate;
        this.default_l = length;
    }

    public int getNote() {
        return note;
    }

    public int getPosition() {
        return position;
    }

    public Map<Integer, Float> getNote_Volume() {
        return new HashMap<Integer, Float>() {{this.put(note, volume);}};
    }
}
