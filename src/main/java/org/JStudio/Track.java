package org.JStudio;

import java.util.List;

public class Track {
    private String name;
    private static short activeTracks = 0;
    private short id;
    private double amplitude, pitch;
//    private List<>

    Track(String name) {
        this.name = name;
        this.id = ++activeTracks;
    }

    public void addClip() {
        //empty for now
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }
}

