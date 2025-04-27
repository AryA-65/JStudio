package org.JStudio.UI;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import org.JStudio.Core.AudioClip;
import org.JStudio.Core.Clip;
import org.JStudio.Core.SynthClip;

public class ClipUI extends Canvas {
    private final Clip clip;
    private boolean selected = false;

    private int startOffset = 0; // Trim from start
    private int endOffset = 0;   // Trim from end
    private int shiftOffset = 0; // Shift view

    public ClipUI(Clip clip, double width) {
        super(width, 64);
        this.clip = clip;

        widthProperty().addListener((obs, oldVal, newVal) -> redraw());
        heightProperty().addListener((obs, oldVal, newVal) -> redraw());

        redraw();
    }

    public void setStartOffset(int offset) {
        this.startOffset = offset;
        this.clip.setStartOffset(startOffset);
        redraw();
    }

    public void setEndOffset(int offset) {
        this.endOffset = offset;
        this.clip.setEndOffset(endOffset);
        redraw();
    }

    public void setShiftOffset(int offset) {
        this.shiftOffset = offset;
        redraw();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        redraw();
    }

    public boolean isSelected() {
        return selected;
    }

    public void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (clip instanceof AudioClip) {
            drawAudioClip(gc, (AudioClip) clip);
        } else if (clip instanceof SynthClip) {
            drawSynthClip(gc, (SynthClip) clip);
        }

        if (selected) drawOutline(gc);
    }

    private void drawAudioClip(GraphicsContext gc, AudioClip audioClip) {
        float[][] buffer = audioClip.getBuffer();
        if (buffer == null || buffer[0] == null) return;

        int width = (int) getWidth();
        int height = (int) getHeight();
        float[] left = buffer[0];

        int start = Math.max(0, startOffset + shiftOffset);
        int end = Math.min(left.length - endOffset, left.length);
        int visibleSamples = end - start;
        if (visibleSamples <= 0) return;

        double xScale = (double) width / visibleSamples;
        double yMid = height / 2.0;

        gc.setStroke(Color.LIGHTGREEN);
        gc.setLineWidth(1.0);
        gc.beginPath();
        for (int i = 0; i < visibleSamples; i++) {
            int sampleIndex = start + i;
            float sample = left[sampleIndex];
            double x = i * xScale;
            double y = yMid - (sample * yMid);
            if (i == 0) gc.moveTo(x, y);
            else gc.lineTo(x, y);
        }
        gc.stroke();
    }

    private void drawSynthClip(GraphicsContext gc, SynthClip synthClip) {
        int width = (int) getWidth();
        int height = (int) getHeight();

        gc.setFill(Color.CORNFLOWERBLUE);
        for (var note : synthClip.getNotes()) {
            long noteTime = note.getPosition() + shiftOffset;
            if (noteTime < startOffset || noteTime > synthClip.getLength() - endOffset) continue;

            double x = (noteTime - startOffset) * (getWidth() / (double) synthClip.getLength());
            double y = height - (note.getNote() * (height / 128.0));
            double w = 10; // Placeholder width (note duration)
            double h = 10;

            gc.fillRect(x, y, w, h);
        }
    }

    private void drawOutline(GraphicsContext gc) {
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(2.0);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.strokeRect(1, 1, getWidth() - 2, getHeight() - 2);
    }
}
