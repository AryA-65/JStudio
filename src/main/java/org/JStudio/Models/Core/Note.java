package org.JStudio.Models.Core;

import java.io.Serializable;

/**
 * Note model class
 */
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

    /**
     * Volume setter
     * @param volume the volume
     */
    public void setVolume(float volume) {
        this.volume = volume;
    }

    /**
     * Sets a note
     * @param note the note to set
     */
    public void setNote(short note) {
        this.note = note;
    }

    /**
     * Sets the position of a note
     * @param position the position of a note
     */
    public void setPosition(double position) {
        this.position = position;
    }

    /**
     * Sets the length of a note
     * @param length the length of a note
     * @param sample_rate the sample rate of a note
     */
    public void setLength(double length, int sample_rate) {
        this.length = length * sample_rate;
        this.default_l = length;
    }

    /**
     * Retrieves a note
     * @return the note
     */
    public short getNote() {
        return note;
    }

    /**
     * Retrieves the position of a note
     * @return the position of a note
     */
    public double getPosition() {
        return position;
    }

    /**
     * Retrieves the volume of a note
     * @return the volume of a note
     */
    public String getNote_Volume() {
        return note + "n " + volume + "v";
    }
}
