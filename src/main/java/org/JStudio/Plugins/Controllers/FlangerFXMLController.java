package org.JStudio.Plugins.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.JStudio.Plugins.Models.Modulation;
import org.JStudio.Plugins.Views.FlangerStage;
import org.JStudio.UI.Knob;
import static org.JStudio.UI.Knob.Type.REG;

/**
 * FXML controller class for the Flanger UI
 * @author Theodore Georgiou
 */
public class FlangerFXMLController {
    @FXML
    private Button resetButton, playButton, saveButton;
    @FXML
    private GridPane grid;
    private final Knob frequencyKnob = new Knob(100, false, 0, REG);
    private final Knob deviationKnob = new Knob(100, true, 0.1, REG);
    private final Knob wetDryKnob = new Knob(100, false, 0, REG);
    private final Knob outputGainKnob = new Knob(100, false, 0, REG);
    private static FlangerStage window;
    private Modulation flanger;
    
    /**
     * Initializes the UI and sets actions for the knobs and buttons
     */
    @FXML
    public void initialize() {
        flanger = new Modulation(100000, 100, 0.5); // Create a flanger
        flanger.setName("Flanger");
        
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
            newFrequency = frequencyKnob.getValue() * 10 * 50000;
            flanger.setFrequency(newFrequency.intValue());
        });
        
        deviationKnob.valueProperty().addListener((ObservableValue<? extends Number> deviation, Number oldDeviation, Number newDeviation) -> {
            newDeviation = deviationKnob.getValue() * 10 * 50;
            flanger.setDeviation(newDeviation.intValue());
        });
        
        wetDryKnob.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldWetDryFactor, Number newWetDryFactor) -> {
            newWetDryFactor = wetDryKnob.getValue();
            flanger.setWetDryFactor(newWetDryFactor.doubleValue());
        });
        
        outputGainKnob.valueProperty().addListener((ObservableValue<? extends Number> outputGain, Number oldOutputGain, Number newOutputGain) -> {
            newOutputGain = outputGainKnob.getValue();
            flanger.setOutputGain(newOutputGain.doubleValue());
        });
        
        // Play the audio
        playButton.setOnAction(e -> {
            flanger.setModulationEffect();
            flanger.play();
        });
        
        // Reset to initial values
        resetButton.setOnAction(e -> {
            flanger.stopAudio();
            flanger.clearFinalAudio();
            flanger = new Modulation(100000, 100, 0.5);
            frequencyKnob.setValue(0.2);
            deviationKnob.setValue(0.2);
            wetDryKnob.setValue(0.5);
            outputGainKnob.setValue(1);
        });
        
        // Saving
        saveButton.setOnAction(e -> {
            flanger.setModulationEffect();
            flanger.stopAudio();
            flanger.setFloatOutput(flanger.convertByteToFloatArray(flanger.getFinalAudio()));
            FlangerFXMLController.window.close();
        });
        
        // Closing the plugin
        FlangerFXMLController.window.setOnCloseRequest(e ->{
            if (flanger.getAudioLine() != null) {
                flanger.getAudioLine().close();
                flanger.clearFinalAudio();
            }
        });
    }
    
    /**
     * Assigns a flanger window object to the flanger controller
     * @param window the flanger window to be assigned to the controller
     */
    public static void setWindow(FlangerStage window) {
        FlangerFXMLController.window = window;
    }
}
