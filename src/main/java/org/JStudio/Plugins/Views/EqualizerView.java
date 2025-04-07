/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.JStudio.Plugins.Views;

import org.JStudio.Plugins.Models.EqualizerBand;
import org.JStudio.Plugins.Controllers.EqualizerController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author alexa
 */
public class EqualizerView extends Pane {

    EqualizerController eqController;
    EqualizerBand[] eqBands = new EqualizerBand[10];

    public EqualizerBand[] getEqBands() {
        return eqBands;
    }

    public EqualizerView() {
        VBox vb = new VBox();

        Button playButton = new Button("Play");
        Button stopButton = new Button("Stop");

        playButton.setOnAction(e -> {
            playButton.setDisable(true);
            eqController = new EqualizerController();
            eqController.setEqView(this);
            eqController.start();
            stopButton.setDisable(false);
        });

        stopButton.setDisable(true);
        stopButton.setOnAction(e -> {
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
            eqBands[i] = new EqualizerBand((int) Math.pow(2, n));
            hbBands.getChildren().add(eqBands[i]);
            
            Label newLabel = new Label(eqBands[i].getCenterFrequency() + " Hz");
            newLabel.setPrefWidth(eqBands[i].getPrefWidth());
            hbLabels.getChildren().add(newLabel);
            
            n++;
        }
        
        
        

        vb.getChildren().addAll(hbButtons, hbBands, hbLabels);
        this.getChildren().add(vb);
    }

}
