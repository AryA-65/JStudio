package org.JStudio.UI;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class TimelineUI {
    private final Canvas timeline_canvas;
    private final GraphicsContext gc;

    public TimelineUI(Canvas timeline_canvas) {
        this.timeline_canvas = timeline_canvas;
        gc = this.timeline_canvas.getGraphicsContext2D();

        drawTimeline();
    }

    public void drawTimeline() {
        Rectangle clip = new Rectangle(timeline_canvas.getWidth(), timeline_canvas.getHeight());
        clip.setArcHeight(10);
        clip.setArcWidth(10);

        timeline_canvas.setClip(clip);

        gc.clearRect(0, 0, timeline_canvas.getWidth(), timeline_canvas.getHeight());

        gc.setFill(Color.GREY);
        gc.fillRoundRect(0,0, timeline_canvas.getWidth(), timeline_canvas.getHeight(), 10, 10);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Inter Regular", 12));

        for (int i = 0; i < timeline_canvas.getWidth(); i++) {
            if (i % 32 == 0) {
                short mult = (short) ((i / 32) + 1);
                gc.fillText(String.valueOf(mult), i, timeline_canvas.getHeight() - 12);
            }
            if (i % 8 == 0) {
                gc.fillRect(i - 1, timeline_canvas.getHeight() - 10, (i % 32 != 0) ? 1 : 2, 8);
            }
        }
    }

    public void drawPlaybackMarker(double positionX) {
        drawTimeline();
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeLine(positionX, 0, positionX, timeline_canvas.getHeight());
        System.out.println(positionX);
    }
}
