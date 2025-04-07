package PianoSection.Models;

public class Note {
    private int noteNum;
    private boolean isPlaying;
    private double length;
    private double positionX;
    private double width;
    
    public Note(int noteNum, double length, double positionX, double width) {
        this.noteNum = noteNum;
        this.length = length;
        this.positionX = positionX;
        this.width = width;
        this.isPlaying = false;
    }
    
    
    //getters and setters for all parameters
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
}
