package org.JStudio;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.concurrent.atomic.AtomicBoolean;

public class Track {
    private String name;
    private static short activeTracks = 0; //move this to song
    private short id;
    private double amplitude, pitch;
    private short numClips; //tempo param for now
//    private List<>

    private byte xfx_channels = 16; //max num of xfx channels for a single audio channel

    Track(String name) {
        this.name = name;
        this.id = ++activeTracks;
    }

    public void addClip() {
        //empty for now
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

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public void removeActiveTrack() {
        activeTracks--;
    }

    public Node addTrack() {
        Canvas canvas = new Canvas();
        canvas.setWidth(1920);
        canvas.setHeight(64);

//        System.out.println(canvas.getWidth());

        GraphicsContext gc = canvas.getGraphicsContext2D();
//        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.GREY);
        gc.fillRoundRect(0, 0, canvas.getWidth(), canvas.getHeight(), 10, 10);

        gc.setStroke(Color.BLACK);
        for (int i = 0; i < 1920; i++) {
            if (i % 32 == 0 && i != 0) {
                gc.strokeLine(i, 0, i, canvas.getHeight());
            }
        }

        canvas.setOnDragOver(e -> {
            if (e.getGestureSource() != canvas && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.COPY);  // Accept the drop
            }
            e.consume();
        });

        canvas.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;

            if (db.hasString()) {

                double dropX = e.getX();

                gc.setFill(Color.BLACK);
                gc.fillRoundRect(dropX, 0, 128, canvas.getHeight(), 10, 10);

                success = true;
            }

            e.setDropCompleted(success);
            e.consume();
        });

        canvas.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {

                if (e.getClickCount() == 2) {
                    gc.setFill(Color.BLACK);
                    gc.fillRoundRect(e.getX(), 0, 128, canvas.getHeight(), 10, 10);
                }

                e.consume();
            }
        });

        return canvas;
    }

    public Node addTrackID() {
        AtomicBoolean clicked = new AtomicBoolean(false);

        Pane container = new Pane();
        container.setPrefHeight(64);
        container.setPrefWidth(126);
        container.setStyle("-fx-background-color: grey; -fx-background-radius: 5px");

        Label idLabel = new Label(String.valueOf(id));
        idLabel.setFont(new Font("Inter Regular", 8));
        idLabel.setLayoutX(4);
        idLabel.setLayoutY(4);

        Pane activeBtn = new Pane();
        activeBtn.setPrefHeight(8);
        activeBtn.setPrefWidth(8);
        activeBtn.setLayoutX(114);
        activeBtn.setLayoutY(52);
        activeBtn.toFront();
        activeBtn.getStyleClass().add("active");
        activeBtn.setStyle("-fx-background-color: #00FD11; -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");

        activeBtn.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                clicked.set(true);
            }
        });

        activeBtn.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY && activeBtn.contains(e.getX(), e.getY()) && clicked.get()) {
                clicked.set(false);
                if (activeBtn.getStyleClass().contains("active")) {
                    activeBtn.getStyleClass().remove("active");
                    activeBtn.getStyleClass().add("disabled");
                    activeBtn.setStyle("-fx-background-color: rgb(0,90,6); -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");
                } else {
                    activeBtn.getStyleClass().remove("disabled");
                    activeBtn.getStyleClass().add("active");
                    activeBtn.setStyle("-fx-background-color: #00FD11; -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");
                }
//                        System.out.println("Registered");
            } else clicked.set(false);

//                    System.out.println("Released");
        });

        container.getChildren().addAll(idLabel, activeBtn);

        return container;
    }

    public Node createChannel(byte i) {
        AtomicBoolean clicked = new AtomicBoolean(false);

//        System.out.println("Added channel " + (i + 1));
        VBox channelBox = new VBox();
        channelBox.setPrefHeight(256);
        channelBox.setPrefWidth(32);
        channelBox.setStyle("-fx-background-color:  #D9D9D9; -fx-background-radius: 5px");
        channelBox.setAlignment(Pos.TOP_CENTER);

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

        Pane activeBtn = new Pane();
        activeBtn.setPrefHeight(8);
        activeBtn.setPrefWidth(8);
        activeBtn.setLayoutX(12);
        activeBtn.setLayoutY(224);
        activeBtn.toFront();
        activeBtn.getStyleClass().add("active");
        activeBtn.setStyle("-fx-background-color: #00FD11; -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");

        activeBtn.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                clicked.set(true);
            }
        });

        activeBtn.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY && activeBtn.contains(e.getX(), e.getY()) && clicked.get()) {
                clicked.set(false);
                if (activeBtn.getStyleClass().contains("active")) {
                    activeBtn.getStyleClass().remove("active");
                    activeBtn.getStyleClass().add("disabled");
                    activeBtn.setStyle("-fx-background-color: rgb(0,90,6); -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");
                } else {
                    activeBtn.getStyleClass().remove("disabled");
                    activeBtn.getStyleClass().add("active");
                    activeBtn.setStyle("-fx-background-color: #00FD11; -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");
                }
//                        System.out.println("Registered");
            } else clicked.set(false);

//                    System.out.println("Released");
        });

        Slider channelAmp = new Slider(0,100,100);
        channelAmp.setOrientation(Orientation.VERTICAL);
        channelAmp.setPrefHeight(96);
        channelAmp.setLayoutY(96);
        channelAmp.setLayoutX(9);

        visContainer.getChildren().add(channelVis);

        channelVisContainer.getChildren().add(visContainer);

        channelContainer.getChildren().addAll(channelVisContainer, channelAmp, activeBtn);

        channelBox.getChildren().addAll(channelID, channelContainer);

//        System.out.println(channelContainer.getHeight());

        return channelBox;
    }
}

