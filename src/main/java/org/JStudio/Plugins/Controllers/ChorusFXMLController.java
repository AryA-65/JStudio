package org.JStudio.Plugins.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.JStudio.Plugins.Models.Modulation;
import org.JStudio.Plugins.Models.Reverb;
import org.JStudio.Plugins.Views.ChorusStage;
import org.JStudio.Plugins.Views.SpectrographStage;
import org.JStudio.Controllers.SettingsController;
import org.JStudio.Views.Knob;
import static org.JStudio.Views.Knob.Type.REG;
import org.JStudio.Utils.AlertBox;

/**
 * FXML controller class for the Chorus UI
 * @author Theodore Georgiou
 */
public class ChorusFXMLController {
    @FXML
    private Button playButton, saveButton;
    @FXML
    private GridPane grid;
    private final Knob frequencyKnob = new Knob(100, false, 0, REG);
    private final Knob deviationKnob = new Knob(100, true, 0.1, REG);
    private final Knob wetDryKnob = new Knob(100, false, 0, REG);
    private final Knob outputGainKnob = new Knob(100, false, 0, REG);
    private static ChorusStage window;
    private Modulation chorus;
    
    //gets chorus plugin
    public Modulation getChorus(){
        return chorus;
    }
    
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
        
        // Saving
        saveButton.setOnAction(e -> {
            chorus.setModulationEffect();
            chorus.stopAudio();
            chorus.setFloatOutput(chorus.convertByteToFloatArray(chorus.getFinalAudio()));
            chorus.export("Chorus");
            
            //runs visualizer if testing
            if (SettingsController.isTesting()) {
                SpectrographStage spectrographStage = new SpectrographStage();
                try {
                    if (SpectrographStage.controller != null) {
                        SpectrographStage.controller.setArrays(chorus.getOriginalAudio(), chorus.getFinalAudio());
                    }
                } catch (Exception ex) {
                    AlertBox.display("Export Error", "Failed to load Unit Testing interface.");
                }
                spectrographStage.show();
            }
            
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
