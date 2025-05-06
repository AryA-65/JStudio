package org.JStudio.Utils;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class provides visual representation of audio data.
 * It visualizes audio waveforms and amplitudes using two Canvas objects.
 */
public class AudioVisualizer {
    private static final int BUFFER_SIZE = 512;

    private final float[] leftBuffer = new float[BUFFER_SIZE];
    private final float[] rightBuffer = new float[BUFFER_SIZE];
    private int writeIndex = 0;

    private float rollingLeft = 0f;
    private float rollingRight = 0f;
    private float decay = 0.9f;

    private final Canvas waveCanvas, ampCanvas;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private float[] lastWaveform = new float[BUFFER_SIZE];

    private boolean playing = false;

    /**
     * Constructs an object with the specified waveform and amplitude canvases
     *
     * @param waveformCanvas The canvas on which to draw the audio waveform
     * @param amplitudeCanvas The canvas on which to draw the audio amplitude
     */
    public AudioVisualizer(Canvas waveformCanvas, Canvas amplitudeCanvas) {
        this.waveCanvas = waveformCanvas;
        this.ampCanvas = amplitudeCanvas;

        for (int i = 0; i < BUFFER_SIZE; i++) {
            leftBuffer[i] = 0;
            rightBuffer[i] = 0;
        }
    }

    /**
     * Adds a new stereo audio sample to the visualizer.
     *
     * @param left  The left channel audio sample
     * @param right The right channel audio sample
     */
    public synchronized void addSamples(Float left, Float right) {
        float l = (left != null) ? left : 0f;
        float r = (right != null) ? right : 0f;

        leftBuffer[writeIndex] = l;
        rightBuffer[writeIndex] = r;
        writeIndex = (writeIndex + 1) % BUFFER_SIZE;

        rollingLeft = rollingLeft * decay + Math.abs(l) * (1 - decay);
        rollingRight = rollingRight * decay + Math.abs(r) * (1 - decay);
    }

    /**
     * Starts the audio visualization by continuously updating the waveform and amplitude
     * The visualizer updates at a rate of 24 fps
     */
    public void start() {
        executor.scheduleAtFixedRate(() -> {
            if (!playing) return;

            Platform.runLater(() -> {
                drawWaveform();
                drawAmplitude();
            });
        }, 0, 1000 / 24, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the audio visualizer by shutting down the tasks
     */
    public void stop() {
        executor.shutdownNow();
    }

    /**
     * Draws the audio waveform
     */
    private synchronized void drawWaveform() {
        GraphicsContext gc = waveCanvas.getGraphicsContext2D();
        double w = waveCanvas.getWidth(), h = waveCanvas.getHeight();
        double centerY = h / 2.0;
        double step = w / BUFFER_SIZE;

        gc.clearRect(0, 0, w, h);
        gc.setStroke(Color.web("#3A6DF0"));
        gc.beginPath();

        for (int i = 0; i < BUFFER_SIZE; i++) {
            int index = (writeIndex + i) % BUFFER_SIZE;
            float avgSample = (leftBuffer[index] + rightBuffer[index]) / 2f;

            lastWaveform[i] = avgSample;

            double x = w - (i * step);
            double y = centerY - (avgSample * centerY);

            if (i == 0) gc.moveTo(x, y);
            else gc.lineTo(x, y);
        }

        gc.stroke();
    }

    /**
     * Draws the amplitude bars
     * The amplitude is represented by two bars, one for each stereo channel (left and right).
     */
    private synchronized void drawAmplitude() {
        GraphicsContext gc = ampCanvas.getGraphicsContext2D();
        double w = ampCanvas.getWidth(), h = ampCanvas.getHeight();
        double halfH = h / 2.0;

        gc.clearRect(0, 0, w, h);

        gc.setFill(getAmpColor(rollingLeft));
        gc.fillRect(0, 0, rollingLeft * w, halfH);

        gc.setFill(getAmpColor(rollingRight));
        gc.fillRect(0, halfH, rollingRight * w, halfH);
    }

    /**
     * Returns the color to be used for the amplitude bars based on the amplitude value
     *
     * @param amp The amplitude value
     * @return The color to represent the amplitude
     */
    private Color getAmpColor(float amp) {
        if (amp > 0.85) return Color.RED;
        else if (amp > 0.5) return Color.ORANGE;
        else return Color.LIMEGREEN;
    }
}
