package org.JStudio.Core;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
    private ArrayList<Clip> clips = new ArrayList<>();
    private ArrayList<Plugin> plugins = new ArrayList<>();

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
    }

    public void addClip(Clip clip) {
        clips.add(clip);
    }

    public void removeClip(int position) {
        for (Clip clip : clips) {
            if (position >= clip.getPosition() && position < clip.getPosition() + clip.getLength()) {
                clips.remove(clip);
                break;
            }
        }
    }

    private void removeClip(Event e) {
        for (Clip clip : clips) {
            if (e.getTarget() == clip) {
                clips.remove(clip);
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

    public StackPane getContainer(int width, int num) {
        StackPane container = new StackPane();
        container.setPrefSize(width, 64);
        container.setAlignment(Pos.CENTER_LEFT);

        Pane clipContainer = new Pane();
        clipContainer.setPrefSize(width, 64);
        clipContainer.setId("clipContainer" + num);

        container.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {

            } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                for (Node clip : clipContainer.getChildren()) {
                    if (event.getTarget() == clip) {
                        clipContainer.getChildren().remove(clip);
                        break;
                    }
                }
            }
        });

        container.setOnDragOver(e -> {
            if (e.getGestureSource() != container && e.getDragboard().hasFiles()) {
                e.acceptTransferModes(TransferMode.COPY);  // Accept the drop
            }
            e.consume();
        });

        container.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            boolean snap = true;

            if (db.hasFiles()) {
                System.out.println("True");

                double dropX = e.getX();

                if (snap) {
                    dropX = Math.round(dropX / 32) * 32;
                }

                AudioClip testFuckingshit = new AudioClip((int) dropX, db.getFiles().get(db.getFiles().size() - 1));

//                System.out.println(string);
                double size = testFuckingshit.lengthToTime() * ((double) 120 / 60) * 32;
                System.out.println(size);

//                System.out.println(string);

                Canvas tempo = new Canvas(size, 64);
                drawWaveform(testFuckingshit, tempo, 120);


                tempo.setLayoutX(dropX);

                final double RESIZE_MARGIN  = 4;
                final double MIN_WIDTH = 4;

//                tempo.setOnMouseEntered(ev -> {
//                    tempo.setCursor(Cursor.MOVE);
//                });

                tempo.setOnMousePressed(ev -> {
                    double mouseX = ev.getX();
                    if (mouseX < RESIZE_MARGIN) {
                        tempo.setCursor(Cursor.W_RESIZE);
                    } else if (mouseX > tempo.getWidth() - RESIZE_MARGIN) {
                        tempo.setCursor(Cursor.E_RESIZE);
                    } else {
                        tempo.setCursor(Cursor.MOVE);
                    }
                });

                tempo.setOnMouseDragged(ev -> {
                    Cursor cursor = tempo.getCursor();

                    if (cursor == Cursor.MOVE) {
                        double newX = tempo.getLayoutX() + ev.getX() - tempo.getWidth() / 2;
                        newX = Math.max(0, Math.min(newX, container.getWidth() - tempo.getWidth()));

                        if (snap) {
                            newX = Math.round(newX / 32.0) * 32.0;
                        }

                        if (!isOverlapping(newX, tempo.getWidth(), tempo, clipContainer)) {
                            tempo.setLayoutX(newX);
                        }

                    } else if (cursor == Cursor.E_RESIZE) {
                        double newWidth = ev.getX();
                        newWidth = Math.max(MIN_WIDTH, Math.min(newWidth, container.getWidth() - tempo.getLayoutX()));

                        if (snap) {
                            double rightEdge = tempo.getLayoutX() + newWidth;
                            rightEdge = Math.round(rightEdge / 32.0) * 32.0;
                            newWidth = Math.max(MIN_WIDTH, rightEdge - tempo.getLayoutX());
                        }

                        if (!isOverlapping(tempo.getLayoutX(), newWidth, tempo, clipContainer)) {
                            tempo.setWidth(newWidth);
//                            redrawTempo(tempo, newWidth);
                        }

                    } else if (cursor == Cursor.W_RESIZE) {
                        double mouseSceneX = ev.getSceneX();
                        double deltaX = mouseSceneX - tempo.localToScene(0, 0).getX();
                        double newX = tempo.getLayoutX() + deltaX;
                        double newWidth = tempo.getWidth() - deltaX;

                        if (snap) {
                            newX = Math.round(newX / 32.0) * 32.0;
                            newWidth = Math.max(MIN_WIDTH, (tempo.getLayoutX() + tempo.getWidth()) - newX);
                        }

                        if (newX >= 0 && newWidth >= MIN_WIDTH && newX + newWidth <= container.getWidth()) {
                            if (!isOverlapping(newX, newWidth, tempo, clipContainer)) {
                                tempo.setLayoutX(newX);
                                tempo.setWidth(newWidth);
//                                redrawTempo(tempo, newWidth);
                            }
                        }
                    }
                });

                tempo.setOnMouseReleased(ev -> {
                    double length = 0;
                    if (ev.getX() < RESIZE_MARGIN) {
                        length = tempo.getWidth() / (((double) 120 / 60) * 32);
                        System.out.println(length + " " + Math.round(length * 44100));
                        tempo.setCursor(Cursor.W_RESIZE);
                    } else if (ev.getX() > tempo.getWidth() - RESIZE_MARGIN) {
                        length = tempo.getWidth() / (((double) 120 / 60) * 32);
                        System.out.println(length + " " + Math.round(length * 44100));
                        tempo.setCursor(Cursor.E_RESIZE);
                    } else {
                        tempo.setCursor(Cursor.MOVE);
                    }
                });

//                redrawTempo(tempo, size);

                clipContainer.getChildren().add(tempo);

                success = true;
            }

            e.setDropCompleted(success);
            e.consume();
        });

        container.getChildren().addAll(addTrack(width), clipContainer);

        return container;
    }

    private boolean isOverlapping(double x, double width, Canvas self, Pane clipContainer) {
        for (Node node : clipContainer.getChildren()) {
            if (node instanceof Canvas && node != self) {
                double otherX = node.getLayoutX();
                double otherW = ((Canvas) node).getWidth();
                if (x < otherX + otherW && x + width > otherX) {
                    return true;
                }
            }
        }
        return false;
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

    public void drawWaveform(AudioClip clip, Canvas c, double bpm) {
        GraphicsContext gc = c.getGraphicsContext2D();

        double pixelsPerBeat = 32.0;
        double beatsPerSecond = bpm / 60.0;
        double pixelsPerSecond = pixelsPerBeat * beatsPerSecond;
        double sampleRate = clip.getSampleRate();
        double samplesPerPixel = sampleRate / pixelsPerSecond;

        float[][] buffer = clip.getBuffer();
        double clipStartX = clip.getPosition();
        double clipDuration = clip.lengthToTime();
        double clipWidth = clipDuration * pixelsPerSecond;

        double waveformHeight = 64;
        double centerY = 64 / 2.0;
        boolean isStereo = buffer[1] != null;

        gc.setFill(Color.WHITE);
        gc.fillRect(clipStartX, 0, clipWidth, 64);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);

        for (int x = 0; x < clipWidth; x++) {
            int sampleStart = (int) (x * samplesPerPixel);
            int sampleEnd = (int) ((x + 1) * samplesPerPixel);
            if (sampleEnd >= buffer[0].length) {
                sampleEnd = buffer[0].length - 1;
            }

            float maxLeft = 0;
            for (int i = sampleStart; i < sampleEnd; i++) {
                maxLeft = Math.max(maxLeft, Math.abs(buffer[0][i]));
            }

            float maxRight = maxLeft;
            if (isStereo) {
                maxRight = 0;
                for (int i = sampleStart; i < sampleEnd; i++) {
                    maxRight = Math.max(maxRight, Math.abs(buffer[1][i]));
                }
            }

            float maxAmplitude = Math.max(maxLeft, maxRight);

            double scaledHeight = maxAmplitude * waveformHeight / 2.0;

            double pixelX = clipStartX + x;
            gc.strokeLine(pixelX, centerY - scaledHeight, pixelX, centerY + scaledHeight);
        }
    }
}

