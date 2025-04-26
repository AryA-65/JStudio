package PianoManualSynth;

import javafx.scene.layout.Pane;

public class NotesTrack extends Pane{
    private double frequency;
    private String txt1;
    private String txt2;
    private String txt3;
    private double tone1Value;
    private double tone2Value;
    private double tone3Value;
    private double volume1Value;
    private double volume2Value;
    private double volume3Value;
    private AudioThread auTh;
    
    public double getFrequency(){
        return frequency;
    }
    
    public String getText1(){
        return txt1;
    }
    
    public String getText2(){
        return txt2;
    }
    
    public String getText3(){
        return txt3;
    }
    
    public double getTone1Value(){
        return tone1Value;
    }
    
    public double getTone2Value(){
        return tone2Value;
    }
    
    public double getTone3Value(){
        return tone3Value;
    }
    
    public double getVolume1Value(){
        return volume1Value;
    }
    
    public double getVolume2Value(){
        return volume2Value;
    }
    
    public double getVolume3Value(){
        return volume3Value;
    }
    
    public AudioThread getAudioThread(){
        return auTh;
    }

    public NotesTrack(double frequency, String txt1, String txt2, String txt3, double tone1Value, double tone2Value, double tone3Value, double volume1Value, double volume2Value, double volume3Value) {
        //this.auTh = auTh;
        this.frequency = frequency;
        this.txt1 = txt1;
        this.txt2 = txt2;
        this.txt3 = txt3;
        this.tone1Value = tone1Value;
        this.tone2Value = tone2Value;
        this.tone3Value = tone3Value;
        this.volume1Value = volume1Value;
        this.volume2Value = volume2Value;
        this.volume3Value = volume3Value;
    }
    
}
