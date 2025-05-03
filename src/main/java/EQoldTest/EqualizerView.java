package EQoldTest;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class EqualizerView extends Pane {

    EqualizerController eqController;
    EqualizerBand[] eqBands = new EqualizerBand[10];

    //returns all the equalizer bands
    public EqualizerBand[] getEqBands() {
        return eqBands;
    }

    public EqualizerView() {
        //create equalizer UI
        VBox vb = new VBox();

        Button playButton = new Button("Play");
        Button stopButton = new Button("Stop");

        playButton.setOnAction(e -> {
            //disable the play button, enable the stop button, and create a controller to modify and play the audio
            playButton.setDisable(true);
            eqController = new EqualizerController();
            eqController.setEqView(this);
            eqController.start(); //start a new thread that plays the audio
            stopButton.setDisable(false);
        });

        stopButton.setDisable(true); //start the stop button as disabled
        stopButton.setOnAction(e -> {
            //disable the stop button, enable the play button, and stop the audio
            stopButton.setDisable(true);
            eqController.stopPlaying();
            playButton.setDisable(false);
        });

        HBox hbButtons = new HBox();

        hbButtons.getChildren().addAll(playButton, stopButton);

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
