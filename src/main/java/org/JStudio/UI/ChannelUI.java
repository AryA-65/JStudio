package org.JStudio.UI;

import javafx.animation.AnimationTimer;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.JStudio.Core.Mixer;
import org.JStudio.Core.Track;

import java.util.function.Function;

public class ChannelUI extends VBox {
    private final Slider vol = new Slider(0,1,1);
    private final Knob pitch = new Knob(24, false, 0, Knob.Type.BIP), pan = new Knob(32, false, 0, Knob.Type.BIP);
    private final Label id = new Label();
    private final Pane visBackground = new Pane();
    private final VBox container = new VBox();
    private final StackPane visContainer = new StackPane();
    private final Canvas canvas = new Canvas(16,40);
    private final MutedBTN mutedBtn;
    private final GraphicsContext gc = canvas.getGraphicsContext2D();
    private final FloatProperty leftAmp = new SimpleFloatProperty(0), rightAmp = new SimpleFloatProperty(0);
    private AnimationTimer timer;
    private long nextDrawTime = 0;
    private final long FRAME_RATE_INTER = 41666666L;

    public ChannelUI(Object input, FloatProperty leftAmp, FloatProperty rightAmp) {
        setPrefSize(32, 256);
        setId("track_channel");
        setAlignment(Pos.TOP_CENTER);

        this.leftAmp.bindBidirectional(leftAmp);
        this.rightAmp.bindBidirectional(rightAmp);

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

        Rectangle visClip = new Rectangle(18, 42);
        visClip.setArcHeight(10);
        visClip.setArcWidth(10);
        canvas.setClip(visClip);

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

//        timer = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                if (now < nextDrawTime) return;
//                nextDrawTime = now + FRAME_RATE_INTER;
//
//                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//
//                double midX = visContainer.getWidth() / 2;
//
//                gc.setFill(Color.GREEN);
//
//                //left amp
//                gc.fillRect(0, canvas.getHeight() - (canvas.getHeight() * leftAmp.get()), midX, canvas.getHeight() * leftAmp.get() * 2);
//                //right amp
//                gc.fillRect(midX, canvas.getHeight() - (canvas.getHeight() * rightAmp.get()), midX, canvas.getHeight() * rightAmp.get() * 2);
//            }
//        };
//
//        timer.start();
    }
}
