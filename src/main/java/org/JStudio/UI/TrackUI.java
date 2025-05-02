package org.JStudio.UI;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.JStudio.Core.AudioClip;
import org.JStudio.Core.Track;
import org.JStudio.UIController;
import org.JStudio.Utils.AudioFileExtractor;

import java.io.File;

public class TrackUI extends StackPane {
//    private ArrayList<ClipUI> clips = new ArrayList<ClipUI>();

//    private PluginRenderer pluginUI = new PluginRenderer();

    private final Track track;
    private final Canvas trackCanvas;
    private final GraphicsContext gc;
    private final Pane clipLayer = new Pane();

    public TrackUI(double width, Track track) {
        this.track = track;
        setPrefSize(width, 64);

        trackCanvas = new Canvas(width, 64);
        gc = trackCanvas.getGraphicsContext2D();
        drawTrackCanvas(gc, width);

        clipLayer.setPickOnBounds(false);

        getChildren().addAll(trackCanvas, clipLayer);

        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                for (Node n : clipLayer.getChildren()) {
                    if (e.getTarget().equals(n)) {
                        track.removeClip(((ClipUI) n).getNodeClip());
                        clipLayer.getChildren().remove(n);
                        break;
                    }
                }
            }
        });

        setupDragAndDrop();
    }

    private void drawTrackCanvas(GraphicsContext gc, double width) {
        gc.clearRect(0,0,width,64);

        gc.setFill(Color.GREY);
        gc.fillRoundRect(0, 0, width, 64, 10, 10);

        gc.setStroke(Color.BLACK);
        for (int i = 0; i < width; i++) {
            if (i % 32 == 0 && i != 0) {
                gc.setLineWidth(2);
                gc.strokeLine(i, 0, i, 64);
            } else if (i % 8 == 0 && !(i % 32 == 0)) {
                gc.setLineWidth(1);
                gc.strokeLine(i, 0, i, 64);
            }
        }
    }

    private void setupDragAndDrop() {
        setOnDragOver(e -> {
            if (e.getGestureSource() != this && e.getDragboard().hasFiles()) {
                e.acceptTransferModes(TransferMode.COPY);
            }
            e.consume();
        });

        setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;

            if (db.hasFiles()) {
                System.out.println("Entered");
                double dropX = e.getX();
                if (UIController.snap.get()) dropX = Math.round(dropX / 8) * 8;
                System.out.println(dropX);

                double pixelsPerBeat = 32.0;
                double bpm = 120;

                double beats = dropX / pixelsPerBeat;
                double seconds = beats * (60.0 / bpm);

                try {
                    float[][] audio_data = AudioFileExtractor.readFile(db.getFiles().get(db.getFiles().size() - 1));

                    AudioClip audioClip = null;

                    if (AudioFileExtractor.isMp3()) {
                        audioClip = new AudioClip(seconds, audio_data, AudioFileExtractor.getMP3SampleRate());
                        System.out.println(AudioFileExtractor.getMP3SampleRate());
                    }
                    else audioClip = new AudioClip(seconds, audio_data, AudioFileExtractor.getSampleRate());

                    ClipUI clip = new ClipUI(audioClip);

                    clip.setLayoutX(dropX);
                    clip.setLayoutY(0);
                    clipLayer.getChildren().add(clip);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                success = true;
            }

            e.setDropCompleted(success);
            e.consume();
        });
    }
}
