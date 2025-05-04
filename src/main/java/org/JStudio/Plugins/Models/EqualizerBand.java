package org.JStudio.Plugins.Models;

import javafx.geometry.Orientation;
import javafx.scene.control.Slider;

public class EqualizerBand extends Slider {

    private int centerFrequency;
    private int highFrequency;
    private int lowFrequency;
    private int octavesPerBand = 1;
    
    //getters for frequencies
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
        
        //determine the max and min of the range each band modifies
        lowFrequency = (int) (this.centerFrequency / Math.sqrt(Math.pow(2, octavesPerBand)));
        highFrequency = (int) (this.centerFrequency * Math.sqrt(Math.pow(2, octavesPerBand)));
        
        //set slider options
        this.setOrientation(Orientation.VERTICAL);
        this.setMin(0);
        this.setMax(5);
        this.setPrefWidth(60);
        this.setMajorTickUnit(0.5);
        this.setShowTickLabels(true);
        this.setShowTickMarks(true);
    }

}
