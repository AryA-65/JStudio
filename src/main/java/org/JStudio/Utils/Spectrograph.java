package org.JStudio.Utils;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.Random;

public class Spectrograph {
    private static final int FFT_SIZE = 1024;
    private static final int BANDS = 64;
    private static final int TOTAL_FRAMES = 200;
    private final Canvas canvas;
    private final Random random = new Random();
    private final LinearGradient gradient = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.BLACK),
            new Stop(0.2, Color.BLUE),
            new Stop(0.4, Color.CYAN),
            new Stop(0.6, Color.LIME),
            new Stop(0.8, Color.YELLOW),
            new Stop(1, Color.RED)
    );
    private double[] magnitudes;
    private double[] previousMagnitudes;
    private AnimationTimer animationTimer;
    private double[][] simulatedFFTFrames;
    private int currentFrame = 0;

    public Spectrograph(Canvas canvas) {
        this.canvas = canvas;
        magnitudes = new double[BANDS];
        previousMagnitudes = new double[BANDS];
    }

    public void update(double[] fftData) {
        for (int band = 0; band < BANDS; band++) {
            double startFreq = Math.pow(2, band * 10.0 / BANDS) - 1;
            double endFreq = Math.pow(2, (band + 1) * 10.0 / BANDS) - 1;

            int startBin = (int) (startFreq * (FFT_SIZE / 2) / 10);
            int endBin = (int) (endFreq * (FFT_SIZE / 2) / 10);

            if (startBin == endBin) endBin++;

            double sum = 0;
            for (int bin = startBin; bin < endBin && bin < FFT_SIZE / 2; bin++) {
                double re = fftData[bin * 2];
                double im = fftData[bin * 2 + 1];
                sum += Math.sqrt(re * re + im * im);
            }

            double avgMagnitude = sum / (endBin - startBin);

            magnitudes[band] = 0.5 * avgMagnitude + 0.5 * previousMagnitudes[band];
            previousMagnitudes[band] = magnitudes[band];
        }

        drawSpectrogram();
    }

    private void drawSpectrogram() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double bandWidth = width / BANDS;

        // Clear with semi-transparent black for trailing effect
        gc.setFill(new Color(0, 0, 0, 0.1));
        gc.fillRect(0, 0, width, height);

        for (int band = 0; band < BANDS; band++) {
            double magnitude = magnitudes[band];
            double normalized = Math.log1p(magnitude) / 10.0;
            normalized = Math.min(1.0, Math.max(0.0, normalized));

            double bandHeight = normalized * height;

            // Fill the entire width by using bandWidth
            gc.setFill(gradient);
            gc.fillRect(band * bandWidth, height - bandHeight,
                    bandWidth, bandHeight);

            // Add some styling
            gc.setStroke(Color.BLACK);
            gc.strokeRect(band * bandWidth, height - bandHeight,
                    bandWidth, bandHeight);
        }
    }

    public void clear() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void computeFFTFrames() {
        simulatedFFTFrames = new double[TOTAL_FRAMES][];
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            simulatedFFTFrames[i] = generateFakeFFTData(1024, i);
        }
        currentFrame = 0;
    }

    public void startSpectrographAnimation() {
        if (simulatedFFTFrames == null) {
            System.out.println("Compute FFT data first!");
            return;
        }

        if (animationTimer != null) {
            animationTimer.stop();
        }

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (currentFrame >= TOTAL_FRAMES) {
                    stop();
                    return;
                }

                update(simulatedFFTFrames[currentFrame]);
                currentFrame++;
            }
        };

        animationTimer.start();
    }

    public void stopSpectrographAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    public void resetSpectrograph() {
        stopSpectrographAnimation();
        currentFrame = 0;
        clear();
    }

    private double[] generateFakeFFTData(int size, int offset) {
        double[] data = new double[size];
        for (int i = 0; i < size / 2; i++) {
            double magnitude = Math.abs(Math.sin((offset + i) * 0.1)) * 100 + random.nextDouble() * 50;
            data[i * 2] = magnitude * Math.cos(i);
            data[i * 2 + 1] = magnitude * Math.sin(i);
        }
        return data;
    }

}
