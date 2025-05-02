package org.JStudio.UI;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import org.JStudio.Core.AudioClip;
import org.JStudio.Core.Clip;
import org.JStudio.Core.SynthClip;
import org.JStudio.UIController;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.JStudio.Core.Song.bpm;

public class ClipUI extends Canvas {
    private final Clip clip;
    private final GraphicsContext gc;
    private boolean selected = false;

    private int startOffset = 0, endOffset = -1, shiftOffset = 0; // Shift view

    public ClipUI(Clip clip) {
        super(clip.getLength() * ((double) bpm.get() / 60) * 32, 64);
        System.out.println(clip.getLength() * ((double) bpm.get() / 60) * 32);
        this.clip = clip;
        gc = getGraphicsContext2D();

        widthProperty().addListener((obs, oldVal, newVal) -> redraw());
        heightProperty().addListener((obs, oldVal, newVal) -> redraw());

        setupDrag();

        redraw();

        //small test
        setOnMousePressed(e -> {
            selected = !selected;
            redraw();
        });
    }

//    public void setEndOffset(double endOffset) {
//
//    }

    private void setupDrag() {
        AtomicBoolean isDragged = new AtomicBoolean(false);

        setOnMouseDragged(e -> {
            double newX = getLayoutX() + e.getX();
            newX = Math.max(0, Math.min(newX, ((Pane) getParent()).getWidth() - getWidth()));

            if (UIController.snap.get()) newX = Math.round(newX / 8) * 8;

            boolean overlap = false;
            for (Node node : ((Pane) getParent()).getChildren()) {
                if (node instanceof ClipUI other && node != this) {
                    double otherX = other.getLayoutX();
                    double otherWidth = other.getWidth();

                    if (newX < otherX + otherWidth && newX + getWidth() > otherX) {
                        overlap = true;
                        break;
                    }
                }
            }

            if (!overlap) setLayoutX(newX);

            if (!isDragged.get()) {
                isDragged.set(true);
            }
        });

        setOnMouseReleased(e -> {
            if (isDragged.get()) {
                isDragged.set(false);
                clip.setPosition((getLayoutX() / 32) * (60 / bpm.get()));
                System.out.println(clip.getPosition());
            }
        });
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        redraw();
    }

    public boolean isSelected() {
        return selected;
    }

    public void redraw() {
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (clip instanceof AudioClip) {
            drawAudioClip(gc, (AudioClip) clip);
        } else if (clip instanceof SynthClip) {
            drawSynthClip(gc, (SynthClip) clip);
        }

        if (selected) drawOutline();
    }

    private void drawAudioClip(GraphicsContext gc, AudioClip audioClip) {
        float[][] buffer = audioClip.getBuffer();
        if (buffer == null || buffer[0] == null) return;

        int width = (int) getWidth();
        int height = (int) getHeight();
        float[] left = buffer[0];

        int start = Math.max(0, startOffset + shiftOffset);
        int end = (endOffset == -1) ? left.length : Math.min(left.length - endOffset, left.length);
        int visibleSamples = end - start;
        if (visibleSamples <= 0) return;

        double yMid = height / 2.0;

        gc.setFill(Color.web("#000000", 0.5));
        gc.fillRoundRect(0, 0, width, height, 10, 10);

        int samplesPerPixel = Math.max(1, visibleSamples / width);
        gc.setStroke(Color.LIGHTGREY);
        gc.setLineWidth(1.0);
        gc.beginPath();
        for (int x = 0; x < width; x++) {
            int startSample = start + x * samplesPerPixel;
            int endSample = Math.min(startSample + samplesPerPixel, end);
            float min = Float.MAX_VALUE, max = Float.MIN_VALUE;

            for (int i = startSample; i < endSample; i++) {
                float sample = left[i];
                if (sample < min) min = sample;
                if (sample > max) max = sample;
            }

            double y1 = yMid - (max * yMid);
            double y2 = yMid - (min * yMid);
            gc.moveTo(x, y1);
            gc.lineTo(x, y2);
        }
        gc.stroke();
    }

    private void drawSynthClip(GraphicsContext gc, SynthClip synthClip) {
        int height = (int) getHeight();

        gc.setFill(Color.CORNFLOWERBLUE);
        for (var note : synthClip.getNotes()) {
            double noteTime = note.getPosition() + shiftOffset;
            if (noteTime < startOffset || noteTime > synthClip.getLength() - endOffset) continue;

            double x = (noteTime - startOffset) * (getWidth() / synthClip.getLength());
            double y = height - (note.getNote() * (height / 128.0));
            double w = 10;
            double h = 10;

            gc.fillRect(x, y, w, h);
        }
    }

    private void drawOutline() {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.0);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.strokeRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
    }

    public Clip getNodeClip() {
        return clip;
    }
}
