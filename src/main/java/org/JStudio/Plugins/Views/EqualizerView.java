package org.JStudio.Plugins.Views;

import javafx.event.Event;
import javafx.geometry.Orientation;
import org.JStudio.Plugins.Models.EqualizerBand;
import org.JStudio.Plugins.Controllers.EqualizerController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.JStudio.Plugins.Models.Equalizer;
import org.JStudio.SettingsController;
import org.JStudio.Utils.AlertBox;

public class EqualizerView extends Pane {

    private EqualizerController eqController = new EqualizerController();;
    private EqualizerBand[] eqBands = new EqualizerBand[10];
    private Stage stage;
    private Slider outputGainSlider = new Slider();

    //returns all the equalizer bands
    public EqualizerBand[] getEqBands() {
        return eqBands;
    }
    
    public EqualizerController getEqualizerController(){
        return eqController;
    }
    
    public void setStage(Stage stage){
        this.stage = stage;
    }
    
    public Slider getOutputGainSlider(){
        return outputGainSlider;
    }

    public EqualizerView() {
        //create equalizer
        Equalizer equalizer = new Equalizer();
        
        //initialize values in equalizer controller
        eqController.setEqualizer(equalizer);
        eqController.getEqualizer().setStage(stage);
        eqController.getEqualizer().setEqView(this);
        
        //create equalizer UI
        VBox vb = new VBox();

        Button playButton = new Button("Play");
        Button stopButton = new Button("Stop");
        Button exportButton = new Button("Export");
        
        //set properties of output gain slider
        outputGainSlider.setValue(1);
        outputGainSlider.setOrientation(Orientation.HORIZONTAL);
        outputGainSlider.setPrefWidth(200);
        outputGainSlider.setMin(0);
        outputGainSlider.setMax(1);
        outputGainSlider.setMajorTickUnit(0.1);
        outputGainSlider.setShowTickMarks(true);
        outputGainSlider.setShowTickLabels(true);

        playButton.setOnAction(e -> {
            //disable the play button, enable the stop button, and create a controller to modify and play the audio
            playButton.setDisable(true);
            eqController.processAudio(eqController.getEqualizer().getAudioFloatInput());
            eqController.getEqualizer().play();
            stopButton.setDisable(false);
        });

        stopButton.setDisable(true); //start the stop button as disabled
        stopButton.setOnAction(e -> {
            //disable the stop button, enable the play button, and stop the audio
            stopButton.setDisable(true);
            eqController.getEqualizer().stopAudio();
            playButton.setDisable(false);
        });
        
        exportButton.setOnAction(event -> {
            //processAudio
            eqController.processAudio(eqController.getEqualizer().getAudioFloatInput());
            
            //export teh audio to a wav file
            equalizer.export("Equalizer");
            
            //runs visualizer if testing
            if (SettingsController.isTesting()) {
                stage.close();
                SpectrographStage spectrographStage = new SpectrographStage();
                try {
                    if (SpectrographStage.controller != null) {
                        SpectrographStage.controller.setArrays(equalizer.getOriginalAudio(), equalizer.getFinalAudio());
                    }
                } catch (Exception e) {
                    AlertBox.display("Export Error", "Failed to load Unit Testing interface.");
                }
                spectrographStage.show();
            }
            
            //close the stage/window
            WindowEvent closeEvent = new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST);
            Event.fireEvent(stage, closeEvent);
        });

        HBox hbButtons = new HBox();
        hbButtons.setSpacing(20);

        hbButtons.getChildren().addAll(playButton, stopButton, exportButton, outputGainSlider);

        HBox hbBands = new HBox();
        HBox hbLabels = new HBox();

        int n = 5;
        for (int i = 0; i < 10; i++) {
            //add the equalizer bands
            eqBands[i] = new EqualizerBand((int) Math.pow(2, n));
            hbBands.getChildren().add(eqBands[i]);
            
            //add labels for the equalizer bands that correspond to the frequency they modify
            Label newLabel = new Label(eqBands[i].getCenterFrequency() + " Hz");
            newLabel.setPrefWidth(eqBands[i].getPrefWidth());
            hbLabels.getChildren().add(newLabel);
            
            n++;
        }
        
        vb.getChildren().addAll(hbButtons, hbBands, hbLabels);
        this.getChildren().add(vb);
    }

}
