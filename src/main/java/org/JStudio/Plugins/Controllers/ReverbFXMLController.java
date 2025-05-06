package org.JStudio.Plugins.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.JStudio.Plugins.Models.Reverb;
import org.JStudio.Plugins.Views.ReverbStage;
import org.JStudio.Plugins.Views.SpectrographStage;
import org.JStudio.SettingsController;
import org.JStudio.UI.Knob;
import static org.JStudio.UI.Knob.Type.REG;
import org.JStudio.Utils.AlertBox;

/**
 * FXML controller class for the Reverb UI
 * @author Theodore Georgiou
 */
public class ReverbFXMLController {
    @FXML
    private Button resetButton, playButton, saveButton;
    @FXML
    private GridPane grid;
    private final Knob preDelayKnob = new Knob(100, true, 0.1, REG);
    private final Knob decayTimeKnob = new Knob(100, false, 0, REG);
    private final Knob diffusionKnob = new Knob(100, true, 0.1, REG);
    private final Knob wetDryKnob = new Knob(100, false, 0, REG);
    private final Knob outputGainKnob = new Knob(100, false, 0, REG);
    private static ReverbStage window;
    private Reverb reverb;
    
    /**
     * Initializes the UI and sets actions for the knobs and buttons
     */
    @FXML
    public void initialize() {
        reverb = new Reverb(1000, 10000, 2000, 0.5); // Create a reverb
        
        preDelayKnob.setTranslateX(15);
        decayTimeKnob.setTranslateX(15);
        diffusionKnob.setTranslateX(15);
        wetDryKnob.setTranslateX(15);
        outputGainKnob.setTranslateX(15);
        preDelayKnob.setValue(0.1);
        decayTimeKnob.setValue(0.1);
        diffusionKnob.setValue(0.2);
        wetDryKnob.setValue(0.5);
        outputGainKnob.setValue(1);
        
        grid.add(preDelayKnob, 0, 0);
        grid.add(decayTimeKnob, 1, 0);
        grid.add(diffusionKnob, 2, 0);
        grid.add(wetDryKnob, 3, 0);
        grid.add(outputGainKnob, 4, 0);
        
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
        
        outputGainKnob.valueProperty().addListener((ObservableValue<? extends Number> outputGain, Number oldOutputGain, Number newOutputGain) -> {
            newOutputGain = outputGainKnob.getValue();
            reverb.setOutputGain(newOutputGain.doubleValue());
        });
        
        // Play the audio
        playButton.setOnAction(e -> {
            reverb.setReverbEffect();
            reverb.play();
        });
        
        // Reset to initial values
        resetButton.setOnAction(e -> {
            reverb.stopAudio();
            reverb.clearFinalAudio();
            reverb = new Reverb(1000, 10000, 2000, 0.5);
            preDelayKnob.setValue(0.1);
            decayTimeKnob.setValue(0.1);
            diffusionKnob.setValue(0.2);
            wetDryKnob.setValue(0.5);
            outputGainKnob.setValue(1);
        });
        
        // Saving
        saveButton.setOnAction(e -> {
            reverb.setReverbEffect();
            reverb.stopAudio();
            reverb.setFloatOutput(reverb.convertByteToFloatArray(reverb.getFinalAudio()));
            reverb.export("Reverb");
            
            //runs visualizer if testing
            if (SettingsController.isTesting()) {
                SpectrographStage spectrographStage = new SpectrographStage();
                try {
                    if (SpectrographStage.controller != null) {
                        SpectrographStage.controller.setArrays(reverb.getOriginalAudio(), reverb.getFinalAudio());
                    }
                } catch (Exception ex) {
                    AlertBox.display("Export Error", "Failed to load Unit Testing interface.");
                }
                spectrographStage.show();
            }
            
            ReverbFXMLController.window.close();
        });
        
        // Closing the plugin
        ReverbFXMLController.window.setOnCloseRequest(e ->{
            if (reverb.getAudioLine() != null) {
                reverb.getAudioLine().close();
                reverb.clearFinalAudio();
            }
        });
    }

    /**
     * Assigns a reverb window object to the reverb controller
     * @param window the reverb window to be assigned to the controller
     */
    public static void setWindow(ReverbStage window) {
        ReverbFXMLController.window = window;
    }
}
