package org.JStudio.Plugins.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import org.JStudio.Plugins.Models.PhaserPlugin;
import org.JStudio.Plugins.Views.PhaserStage;
import org.JStudio.UI.Knob;
import static org.JStudio.UI.Knob.Type.REG;

/**
 * FXML controller class for the Phaser UI
 * @author Theodore Georgiou
 */
public class PhaserFXMLController {
//    @FXML
//    private Slider frequencySlider;
//    @FXML
//    private Slider deviationSlider;
//    @FXML
//    private Slider wetDrySlider;
    @FXML
    private Button resetButton;
    @FXML
    private Button playButton;
    @FXML
    private GridPane grid;
    private final Knob frequencyKnob = new Knob(100, false, 0, REG);
    private final Knob deviationKnob = new Knob(100, true, 0.2, REG);
    private final Knob wetDryKnob = new Knob(100, false, 0, REG);
    private static PhaserStage window;
    private PhaserPlugin phaser;
    
    /**
     * Initializes the UI and sets actions for the knobs and buttons
     */
    @FXML
    public void initialize() {
        phaser = new PhaserPlugin(100000, 8, 0.5); // Create a phaser
        
        deviationKnob.setValues(0.6, 1);
        frequencyKnob.setValue(0.2);
        deviationKnob.setValue(0.8);
        wetDryKnob.setValue(0.5);
        frequencyKnob.setTranslateX(25);
        deviationKnob.setTranslateX(25);
        wetDryKnob.setTranslateX(25);
        
        grid.add(frequencyKnob, 0, 0);
        grid.add(deviationKnob, 1, 0);
        grid.add(wetDryKnob, 2, 0);

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
        
//        frequencySlider.setMin(1);
//        frequencySlider.setMax(10);
//        frequencySlider.setShowTickMarks(true);
//        frequencySlider.setMajorTickUnit(1);
//        frequencySlider.setMinorTickCount(0);
//        frequencySlider.setShowTickLabels(true);
//        frequencySlider.setValue(2);
//        
//        deviationSlider.setMin(5);
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
//            phaser.setFrequency(newFrequency.intValue()*50000);
//
//        });
//        
//        deviationSlider.valueProperty().addListener((ObservableValue<? extends Number> deviation, Number oldDeviation, Number newDeviation) -> {
//            phaser.setDeviation(newDeviation.intValue());
//            
//        });
//        
//        wetDrySlider.valueProperty().addListener((ObservableValue<? extends Number> decayTime, Number oldWetDryFactor, Number newWetDryFactor) -> {
//            phaser.setWetDryFactor(newWetDryFactor.doubleValue()/10);
//        });
        
        // Play the audio
        playButton.setOnAction(e -> {
            phaser.setPhaserEffect();
        });
        
        // Reset to initial values
        resetButton.setOnAction(e -> {
            phaser.stopAudio();
            phaser = new PhaserPlugin(100000, 8, 0.5);
            frequencyKnob.setValue(0.2);
            deviationKnob.setValue(0.8);
            wetDryKnob.setValue(0.5);
//            frequencySlider.setValue(2);
//            deviationSlider.setValue(8);
//            wetDrySlider.setValue(5);
        });
        
        PhaserFXMLController.window.setOnCloseRequest(e ->{
            if (phaser.getAudioLine() != null) {
                phaser.getAudioLine().close();
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
