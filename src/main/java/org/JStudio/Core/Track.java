package org.JStudio.Core;

import javafx.beans.property.*;
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
    private static short trackCounter = 0;

    private StringProperty name = new SimpleStringProperty(), id = new SimpleStringProperty();
    private final DoubleProperty amplitude = new SimpleDoubleProperty(100), pitch = new SimpleDoubleProperty(0), pan = new SimpleDoubleProperty(0);
    public final BooleanProperty muted = new SimpleBooleanProperty(false);
    private ArrayList<Clip> clips = new ArrayList<>();
    private ArrayList<Plugin> plugins = new ArrayList<>();

    public Track(String name) {
        this.name.set(name);
        this.id.set(String.valueOf(trackCounter++));
    }

    public Track() {
        this("Empty Track");
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

    public StringProperty getName() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty getId() {
        return id;
    }

    public void setId(byte id) {
        this.id.set(String.valueOf(id));
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

    public DoubleProperty getPan() {
        return pan;
    }

    public void setPan(double pan) {
        this.pan.set(pan);
    }

    public BooleanProperty getMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted.set(muted);
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

//                AudioClip testFuckingshit = new AudioClip((int) dropX, db.getFiles().get(db.getFiles().size() - 1));

//                System.out.println(string);
//                double size = testFuckingshit.lengthToTime() * ((double) 120 / 60) * 32;
//                System.out.println(size);

//                System.out.println(string);

                Canvas tempo = new Canvas(123, 64);
//                drawWaveform(testFuckingshit, tempo, 120);


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

//        container.getChildren().addAll(addTrack(width), clipContainer);

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
}

