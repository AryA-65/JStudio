package org.JStudio.Plugins.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import org.JStudio.Plugins.Models.Echo;
import org.JStudio.Plugins.Views.EchoStage;
import org.JStudio.UI.Knob;
import static org.JStudio.UI.Knob.Type.REG;

public class EchoFXMLController {
    @FXML
    private Button resetButton;
    @FXML
    private Button playButton;
//    @FXML
//    private Slider echoNumSlider;
//    @FXML
//    private Slider decayTimeSlider;
//    @FXML
//    private Slider diffusionSlider;
//    @FXML
//    private Slider preDelaySlider;
//    @FXML
//    private Slider wetDrySlider;
    @FXML
    private GridPane grid;
    private final Knob preDelayKnob = new Knob(100, true, 0.1, REG);
    private final Knob decayTimeKnob = new Knob(100, false, 0, REG);
    private final Knob diffusionKnob = new Knob(100, true, 0.1, REG);
    private final Knob echoNumKnob = new Knob(100, true, 0.1, REG);
    private final Knob wetDryKnob = new Knob(100, false, 0, REG);
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
        preDelayKnob.setTranslateX(5);
        decayTimeKnob.setTranslateX(5);
        diffusionKnob.setTranslateX(5);
        echoNumKnob.setTranslateX(5);
        wetDryKnob.setTranslateX(5);
        
        grid.add(preDelayKnob, 0, 0);
        grid.add(decayTimeKnob, 1, 0);
        grid.add(diffusionKnob, 2, 0);
        grid.add(echoNumKnob, 3, 0);
        grid.add(wetDryKnob, 4, 0);
        
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
//        echoNumSlider.setMin(1);
//        echoNumSlider.setMax(10);
//        echoNumSlider.setShowTickMarks(true);
//        echoNumSlider.setMajorTickUnit(1);
//        echoNumSlider.setMinorTickCount(0);
//        echoNumSlider.setShowTickLabels(true);
//        echoNumSlider.setSnapToTicks(true);
//        echoNumSlider.setValue(1);
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
//            echo.setPreDelay(newPreDelay.intValue()*1000);
//        });
//        
//        decayTimeSlider.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldDecayTime, Number newDecayTime) -> {
//            echo.setDecay(newDecayTime.doubleValue()/11);
//        });
//        
//        wetDrySlider.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldWetDryFactor, Number newWetDryFactor) -> {
//            echo.setWetDryFactor(newWetDryFactor.doubleValue()/10);
//
//        });
//        
//        diffusionSlider.valueProperty().addListener((ObservableValue<? extends Number> diffusion, Number oldDiffusion, Number newDiffusion) -> {
//            echo.setDiffusion(newDiffusion.intValue()*5000);
//        });
//        
//        echoNumSlider.valueProperty().addListener((ObservableValue<? extends Number> echoNum, Number oldEchoNum, Number newEchoNum) -> {
//            echo.setEchoNum(newEchoNum.intValue());
//        });
        
        // Play the audio
        playButton.setOnAction(e -> {
            echo.setEchoEffect();
        });
        
        // Reset to initial values
        resetButton.setOnAction(e -> {
            echo.stopAudio();
            echo = new Echo(1000, 1/11, 10000, 5, 0.5);
            preDelayKnob.setValue(0.1);
            decayTimeKnob.setValue(0.1);
            diffusionKnob.setValue(0.2);
            echoNumKnob.setValue(0.5);
            wetDryKnob.setValue(0.5);
//            preDelaySlider.setValue(1);
//            decayTimeSlider.setValue(1);
//            diffusionSlider.setValue(2);
//            echoNumSlider.setValue(5);
//            wetDrySlider.setValue(5);
        });
        
        EchoFXMLController.window.setOnCloseRequest(e ->{
            if (echo.getAudioLine() != null) {
                echo.getAudioLine().close();
            }
        });
    }
    
    /**
     * Assigns a reverb window object to the reverb controller
     * @param window the reverb window to be assigned to the controller
     */
    public static void setWindow(EchoStage window) {
        EchoFXMLController.window = window;
    }
}
