package org.JStudio.Plugins.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import org.JStudio.Plugins.Models.Reverb;
import org.JStudio.UI.Knob;
import static org.JStudio.UI.Knob.Type.REG;

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
    private Slider wetDrySlider;
    @FXML
    private GridPane grid;
    Knob preDelayKnob = new Knob(100, true, 0.1, REG);
    Knob decayTimeKnob = new Knob(100, false, 0, REG);
    Knob diffusionKnob = new Knob(100, true, 0.1, REG);
    Knob wetDryKnob = new Knob(100, false, 0, REG);
    private Reverb reverb;
    
    /**
     * Initializes the UI, showing the tick marks on the slider and setting
     * actions for the sliders and buttons
     */
    @FXML
    public void initialize() {
        reverb = new Reverb(1000, 10000, 2000, 0.5); // Create a reverb
        preDelayKnob.setTranslateX(15);
        decayTimeKnob.setTranslateX(15);
        diffusionKnob.setTranslateX(15);
        wetDryKnob.setTranslateX(15);
        
        grid.add(preDelayKnob, 0, 1);
        grid.add(decayTimeKnob, 1, 1);
        grid.add(diffusionKnob, 3, 1);
        grid.add(wetDryKnob, 4, 1);
        
        preDelayKnob.valueProperty().addListener((ObservableValue<? extends Number> preDelay, Number oldPredelay, Number newPreDelay) -> {
            newPreDelay = preDelayKnob.getValue() * 10 * 500;
            reverb.setPreDelay(newPreDelay.intValue());
        });
        
        decayTimeKnob.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldDecayTime, Number newDecayTime) -> {
            newDecayTime = decayTimeKnob.getValue() * 10 * 3000;
            reverb.setDecay(newDecayTime.doubleValue());
        });
        
        diffusionKnob.valueProperty().addListener((ObservableValue<? extends Number> diffusion, Number oldDiffusion, Number newDiffusion) -> {
            newDiffusion = diffusionKnob.getValue() * 10 * 300;
            reverb.setDiffusion(newDiffusion.intValue());
        });
        
        wetDryKnob.valueProperty().addListener((ObservableValue<? extends Number> wetDryFactor, Number oldWetDryFactor, Number newWetDryFactor) -> {
            newWetDryFactor = wetDryKnob.getValue();
            reverb.setWetDryFactor(newWetDryFactor.doubleValue());
        });
        
//        // Set the visual components/max and min values of the sliders
//        preDelaySlider.setMin(1);
//        preDelaySlider.setMax(9.9);
//        preDelaySlider.setShowTickMarks(true);
//        preDelaySlider.setMajorTickUnit(1);
//        preDelaySlider.setMinorTickCount(0);
//        preDelaySlider.setShowTickLabels(true);
//        preDelaySlider.setSnapToTicks(true);
//        preDelaySlider.setValue(1);
//        
//        decayTimeSlider.setMin(1);
//        decayTimeSlider.setMax(10);
//        decayTimeSlider.setShowTickMarks(true);
//        decayTimeSlider.setMajorTickUnit(1);
//        decayTimeSlider.setMinorTickCount(0);
//        decayTimeSlider.setShowTickLabels(true);
//        decayTimeSlider.setSnapToTicks(true);
//        decayTimeSlider.setValue(1);
//        
//        diffusionSlider.setMin(1);
//        diffusionSlider.setMax(10);
//        diffusionSlider.setShowTickMarks(true);
//        diffusionSlider.setMajorTickUnit(1);
//        diffusionSlider.setMinorTickCount(0);
//        diffusionSlider.setShowTickLabels(true);
//        diffusionSlider.setSnapToTicks(true);
//        diffusionSlider.setValue(2);
//        
//        wetDrySlider.setMin(1);
//        wetDrySlider.setMax(10);
//        wetDrySlider.setShowTickMarks(true);
//        wetDrySlider.setMajorTickUnit(1);
//        wetDrySlider.setMinorTickCount(0);
//        wetDrySlider.setShowTickLabels(true);
//        wetDrySlider.setValue(5);
//        
//        // Set listeners and actions for sliders and buttons
//        preDelaySlider.valueProperty().addListener((ObservableValue<? extends Number> preDelay, Number oldPredelay, Number newPreDelay) -> {
//            reverb.setPreDelay(newPreDelay.intValue()*500);
//        });
//        
//        decayTimeSlider.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldDecayTime, Number newDecayTime) -> {
//            reverb.setDecay(newDecayTime.doubleValue()*3000);
//        });
//        
//        wetDrySlider.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldWetDryFactor, Number newWetDryFactor) -> {
//            reverb.setWetDryFactor(newWetDryFactor.doubleValue()/10);
//
//        });
//        
//        diffusionSlider.valueProperty().addListener((ObservableValue<? extends Number> diffusion, Number oldDiffusion, Number newDiffusion) -> {
//            reverb.setDiffusion(newDiffusion.intValue()*300);
//        });
        
        // Play the audio
        playButton.setOnAction(e -> {
                reverb.setReverbEffect();
        });
        
        // Reset to initial values
        resetButton.setOnAction(e -> {
            reverb.stopAudio();
            reverb = new Reverb(1000, 10000, 2000, 0.5);
            preDelaySlider.setValue(1);
            decayTimeSlider.setValue(1);
            diffusionSlider.setValue(2);
            wetDrySlider.setValue(5);
        });
    }
}
