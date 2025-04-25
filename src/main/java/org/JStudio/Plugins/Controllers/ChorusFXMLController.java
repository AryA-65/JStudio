package org.JStudio.Plugins.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import org.JStudio.Plugins.Models.Modulation;

public class ChorusFXMLController {
    @FXML
    private Slider frequencySlider;
    @FXML
    private Slider deviationSlider;
    @FXML
    private Slider wetDrySlider;
    @FXML
    private Button resetButton;
    @FXML
    private Button playButton;
    private Modulation chorus;
    
    /**
     * Initializes the UI, showing the tick marks on the slider and setting
     * actions for the sliders and buttons
     */
    @FXML
    public void initialize() {
        chorus = new Modulation(20000, 200, 0.5); // Create a chorus
        
        frequencySlider.setMin(1);
        frequencySlider.setMax(10);
        frequencySlider.setShowTickMarks(true);
        frequencySlider.setMajorTickUnit(1);
        frequencySlider.setMinorTickCount(0);
        frequencySlider.setShowTickLabels(true);
        frequencySlider.setValue(2);
        
        deviationSlider.setMin(1);
        deviationSlider.setMax(10);
        deviationSlider.setShowTickMarks(true);
        deviationSlider.setMajorTickUnit(1);
        deviationSlider.setMinorTickCount(0);
        deviationSlider.setShowTickLabels(true);
        deviationSlider.setSnapToTicks(true);
        deviationSlider.setValue(2);
        
        wetDrySlider.setMin(1);
        wetDrySlider.setMax(10);
        wetDrySlider.setShowTickMarks(true);
        wetDrySlider.setMajorTickUnit(1);
        wetDrySlider.setMinorTickCount(0);
        wetDrySlider.setShowTickLabels(true);
        wetDrySlider.setValue(5);
        
        // Set listeners and actions for sliders and buttons
        frequencySlider.valueProperty().addListener((ObservableValue<? extends Number> frequency, Number oldFrequency, Number newFrequency) -> {
            chorus.setFrequency(newFrequency.intValue()*10000);
        });
        
        deviationSlider.valueProperty().addListener((ObservableValue<? extends Number> deviation, Number oldDeviation, Number newDeviation) -> {
            chorus.setDeviation(newDeviation.intValue()*100);
            
        });
        
        wetDrySlider.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldWetDryFactor, Number newWetDryFactor) -> {
            chorus.setWetDryFactor(newWetDryFactor.doubleValue()/10);
        });
        
        // Play the audio
        playButton.setOnAction(e -> {
            chorus.setModulationEffect();
        });
        
        // Reset to initial values
        resetButton.setOnAction(e -> {
            chorus.stopAudio();
            chorus = new Modulation(20000, 200, 0.5);
            frequencySlider.setValue(2);
            deviationSlider.setValue(2);
            wetDrySlider.setValue(5);
        });
    }
}
