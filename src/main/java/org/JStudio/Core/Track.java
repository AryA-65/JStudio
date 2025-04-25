package org.JStudio.Core;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.JStudio.Plugins.Plugin;
import org.JStudio.UI.Knob;

public class Track {
    private String name;
    private short id;
    private final DoubleProperty amplitude = new SimpleDoubleProperty(1), pitch = new SimpleDoubleProperty(0);
    private final BooleanProperty activeTrack = new SimpleBooleanProperty(true);
    private List<Clip> clips = new ArrayList<>();
    private List<Plugin> plugins = new ArrayList<>();

    //test colors
    public final List<String> MATTE_COLORS = List.of(
            "#FF6B6B", // Matte Red
            "#FF9F5B", // Matte Orange
            "#FFD166", // Matte Yellow
            "#06D6A0", // Matte Green
            "#1B9AAA", // Matte Teal
            "#118AB2", // Matte Blue
            "#9A4C95", // Matte Purple
            "#EF476F", // Matte Pink
            "#8338EC", // Matte Violet
            "#FF5F7E"  // Matte Coral
    );

    public Track(String name) {
        this.name = name;
//        this.id = ;
    }

    public void addClip() {
        //empty for now
    }

    public void removeClip(int position) {
        for (Clip clip : clips) {
            if (position >= clip.getPosition() && position < clip.getPosition() + clip.getLength()) {
                clips.remove(clip);
                break;
            }
        }
    }

    public float[][] process() { //1024 chucks
        float[][] output = null;


        return output;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public DoubleProperty getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude.set(amplitude);
    }

    public DoubleProperty getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch.set(pitch);
    }

    public Canvas addTrack(int width) {
        Canvas canvas = new Canvas();
        canvas.setWidth(width);
        canvas.setHeight(64);

//        System.out.println(canvas.getWidth());

        GraphicsContext gc = canvas.getGraphicsContext2D();
//        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.GREY);
        gc.fillRoundRect(0, 0, canvas.getWidth(), canvas.getHeight(), 10, 10);

        gc.setStroke(Color.LIGHTGRAY);
        for (int i = 0; i < width; i++) {
            if (i % 32 == 0 && i != 0) {
                gc.strokeLine(i, 0, i, canvas.getHeight());
            }
        }

        canvas.setOnDragOver(e -> {
//            if (e.getGestureSource() != canvas && e.getDragboard().hasFiles()) {
//                e.acceptTransferModes(TransferMode.COPY);  // Accept the drop
//            }
            if (e.getGestureSource() != canvas && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.COPY);  // Accept the drop
            }
            e.consume();
        });

        canvas.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;

            if (db.hasString()) {
//                System.out.println("True");

                double dropX = e.getX();

                String string = db.getString();

//                System.out.println(string);
                double size = Double.parseDouble(string) * ((double) 120 / 60) * 32;

//                System.out.println(string);

                gc.setFill(Color.BLACK);
                gc.fillRoundRect(dropX, 0, size, canvas.getHeight(), 10, 10);

                success = true;
            }

            e.setDropCompleted(success);
            e.consume();
        });

        return canvas;
    }

    public Pane addTrackID() {
        String color = MATTE_COLORS.get(new Random().nextInt(MATTE_COLORS.size()));

        Pane container = new Pane();
        container.setPrefHeight(64);
        container.setPrefWidth(126);
        container.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5px");

        Label idLabel = new Label(String.valueOf(id));
        idLabel.setFont(new Font("Inter Regular", 8));
        idLabel.setLayoutX(4);
        idLabel.setLayoutY(4);

        container.setOnMouseClicked(e -> {
            //to change the name of the track
            if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
                //to open synth if available, if not it'll open plugin rack
            }
        });

        container.getChildren().addAll(idLabel, createActiveBTN(114, 52));

        return container;
    }

    public Node createChannel(byte i) {
        VBox channelBox = new VBox();
        channelBox.setPrefHeight(256);
        channelBox.setPrefWidth(32);
        channelBox.setStyle("-fx-background-color:  #D9D9D9; -fx-background-radius: 5px");
        channelBox.setAlignment(Pos.TOP_CENTER);

        Knob pan = new Knob(32, false, 0, Knob.Type.BIP);
        pan.setLayoutX(0);
        pan.setLayoutY(48);

        Label channelID = new Label(String.valueOf(i + 1));
        channelID.setFont(new Font("Inter Regular", 8));
        VBox.setMargin(channelID, new Insets(2, 0, 2, 0));

        Pane channelContainer = new Pane();
        channelContainer.setPrefHeight(243);
        channelContainer.setPrefWidth(32);
        channelContainer.setStyle("-fx-background-color: #404040; -fx-background-radius: 5px");

        Pane channelVisContainer = new Pane();
        channelVisContainer.setPrefHeight(64);
        channelVisContainer.setPrefWidth(32);
        channelVisContainer.setStyle("-fx-background-color: #808080; -fx-background-radius: 5px");

        StackPane visContainer = new StackPane();
        visContainer.setPrefSize(18,42);
        visContainer.setLayoutX(7);
        visContainer.setLayoutY(4);
        visContainer.setStyle("-fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 5px");

        Canvas channelVis = new Canvas();
        channelVis.setHeight(40);
        channelVis.setWidth(16);

//        JFXSlider channelAmp = new JFXSlider(0, 100, 100);
        Slider channelAmp = new Slider(0,100,100);
        channelAmp.setOrientation(Orientation.VERTICAL);
        channelAmp.setPrefHeight(96);
        channelAmp.setLayoutY(90);
        channelAmp.setLayoutX(9);

        channelAmp.valueProperty().bindBidirectional(amplitude);

        Knob pitch_knob = new Knob(24, false, 0, Knob.Type.BIP);
        pitch_knob.setLayoutX(4);
        pitch_knob.setLayoutY(192);
        pitch_knob.valueProperty().bindBidirectional(pitch);

        visContainer.getChildren().add(channelVis);

        channelVisContainer.getChildren().add(visContainer);

        channelContainer.getChildren().addAll(channelVisContainer, pan, channelAmp, createActiveBTN(12, 224), pitch_knob);

        channelBox.getChildren().addAll(channelID, channelContainer);

//        System.out.println(channelContainer.getHeight());

        return channelBox;
    }

    private Node createActiveBTN(double layoutX, double layoutY) {
        Pane activeBtn = new Pane(); //maybe use a radio button instead
        activeBtn.setPrefSize(8,8);
        activeBtn.setLayoutX(layoutX);
        activeBtn.setLayoutY(layoutY);
        activeBtn.toFront();
        activeBtn.getStyleClass().add("active");
        activeBtn.setStyle("-fx-background-color: #00FD11; -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");

        activeTrack.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                activeBtn.setStyle("-fx-background-color: #00FD11; -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");
            } else {
                activeBtn.setStyle("-fx-background-color: rgb(0,90,6); -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");
            }
        });

        activeBtn.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY && activeBtn.contains(e.getX(), e.getY())) {
                activeTrack.set(!activeTrack.get());
            }
        });

//        if (activeBtn.getStyleClass().contains("active")) {
//            activeBtn.getStyleClass().remove("active");
//            activeBtn.getStyleClass().add("disabled");
////                    System.out.println(activeBtn.getStyleClass());
//            activeBtn.setStyle("-fx-background-color: rgb(0,90,6); -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");
//        } else {
//            activeBtn.getStyleClass().remove("disabled");
//            activeBtn.getStyleClass().add("active");
//            activeBtn.setStyle("-fx-background-color: #00FD11; -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");
//        }

        return activeBtn;
    }
}

