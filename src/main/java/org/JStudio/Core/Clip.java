package org.JStudio.Core;

import java.io.Serializable;

public abstract class Clip implements Serializable {
    private double position, length;

    Clip(double position) {
        this.position = position;
    }

    Clip(double position, double length) {}

    public void setPosition(double position) {
        this.position = position;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getPosition() {
        return position;
    }

    public double getLength() {
        return length;
    }

    public String getInfo() {
        return "Clip position: " + position + ", length: " + length;
    }
}

