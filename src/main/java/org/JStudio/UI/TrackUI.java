package org.JStudio.UI;

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

/**
 * Class that provides the user interface to display audio clips, handle drag-and-drop audio file import, and respond to user interactions such as right-click deletion.
 */
public class TrackUI extends StackPane {
    private final Track track;
    private final Canvas trackCanvas;
    private final GraphicsContext gc;
    private final Pane clipLayer = new Pane();

    /**
     * Constructs TrackUI object
     *
     * @param width the preferred width of the track UI
     * @param track the backend track associated with this UI
     */
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
                track.removeClip(e);
                clipLayer.getChildren().remove(e.getTarget());
                System.gc();
//                for (Node n : clipLayer.getChildren()) {
//                    if (e.getTarget().equals(n)) {
//                        track.removeClip(((ClipUI) n).getNodeClip());
//                        clipLayer.getChildren().remove(n);
//                        System.gc();
//                        break;
//                    }
//                }
            }
        });

        setupDragAndDrop();
    }

    /**
     * Draws the background canvas of the track, including grey background and vertical beat lines.
     *
     * @param gc the GraphicsContext to draw with
     * @param width the width of the canvas
     */
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

    /**
     * Sets up drag-and-drop functionality for importing audio files
     */
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
                double dropX = e.getX();
                if (UIController.snap.get()) dropX = Math.round(dropX / 8) * 8;

                if (dropX < 0 || dropX > getWidth() || isOverlappingClip(dropX)) {
                    e.setDropCompleted(false);
                    e.consume();
                    return;
                }

                double pixelsPerBeat = 32.0;
                double bpm = 120;

                double beats = dropX / pixelsPerBeat;
                double seconds = beats * (60.0 / bpm);

                try {
                    float[][] audio_data = AudioFileExtractor.readFile(db.getFiles().get(db.getFiles().size() - 1));

                    AudioClip audioClip = null;

                    if (AudioFileExtractor.isMp3()) {
                        audioClip = new AudioClip(seconds, audio_data, AudioFileExtractor.getMP3SampleRate());
                    } else audioClip = new AudioClip(seconds, audio_data, AudioFileExtractor.getSampleRate());

                    track.addClip(audioClip);

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

    /**
     * Checks if the given drop X-position overlaps with any existing audio clips on the track
     *
     * @param dropX the x-position of the drop
     * @return true if the drop location overlaps an existing clip
     */
    private boolean isOverlappingClip(double dropX) {
        for (var node : clipLayer.getChildren()) {
            if (node instanceof ClipUI clip) {
                double x = clip.getLayoutX();
                double w = clip.getWidth();
                if (dropX >= x && dropX <= x + w) {
                    return true;
                }
            }
        }
        return false;
    }
}
