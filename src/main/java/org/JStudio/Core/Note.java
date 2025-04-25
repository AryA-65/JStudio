package org.JStudio.Core;

public class Note {
    private double default_l = 0.25, length;
    private long position;
    private short note;
    private float volume;

    Note(short sample_rate, short note, long position) {
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

    public void setPosition(int position) {
        this.position = position;
    }

    public void setLength(double length, int sample_rate) {
        this.length = length * sample_rate;
        this.default_l = length;
    }

    public short getNote() {
        return note;
    }

    public long getPosition() {
        return position;
    }

    public String getNote_Volume() {
        return note + "n " + volume + "v";
    }
}
