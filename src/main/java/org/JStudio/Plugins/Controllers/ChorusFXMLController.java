package org.JStudio.Plugins.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private Button resetButton, playButton, saveButton;
    @FXML
    private GridPane grid;
    private final Knob frequencyKnob = new Knob(100, false, 0, REG);
    private final Knob deviationKnob = new Knob(100, true, 0.1, REG);
    private final Knob wetDryKnob = new Knob(100, false, 0, REG);
    private final Knob outputGainKnob = new Knob(100, false, 0, REG);
    private static ChorusStage window;
    private Modulation chorus;
    
    /**
     * Initializes the UI and sets actions for the knobs and buttons
     */
    @FXML
    public void initialize() {
        chorus = new Modulation(20000, 200, 0.5); // Create a chorus
        chorus.setName("Chorus");
        
        frequencyKnob.setValue(0.2);
        deviationKnob.setValue(0.2);
        wetDryKnob.setValue(0.5);
        outputGainKnob.setValue(1);
        frequencyKnob.setTranslateX(15);
        deviationKnob.setTranslateX(15);
        wetDryKnob.setTranslateX(15);
        outputGainKnob.setTranslateX(15);
        
        grid.add(frequencyKnob, 0, 0);
        grid.add(deviationKnob, 1, 0);
        grid.add(wetDryKnob, 2, 0);
        grid.add(outputGainKnob, 3, 0);

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
        
        outputGainKnob.valueProperty().addListener((ObservableValue<? extends Number> outputGain, Number oldOutputGain, Number newOutputGain) -> {
            newOutputGain = outputGainKnob.getValue();
            chorus.setOutputGain(newOutputGain.doubleValue());
        });
        
        // Play the audio
        playButton.setOnAction(e -> {
            chorus.setModulationEffect();
            chorus.play();
        });
        
        // Reset to initial values
        resetButton.setOnAction(e -> {
            chorus.stopAudio();
            chorus.clearFinalAudio();
            chorus = new Modulation(20000, 200, 0.5);
            frequencyKnob.setValue(0.2);
            deviationKnob.setValue(0.2);
            wetDryKnob.setValue(0.5);
            outputGainKnob.setValue(1);
        });
        
        // Saving
        saveButton.setOnAction(e -> {
            chorus.setModulationEffect();
            chorus.stopAudio();
            chorus.setFloatOutput(chorus.convertByteToFloatArray(chorus.getFinalAudio()));
            ChorusFXMLController.window.close();
        });
        
        // Closing the plugin
        ChorusFXMLController.window.setOnCloseRequest(e ->{
            if (chorus.getAudioLine() != null) {
                chorus.getAudioLine().close();
                chorus.clearFinalAudio();
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
