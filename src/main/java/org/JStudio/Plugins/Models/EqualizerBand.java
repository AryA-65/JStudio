/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.JStudio.Plugins.Models;

import javafx.geometry.Orientation;
import javafx.scene.control.Slider;

/**
 *
 * @author alexa
 */
public class EqualizerBand extends Slider {

    private int centerFrequency;
    private int highFrequency;
    private int lowFrequency;
    private int octavesPerBand = 1;
    
    public int getCenterFrequency(){
        return centerFrequency;
    }
    
    public int getHighFrequency(){
        return highFrequency;
    }
    
    public int getLowFrequency(){
        return lowFrequency;
    }

    public EqualizerBand(int centerFrequency) {
        this.setValue(1);
        this.centerFrequency = centerFrequency;
        lowFrequency = (int) (this.centerFrequency / Math.sqrt(Math.pow(2, octavesPerBand)));
        highFrequency = (int) (this.centerFrequency * Math.sqrt(Math.pow(2, octavesPerBand)));
//        System.out.println("Low: " + lowFrequency);
//        System.out.println("Center: " + this.centerFrequency);
//        System.out.println("High: " + highFrequency);
        
        this.setOrientation(Orientation.VERTICAL);
        this.setMin(0);
        this.setMax(20);
        this.setPrefWidth(60);
        this.setMajorTickUnit(0.5);
        this.setShowTickLabels(true);
        this.setShowTickMarks(true);
    }

}
