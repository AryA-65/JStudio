package org.JStudio.Views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.JStudio.Models.Core.Mixer;
import org.JStudio.Controllers.UIController;

/**
 * UI handler for the mixer
 */
public class MixerUI extends HBox {
    private final ChannelUI channel;
    private final VBox leftContainer = new VBox();
    private final Label masterLabel = new Label("Out");
    private final StackPane masterVisContainer = new StackPane();
    private final Canvas masterVis = new Canvas();
    private final GraphicsContext gc = masterVis.getGraphicsContext2D(); //add visualization of audio

    /**
     * used in the channel rack for the mixer class and its outputs
     * @param mixer input is the mixer
     */
    public MixerUI(Mixer mixer) {
        setPrefHeight(256);
        setPrefWidth(64);
        setId("mixer");
        setAlignment(Pos.TOP_CENTER);

        channel = new ChannelUI(mixer, mixer.getLeftAmp(), mixer.getRightAmp());

        leftContainer.setPrefHeight(256);
        leftContainer.setPrefWidth(32);
        leftContainer.setAlignment(Pos.TOP_CENTER);

        masterLabel.setFont(new Font("Inter Regular", 8));
        VBox.setMargin(masterLabel, new Insets(2, 0, 2, 0));

        masterVisContainer.setPrefSize(18,243);
        masterVisContainer.setId("masterVis");
        VBox.setMargin(masterVisContainer, new Insets(0,5,0,5));

        masterVisContainer.getChildren().add(masterVis);
        leftContainer.getChildren().addAll(masterLabel, masterVisContainer);

        getChildren().addAll(leftContainer, channel);
    }

}
