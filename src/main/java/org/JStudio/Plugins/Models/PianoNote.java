package org.JStudio.Plugins.Models;

public class PianoNote {
    private int noteNum;
    private int velocity = 90;
    private boolean isPlaying;
    private double length;
    //private double startTime;
    private double positionX;
    private double width;
    
    //sets note parameters in the constructor
    public PianoNote(int noteNum, double length, double positionX, double width) {
        this.noteNum = noteNum;
        this.length = length;
        this.positionX = positionX;
        this.width = width;
        this.isPlaying = false;
    }

    //getters and setters
    public int getNoteNum() {
        return noteNum;
    }

    public void setNoteNum(int noteNum) {
        this.noteNum = noteNum;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }
    
    public int getVelocity(){
        return velocity;
    }
}
