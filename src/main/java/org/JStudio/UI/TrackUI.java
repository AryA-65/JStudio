package org.JStudio.UI;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.JStudio.Core.Track;

import java.util.ArrayList;

public class TrackUI extends StackPane {
//    private ArrayList<ClipUI> clips = new ArrayList<ClipUI>();

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

    }
}
