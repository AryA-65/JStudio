package org.JStudio.Plugins.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.JStudio.Plugins.Models.Echo;
import org.JStudio.Plugins.Views.EchoStage;
import org.JStudio.UI.Knob;
import static org.JStudio.UI.Knob.Type.REG;

/**
 * FXML controller class for the Echo UI
 * @author Theodore Georgiou
 */
public class EchoFXMLController {
    @FXML
    private Button resetButton, playButton, saveButton;
    @FXML
    private GridPane grid;
    private final Knob preDelayKnob = new Knob(100, true, 0.1, REG);
    private final Knob decayTimeKnob = new Knob(100, false, 0, REG);
    private final Knob diffusionKnob = new Knob(100, true, 0.1, REG);
    private final Knob echoNumKnob = new Knob(100, true, 0.1, REG);
    private final Knob wetDryKnob = new Knob(100, false, 0, REG);
    private final Knob outputGainKnob = new Knob(100, false, 0, REG);
    private static EchoStage window;
    private Echo echo;
    
    /**
     * Initializes the UI and sets actions for the knobs and buttons
     */
    @FXML
    public void initialize() {
        echo = new Echo(1000, 1/11, 10000, 5, 0.5); // Create a echo
        
        preDelayKnob.setValue(0.1);
        decayTimeKnob.setValue(0.1);
        diffusionKnob.setValue(0.2);
        echoNumKnob.setValue(0.5);
        wetDryKnob.setValue(0.5);
        outputGainKnob.setValue(1);
        preDelayKnob.setTranslateX(5);
        decayTimeKnob.setTranslateX(5);
        diffusionKnob.setTranslateX(5);
        echoNumKnob.setTranslateX(5);
        wetDryKnob.setTranslateX(5);
        outputGainKnob.setTranslateX(5);
        
        grid.add(preDelayKnob, 0, 0);
        grid.add(decayTimeKnob, 1, 0);
        grid.add(diffusionKnob, 2, 0);
        grid.add(echoNumKnob, 3, 0);
        grid.add(wetDryKnob, 4, 0);
        grid.add(outputGainKnob, 5, 0);
        
        preDelayKnob.valueProperty().addListener((ObservableValue<? extends Number> preDelay, Number oldPredelay, Number newPreDelay) -> {
            newPreDelay = preDelayKnob.getValue() * 10 * 1000;
            echo.setPreDelay(newPreDelay.intValue());
        });
        
        decayTimeKnob.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldDecayTime, Number newDecayTime) -> {
            newDecayTime = decayTimeKnob.getValue() * 10 * 1/11;
            echo.setDecay(newDecayTime.doubleValue());
        });
        
        diffusionKnob.valueProperty().addListener((ObservableValue<? extends Number> diffusion, Number oldDiffusion, Number newDiffusion) -> {
            newDiffusion = diffusionKnob.getValue() * 10 * 5000;
            echo.setDiffusion(newDiffusion.intValue());
        });
        
        echoNumKnob.valueProperty().addListener((ObservableValue<? extends Number> echoNum, Number oldEchoNum, Number newEchoNum) -> {
            newEchoNum = echoNumKnob.getValue() * 10;
            echo.setEchoNum(newEchoNum.intValue());
        });
        
        wetDryKnob.valueProperty().addListener((ObservableValue<? extends Number> wetDryFactor, Number oldWetDryFactor, Number newWetDryFactor) -> {
            newWetDryFactor = wetDryKnob.getValue();
            echo.setWetDryFactor(newWetDryFactor.doubleValue());
        });
        
        outputGainKnob.valueProperty().addListener((ObservableValue<? extends Number> outputGain, Number oldOutputGain, Number newOutputGain) -> {
            newOutputGain = outputGainKnob.getValue();
            echo.setOutputGain(newOutputGain.doubleValue());
        });
        
        // Play the audio
        playButton.setOnAction(e -> {
            echo.setEchoEffect();
            echo.play();
        });
        
        // Reset to initial values
        resetButton.setOnAction(e -> {
            echo.stopAudio();
            echo.clearFinalAudio();
            echo = new Echo(1000, 1/11, 10000, 5, 0.5);
            preDelayKnob.setValue(0.1);
            decayTimeKnob.setValue(0.1);
            diffusionKnob.setValue(0.2);
            echoNumKnob.setValue(0.5);
            wetDryKnob.setValue(0.5);
            outputGainKnob.setValue(1);
        });
        
        // Saving
        saveButton.setOnAction(e -> {
            echo.setEchoEffect();
            echo.stopAudio();
            echo.setFloatOutput(echo.convertByteToFloatArray(echo.getFinalAudio()));
            EchoFXMLController.window.close();
        });
        
        // Closing the plugin
        EchoFXMLController.window.setOnCloseRequest(e ->{
            if (echo.getAudioLine() != null) {
                echo.getAudioLine().close();
                echo.clearFinalAudio();
            }
        });
    }
    
    /**
     * Assigns an echo window object to the echo controller
     * @param window the echo window to be assigned to the controller
     */
    public static void setWindow(EchoStage window) {
        EchoFXMLController.window = window;
    }
}
