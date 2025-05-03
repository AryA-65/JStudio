package org.JStudio.Plugins.Views;

import java.io.File;
import org.JStudio.Plugins.Models.EqualizerBand;
import org.JStudio.Plugins.Controllers.EqualizerController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EqualizerView extends Pane {

    private EqualizerController eqController;
    private EqualizerBand[] eqBands = new EqualizerBand[10];
    private Stage stage;

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

    public EqualizerView() {
        //get audio file
        //File file = new FileChooser().showOpenDialog(null);
        
        //create equalizer UI
        VBox vb = new VBox();

        Button playButton = new Button("Play");
        Button stopButton = new Button("Stop");
        Button exportButton = new Button("Export");

        playButton.setOnAction(e -> {
            //disable the play button, enable the stop button, and create a controller to modify and play the audio
            playButton.setDisable(true);
            eqController = new EqualizerController();
            eqController.getEqualizer().setStage(stage);
            eqController.getEqualizer().setEqView(this);
            eqController.processAudio(eqController.getAudioFloatInput());
            eqController.play();
            stopButton.setDisable(false);
        });

        stopButton.setDisable(true); //start the stop button as disabled
        stopButton.setOnAction(e -> {
            //disable the stop button, enable the play button, and stop the audio
            stopButton.setDisable(true);
            eqController.stopAudio();
            playButton.setDisable(false);
        });
        
        exportButton.setOnAction(e -> {
            //disable the stop button, enable the play button, and stop the audio
            stopButton.setDisable(true);
            playButton.setDisable(true);
            //eqController.export();
        });

        HBox hbButtons = new HBox();

        hbButtons.getChildren().addAll(playButton, stopButton, exportButton);

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
