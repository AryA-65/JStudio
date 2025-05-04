package org.JStudio.Core;

import java.io.Serializable;

public class Note implements Serializable {
    private double default_l = 0.5, length, position; //in seconds
    private short note;
    private float volume;

    Note(short sample_rate, short note, double position) {
        this.length = sample_rate * default_l;
        this.note = note;
        this.position = position;
        this.volume = 1f;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void setNote(short note) {
        this.note = note;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public void setLength(double length, int sample_rate) {
        this.length = length * sample_rate;
        this.default_l = length;
    }

    public short getNote() {
        return note;
    }

    public double getPosition() {
        return position;
    }

    public String getNote_Volume() {
        return note + "n " + volume + "v";
    }
}
