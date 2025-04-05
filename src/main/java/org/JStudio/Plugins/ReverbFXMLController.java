package org.JStudio.Plugins;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

/**
 * FXML controller class for the Reverb UI
 * @author Theodore Georgiou
 */
public class ReverbFXMLController {
    @FXML
    private Label preDelayLabel;
    @FXML
    private Label decayTimeLabel;
    @FXML
    private Label diffusionLabel;
    @FXML
    private Label dampingLabel;
    @FXML
    private Label wetDryLabel;
    @FXML
    private Button resetButton;
    @FXML
    private Button playButton;
    @FXML
    private Slider preDelaySlider;
    @FXML
    private Slider decayTimeSlider;
    @FXML
    private Slider diffusionSlider;
    @FXML
    private Slider dampingSlider;
    @FXML
    private Slider wetDrySlider;
    private ReverbPlugin reverb;
    
    /**
     * Initializes the UI, showing the tick marks on the slider and setting
     * actions for the sliders and buttons
     */
    @FXML
    public void initialize() {
        reverb = new ReverbPlugin(1000, 10000, 2000, 0.5); // Create a reverb
        
        // Set the visual components/max and min values of the sliders
        preDelaySlider.setMin(1);
        preDelaySlider.setMax(9.9);
        preDelaySlider.setShowTickMarks(true);
        preDelaySlider.setMajorTickUnit(1);
        preDelaySlider.setMinorTickCount(0);
        preDelaySlider.setShowTickLabels(true);
        preDelaySlider.setSnapToTicks(true);
        preDelaySlider.setValue(1);
        
        decayTimeSlider.setMin(1);
        decayTimeSlider.setMax(10);
        decayTimeSlider.setShowTickMarks(true);
        decayTimeSlider.setMajorTickUnit(1);
        decayTimeSlider.setMinorTickCount(0);
        decayTimeSlider.setShowTickLabels(true);
        decayTimeSlider.setSnapToTicks(true);
        decayTimeSlider.setValue(1);
        
        diffusionSlider.setMin(1);
        diffusionSlider.setMax(10);
        diffusionSlider.setShowTickMarks(true);
        diffusionSlider.setMajorTickUnit(1);
        diffusionSlider.setMinorTickCount(0);
        diffusionSlider.setShowTickLabels(true);
        diffusionSlider.setSnapToTicks(true);
        diffusionSlider.setValue(2);
        
        wetDrySlider.setMin(1);
        wetDrySlider.setMax(10);
        wetDrySlider.setShowTickMarks(true);
        wetDrySlider.setMajorTickUnit(1);
        wetDrySlider.setMinorTickCount(0);
        wetDrySlider.setShowTickLabels(true);
        wetDrySlider.setValue(5);
        
        // Set listeners and actions for sliders and buttons
        preDelaySlider.valueProperty().addListener((ObservableValue<? extends Number> preDelay, Number oldPredelay, Number newPreDelay) -> {
            reverb.setPreDelay(newPreDelay.intValue()*1000);
        });
        
        decayTimeSlider.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldDecayTime, Number newDecayTime) -> {
            reverb.setDecay(newDecayTime.doubleValue()*10000);
            
        });
        
        wetDrySlider.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldWetDryFactor, Number newWetDryFactor) -> {
            reverb.setWetDryFactor(newWetDryFactor.doubleValue()/10);
        });
        
        diffusionSlider.valueProperty().addListener((ObservableValue<? extends Number> diffusion, Number oldDiffusion, Number newDiffusion) -> {
            reverb.setDiffusion(newDiffusion.intValue()*1000);
        });
        
        // Play the audio
        playButton.setOnAction(e -> {
            reverb.setReverbEffect();
        });
        
        // Reset to initial values
        resetButton.setOnAction(e -> {
            reverb = new ReverbPlugin(1000, 10000, 2000, 0.5);
            preDelaySlider.setValue(1);
            decayTimeSlider.setValue(1);
            diffusionSlider.setValue(2);
            wetDrySlider.setValue(5);
        });
    }
}
