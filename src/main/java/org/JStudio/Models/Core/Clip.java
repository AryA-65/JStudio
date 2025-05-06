package org.JStudio.Models.Core;

import java.io.Serial;
import java.io.Serializable;

/**
 * Clip model class
 */
public abstract class Clip implements Serializable {
    private double position, length;

    @Serial
    private static final long serialVersionUID = 3L;

    Clip(double position) {
        this.position = position;
    }

    Clip(double position, double length) {}

    /**
     * Sets the position of a clip
     * @param position the position of a clip
     */
    public void setPosition(double position) {
        this.position = position;
    }

    /**
     * Sets the length of a clip
     * @param length the length of a clip
     */
    public void setLength(double length) {
        this.length = length;
    }

    /**
     * Retrieves the position of a clip
     * @return the position of a clip
     */
    public double getPosition() {
        return position;
    }

    /**
     * Retrieves the length of a clip
     * @return the length of a clip
     */
    public double getLength() {
        return length;
    }

    /**
     * Retrieves the information of a note
     * @return the note's information
     */
    public String getInfo() {
        return "Clip position: " + position + ", length: " + length;
    }
}

