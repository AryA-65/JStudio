package org.JStudio.Core;

public abstract class Clip {
    private double position, length;

    Clip(double position) {
        this.position = position;
    }

    Clip(double position, int length) {}

    public void setPosition(double position) {
        this.position = position;
    }

    public void setLength(int length) {
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

