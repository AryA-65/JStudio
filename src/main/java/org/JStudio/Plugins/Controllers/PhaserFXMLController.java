package org.JStudio.Plugins.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import org.JStudio.Plugins.Models.PhaserPlugin;

/**
 * FXML controller class for the Phaser UI
 * @author Theodore Georgiou
 */
public class PhaserFXMLController {
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
    private PhaserPlugin phaser;
    
    /**
     * Initializes the UI, showing the tick marks on the slider and setting
     * actions for the sliders and buttons
     */
    @FXML
    public void initialize() {
        phaser = new PhaserPlugin(100000, 8, 0.5); // Create a phaser
        
        frequencySlider.setMin(1);
        frequencySlider.setMax(10);
        frequencySlider.setShowTickMarks(true);
        frequencySlider.setMajorTickUnit(1);
        frequencySlider.setMinorTickCount(0);
        frequencySlider.setShowTickLabels(true);
        frequencySlider.setValue(2);
        
        deviationSlider.setMin(5);
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
            phaser.setFrequency(newFrequency.intValue()*50000);

        });
        
        deviationSlider.valueProperty().addListener((ObservableValue<? extends Number> deviation, Number oldDeviation, Number newDeviation) -> {
            phaser.setDeviation(newDeviation.intValue());
            
        });
        
        wetDrySlider.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldWetDryFactor, Number newWetDryFactor) -> {
            phaser.setWetDryFactor(newWetDryFactor.doubleValue()/10);
        });
        
        // Play the audio
        playButton.setOnAction(e -> {
            phaser.setPhaserEffect();
        });
        
        // Reset to initial values
        resetButton.setOnAction(e -> {
            phaser.stopAudio();
            phaser = new PhaserPlugin(100000, 8, 0.5);
            frequencySlider.setValue(2);
            deviationSlider.setValue(8);
            wetDrySlider.setValue(5);
        });
    }
}
