package org.JStudio.Plugins.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import org.JStudio.Plugins.Models.Modulation;
import org.JStudio.UI.Knob;
import static org.JStudio.UI.Knob.Type.REG;

/**
 * FXML controller class for the Flanger UI
 * @author Theodore Georgiou
 */
public class FlangerFXMLController {
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
    @FXML
    private GridPane grid;
    Knob frequencyKnob = new Knob(100, false, 0, REG);
    Knob deviationKnob = new Knob(100, true, 0.1, REG);
    Knob wetDryKnob = new Knob(100, false, 0, REG);
    private Modulation flanger;
    
    /**
     * Initializes the UI, showing the tick marks on the slider and setting
     * actions for the sliders and buttons
     */
    @FXML
    public void initialize() {
        flanger = new Modulation(100000, 100, 0.5); // Create a flanger
        
        frequencyKnob.setTranslateX(52);
        deviationKnob.setTranslateX(52);
        wetDryKnob.setTranslateX(52);
        
        grid.add(frequencyKnob, 0, 1);
        grid.add(deviationKnob, 1, 1);
        grid.add(wetDryKnob, 2, 1);

        frequencyKnob.valueProperty().addListener((ObservableValue<? extends Number> frequency, Number oldFrequency, Number newFrequency) -> {
            newFrequency = frequencyKnob.getValue() * 10 * 50000;
            System.out.println(newFrequency);
            flanger.setFrequency(newFrequency.doubleValue());
        });
        
        deviationKnob.valueProperty().addListener((ObservableValue<? extends Number> deviation, Number oldDeviation, Number newDeviation) -> {
            newDeviation = deviationKnob.getValue() * 10 * 50;
            System.out.println(newDeviation);
            flanger.setDeviation(newDeviation.intValue());
        });
        
        wetDryKnob.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldWetDryFactor, Number newWetDryFactor) -> {
            newWetDryFactor = wetDryKnob.getValue();
            System.out.println(newWetDryFactor);
            flanger.setWetDryFactor(newWetDryFactor.doubleValue());
        });
        
//        frequencySlider.setMin(1);
//        frequencySlider.setMax(10);
//        frequencySlider.setShowTickMarks(true);
//        frequencySlider.setMajorTickUnit(1);
//        frequencySlider.setMinorTickCount(0);
//        frequencySlider.setShowTickLabels(true);
//        frequencySlider.setValue(2);
//        
//        deviationSlider.setMin(1);
//        deviationSlider.setMax(10);
//        deviationSlider.setShowTickMarks(true);
//        deviationSlider.setMajorTickUnit(1);
//        deviationSlider.setMinorTickCount(0);
//        deviationSlider.setShowTickLabels(true);
//        deviationSlider.setSnapToTicks(true);
//        deviationSlider.setValue(2);
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
//        frequencySlider.valueProperty().addListener((ObservableValue<? extends Number> frequency, Number oldFrequency, Number newFrequency) -> {
//            flanger.setFrequency(newFrequency.intValue()*50000);
//        });
//        
//        deviationSlider.valueProperty().addListener((ObservableValue<? extends Number> deviation, Number oldDeviation, Number newDeviation) -> {
//            flanger.setDeviation(newDeviation.intValue()*50);
//            
//        });
//        
//        wetDrySlider.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldWetDryFactor, Number newWetDryFactor) -> {
//            flanger.setWetDryFactor(newWetDryFactor.doubleValue()/10);
//        });
        
        // Play the audio
        playButton.setOnAction(e -> {
            flanger.setModulationEffect();
        });
        
        // Reset to initial values
        resetButton.setOnAction(e -> {
            flanger.stopAudio();
            flanger = new Modulation(100000, 100, 0.5);
            frequencySlider.setValue(2);
            deviationSlider.setValue(2);
            wetDrySlider.setValue(5);
        });
    }
}
