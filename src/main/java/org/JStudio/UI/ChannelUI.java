package org.JStudio.UI;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.JStudio.Core.Mixer;
import org.JStudio.Core.Track;

public class ChannelUI extends VBox {
    private final Object input;
    private final Slider vol = new Slider(0,100,100);
//    private final Node activeBtn = createActiveBTN();
    private final Knob pitch = new Knob(24, false, 0, Knob.Type.BIP), pan = new Knob(32, false, 0, Knob.Type.BIP);
    private final Label id = new Label();
//    private final Pane container = new Pane(), visBackground = new Pane();
    private final Pane visBackground = new Pane();
    private final VBox container = new VBox();
    private final StackPane visContainer = new StackPane();
    private final Canvas canvas = new Canvas(16,40);
    private final MutedBTN mutedBtn;
    private final GraphicsContext gc = canvas.getGraphicsContext2D();

    public ChannelUI(Object input) {
        this.input = input;

        setPrefSize(32, 256);
        setId("track_channel");
        setAlignment(Pos.TOP_CENTER);

        if (input instanceof Track) {
            id.setText(((Track) input).getId().get());
        } else if (input instanceof Mixer) {
            id.setText("Master");
        }
        id.setFont(new Font("Inter Regular", 8));
        VBox.setMargin(id, new Insets(2, 0, 2, 0));

        container.setPrefWidth(32);
        container.setPrefHeight(243);
        container.setId("channel_container");
        container.setAlignment(Pos.TOP_CENTER);
        container.setSpacing(4);

        Rectangle clip = new Rectangle(32, 96);
        clip.setArcHeight(10);
        clip.setArcWidth(10);
        visBackground.setPrefSize(32, 64);
        visBackground.setClip(clip);
        visBackground.setId("vis_background");

        visContainer.setPrefSize(18, 42);
        visContainer.setLayoutX(7);
        visContainer.setLayoutY(4);
        visContainer.setId("vis_container");

        pan.setTranslateY(48);
        pan.setTranslateX(0);
        pan.setId("pan_knob");

        if (input instanceof Track) {
            pan.valueProperty().bindBidirectional(((Track) input).getPan());
        } else if (input instanceof Mixer) {
            pan.valueProperty().bindBidirectional(((Mixer) input).getPan());
        }

        vol.setOrientation(Orientation.VERTICAL);
        vol.setCursor(Cursor.HAND);
        vol.setPrefHeight(108);
        vol.setMaxHeight(108);
        vol.setId("volume_slider");

        if (input instanceof Track) {
            vol.valueProperty().bindBidirectional(((Track) input).getAmplitude());
        } else if (input instanceof Mixer) {
            vol.valueProperty().bindBidirectional(((Mixer) input).getMasterGain());
        }

        mutedBtn = new MutedBTN(12, 224, input);

        pitch.setId("pitch_knob");
        VBox.setMargin(pitch, new Insets(0, 0, 5, 0));
        if (input instanceof Track) {
            pitch.valueProperty().bindBidirectional(((Track) input).getPitch());
        } else if (input instanceof Mixer) {
            pitch.valueProperty().bindBidirectional(((Mixer) input).getPitch());
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        visContainer.getChildren().add(canvas);
        visBackground.getChildren().addAll(visContainer, pan);
        container.getChildren().addAll(visBackground, spacer, vol, mutedBtn, pitch);
        getChildren().addAll(id, container);
    }


}
