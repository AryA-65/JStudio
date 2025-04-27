package org.JStudio.UI;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.JStudio.Core.Track;

import java.util.ArrayList;
import java.util.List;

public class TrackUI extends StackPane {
    private List<ClipUI> clips = new ArrayList<ClipUI>();

    private final Track track;
    private final Canvas trackCanvas;
    private final Pane clipLayer;

    public TrackUI(double width, Track track) {
        this.track = track;
        setPrefSize(width, 64);

        trackCanvas = new Canvas(width, 64);
        drawTrackCanvas(trackCanvas.getGraphicsContext2D(), width);

        clipLayer = new Pane();
        clipLayer.setPickOnBounds(false);

        getChildren().addAll(trackCanvas, clipLayer);

        setupDragAndDrop();
    }

    private void drawTrackCanvas(GraphicsContext gc, double width) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, 64);

        gc.setFill(Color.GREY);
        gc.fillRoundRect(0, 0, width, 64, 10, 10);

        gc.setStroke(Color.LIGHTGRAY);
        for (int i = 0; i < width; i++) {
            if (i % 32 == 0 && i != 0) {
                gc.strokeLine(i, 0, i, 64);
            }
        }
    }

    private void setupDragAndDrop() {

    }
}
