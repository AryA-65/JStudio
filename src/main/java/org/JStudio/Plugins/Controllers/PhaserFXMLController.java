package org.JStudio.Plugins.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.JStudio.Plugins.Models.Phaser;
import org.JStudio.Plugins.Views.PhaserStage;
import org.JStudio.Plugins.Views.SpectrographStage;
import org.JStudio.SettingsController;
import org.JStudio.UI.Knob;
import static org.JStudio.UI.Knob.Type.REG;
import org.JStudio.Utils.AlertBox;

/**
 * FXML controller class for the Phaser UI
 * @author Theodore Georgiou
 */
public class PhaserFXMLController {
    @FXML
    private Button resetButton, playButton, saveButton;
    @FXML
    private GridPane grid;
    private final Knob frequencyKnob = new Knob(100, false, 0, REG);
    private final Knob deviationKnob = new Knob(100, false, 0, REG);
    private final Knob wetDryKnob = new Knob(100, false, 0, REG);
    private final Knob outputGainKnob = new Knob(100, false, 0, REG);
    private static PhaserStage window;
    private Phaser phaser;
    
    /**
     * Initializes the UI and sets actions for the knobs and buttons
     */
    @FXML
    public void initialize() {
        phaser = new Phaser(100000, 8, 0.5); // Create a phaser
        
        deviationKnob.setValues(0.6, 1);
        frequencyKnob.setValue(0.2);
        deviationKnob.setValue(0.8);
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
            phaser.setFrequency(newFrequency.intValue());
        });
        
        deviationKnob.valueProperty().addListener((ObservableValue<? extends Number> deviation, Number oldDeviation, Number newDeviation) -> {
            newDeviation = deviationKnob.getValue() * 10;
            phaser.setDeviation(newDeviation.intValue());
        });
        
        wetDryKnob.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldWetDryFactor, Number newWetDryFactor) -> {
            newWetDryFactor = wetDryKnob.getValue();
            phaser.setWetDryFactor(newWetDryFactor.doubleValue());
        });
        
        outputGainKnob.valueProperty().addListener((ObservableValue<? extends Number> outputGain, Number oldOutputGain, Number newOutputGain) -> {
            newOutputGain = outputGainKnob.getValue();
            phaser.setOutputGain(newOutputGain.doubleValue());
        });
        
        // Play the audio
        playButton.setOnAction(e -> {
            phaser.setPhaserEffect();
            phaser.play();
        });
        
        // Reset to initial values
        resetButton.setOnAction(e -> {
            phaser.stopAudio();
            phaser.clearFinalAudio();
            phaser = new Phaser(100000, 8, 0.5);
            frequencyKnob.setValue(0.2);
            deviationKnob.setValue(0.8);
            wetDryKnob.setValue(0.5);
            outputGainKnob.setValue(1);
        });
        
        // Saving
        saveButton.setOnAction(e -> {
            phaser.setPhaserEffect();
            phaser.stopAudio();
            phaser.setFloatOutput(phaser.convertByteToFloatArray(phaser.getFinalAudio()));
            
            //runs visualizer if testing
            if (SettingsController.isTesting()) {
                SpectrographStage spectrographStage = new SpectrographStage();
                try {
                    if (SpectrographStage.controller != null) {
                        SpectrographStage.controller.setArrays(phaser.getOriginalAudio(), phaser.getFinalAudio());
                    }
                } catch (Exception ex) {
                    AlertBox.display("Export Error", "Failed to load Unit Testing interface.");
                }
                spectrographStage.show();
            }
            
            PhaserFXMLController.window.close();
        });
        
        // Closing the plugin
        PhaserFXMLController.window.setOnCloseRequest(e ->{
            if (phaser.getAudioLine() != null) {
                phaser.getAudioLine().close();
                phaser.clearFinalAudio();
            }
        });
    }
    
    /**
     * Assigns a phaser window object to the phaser controller
     * @param window the phaser window to be assigned to the controller
     */
    public static void setWindow(PhaserStage window) {
        PhaserFXMLController.window = window;
    }
}
