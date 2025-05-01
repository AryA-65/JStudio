package org.JStudio.Plugins.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import org.JStudio.Plugins.Models.Modulation;
import org.JStudio.Plugins.Views.ChorusStage;
import org.JStudio.UI.Knob;
import static org.JStudio.UI.Knob.Type.REG;

/**
 * FXML controller class for the Chorus UI
 * @author Theodore Georgiou
 */
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
    @FXML
    private GridPane grid;
    private final Knob frequencyKnob = new Knob(100, false, 0, REG);
    private final Knob deviationKnob = new Knob(100, true, 0.1, REG);
    private final Knob wetDryKnob = new Knob(100, false, 0, REG);
    private static ChorusStage window;
    private Modulation chorus;
    
    /**
     * Initializes the UI and sets actions for the knobs and buttons
     */
    @FXML
    public void initialize() {
        chorus = new Modulation(20000, 200, 0.5); // Create a chorus
        chorus.setName("Chorus");
        
        frequencyKnob.setTranslateX(25);
        deviationKnob.setTranslateX(25);
        wetDryKnob.setTranslateX(25);
        
        grid.add(frequencyKnob, 0, 0);
        grid.add(deviationKnob, 1, 0);
        grid.add(wetDryKnob, 2, 0);

        frequencyKnob.valueProperty().addListener((ObservableValue<? extends Number> frequency, Number oldFrequency, Number newFrequency) -> {
            newFrequency = frequencyKnob.getValue() * 10 * 10000;
            chorus.setFrequency(newFrequency.intValue());
        });
        
        deviationKnob.valueProperty().addListener((ObservableValue<? extends Number> deviation, Number oldDeviation, Number newDeviation) -> {
            newDeviation = deviationKnob.getValue() * 10 * 100;
            chorus.setDeviation(newDeviation.intValue());
        });
        
        wetDryKnob.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldWetDryFactor, Number newWetDryFactor) -> {
            newWetDryFactor = wetDryKnob.getValue();
            chorus.setWetDryFactor(newWetDryFactor.doubleValue());
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
//            chorus.setFrequency(newFrequency.intValue()*10000);
//        });
//        
//        deviationSlider.valueProperty().addListener((ObservableValue<? extends Number> deviation, Number oldDeviation, Number newDeviation) -> {
//            chorus.setDeviation(newDeviation.intValue()*100);
//        });
//        
//        wetDrySlider.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldWetDryFactor, Number newWetDryFactor) -> {
//            chorus.setWetDryFactor(newWetDryFactor.doubleValue()/10);
//        });
        
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
        
        ChorusFXMLController.window.setOnCloseRequest(e ->{
            if (chorus.getAudioLine() != null) {
                chorus.getAudioLine().close();
            }
        });
    }
    
    /**
     * Assigns a chorus window object to the chorus controller
     * @param window the chorus window to be assigned to the controller
     */
    public static void setWindow(ChorusStage window) {
        ChorusFXMLController.window = window;
    }
}
