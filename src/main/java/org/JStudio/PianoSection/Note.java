/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.JStudio.PianoSection;

import javafx.scene.shape.Rectangle;

/**
 *
 * @author alexa
 */
public class Note extends Rectangle{
    int noteNum;
    private boolean isPlaying = false;
    double length;
    
    public void setNoteNum(int noteNum){
        this.noteNum = noteNum;
    }
    
    public int getNoteNum(){
        return noteNum;
    }
    
    public void setIsPlaying(boolean isPlaying){
        this.isPlaying = isPlaying;
    }
    
    public boolean getIsPlaying(){
        return isPlaying;
    }
    
    public Note(double width, double height){
        this.setWidth(width);
        this.setHeight(height);
    }
    
}
