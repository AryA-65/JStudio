package org.JStudio.Utils;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.*;
import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;
import java.util.List;

public class Spectrograph {
    private static final int FFT_SIZE = 1024;
    private static final int BANDS = 128;
    private static final double MIN_DB = -60;
    private final DoubleFFT_1D fft;
    private final LinearGradient gradient;

    private double[] magnitudes;
    private double[] previousMagnitudes;
    private AnimationTimer animationTimer;
    private List<double[]> fftFrames;
    private int currentFrame;
    private boolean isComputing = false;

    private Thread thread;

    public Spectrograph() {
        this.fft = new DoubleFFT_1D(FFT_SIZE);
        this.gradient = createColorGradient();
        this.magnitudes = new double[BANDS];
        this.previousMagnitudes = new double[BANDS];
        this.fftFrames = new ArrayList<>();

        for (int i = 0; i < BANDS; i++) {
            magnitudes[i] = MIN_DB;
            previousMagnitudes[i] = MIN_DB;
        }
    }

    private LinearGradient createColorGradient() {
        return new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.BLACK),
                new Stop(0.2, Color.BLUE),
                new Stop(0.4, Color.CYAN),
                new Stop(0.6, Color.LIME),
                new Stop(0.8, Color.YELLOW),
                new Stop(1, Color.RED)
        );
    }

    public void computeFFTFrames(byte[] audioBytes, ProgressBar progressBar) {
        if (isComputing) return;
        isComputing = true;
        progressBar.setVisible(true);

        thread = new Thread(() -> {
            fftFrames.clear();
            short[] audioData = bytesToShorts(audioBytes);

            for (int i = 0; i < audioData.length - FFT_SIZE; i += FFT_SIZE / 2) {
                double[] chunk = new double[FFT_SIZE];

                for (int j = 0; j < FFT_SIZE; j++) {
                    chunk[j] = audioData[i + j] / 32768.0 * hammingWindow(j, FFT_SIZE);
                }

                fft.realForward(chunk);
                fftFrames.add(chunk.clone());
            }

            isComputing = false;
            currentFrame = 0;

            javafx.application.Platform.runLater(() -> {
                progressBar.setVisible(false);
            });
        });

        thread.setDaemon(true);
        thread.start();
    }

    private short[] bytesToShorts(byte[] bytes) {
        short[] shorts = new short[bytes.length / 2];
        for (int i = 0; i < shorts.length; i++) {
            shorts[i] = (short)((bytes[2*i] & 0xFF) | (bytes[2*i+1] << 8));
        }
        return shorts;
    }

    private double hammingWindow(int n, int N) {
        return 0.54 - 0.46 * Math.cos(2 * Math.PI * n / (N - 1));
    }

    public void update(double[] fftData, int frameIndex, Canvas canvas) {
        double smoothingFactor = 0.3;

        for (int band = 0; band < BANDS; band++) {
            double startFreq = Math.pow(2, band * 10.0 / BANDS) - 1;
            double endFreq = Math.pow(2, (band + 1) * 10.0 / BANDS) - 1;

            int startBin = (int)(startFreq * (FFT_SIZE / 2.0) / 10);
            int endBin = (int)(endFreq * (FFT_SIZE / 2.0) / 10);
            endBin = Math.min(endBin, FFT_SIZE / 2 - 1);

            double sum = 0;
            int count = 0;

            for (int bin = startBin; bin <= endBin; bin++) {
                double re, im;
                if (bin == 0 || bin == FFT_SIZE / 2) {
                    re = fftData[bin];
                    im = 0;
                } else {
                    re = fftData[2 * bin];
                    im = fftData[2 * bin + 1];
                }

                double magnitude = 20 * Math.log10(Math.sqrt(re * re + im * im) + 1e-12);
                sum += magnitude;
                count++;
            }

            double avgMagnitude = (count > 0) ? sum / count : MIN_DB;
            avgMagnitude = Math.max(MIN_DB, avgMagnitude);

            // Exponential smoothing
            magnitudes[band] = smoothingFactor * avgMagnitude + (1 - smoothingFactor) * previousMagnitudes[band];
            previousMagnitudes[band] = magnitudes[band];
        }

        drawSpectrogram(frameIndex, canvas);
    }

    private void drawSpectrogram(int frameIndex, Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        // Draw dB lines
        drawDBLines(gc, height, canvas);

        double[] xPoints = new double[BANDS + 2];
        double[] yPoints = new double[BANDS + 2];

        for (int band = 0; band < BANDS; band++) {
            double x = (double) band / (BANDS - 1) * width;
            double magnitude = magnitudes[band];
            double normalized = (magnitude - MIN_DB) / (-MIN_DB);
            normalized = Math.min(1.0, Math.max(0.0, normalized));
            double y = height - (normalized * height);

            xPoints[band + 1] = x;
            yPoints[band + 1] = y;
        }

        // Close polygon: bottom-left and bottom-right corners
        xPoints[0] = 0;
        yPoints[0] = height;
        xPoints[BANDS + 1] = width;
        yPoints[BANDS + 1] = height;

        gc.setFill(gradient);
        gc.fillPolygon(xPoints, yPoints, BANDS + 2);

        gc.setStroke(Color.LIMEGREEN);
        gc.setLineWidth(1.5);
        for (int i = 1; i < BANDS; i++) {
            gc.strokeLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }

        gc.setFill(Color.WHITE);
        gc.fillText(String.format("Frame %d", frameIndex), canvas.getWidth() -100, 20);
    }

    private void drawDBLines(GraphicsContext gc, double height, Canvas canvas) {
        double[] dBLevels = {-20, -40, -60}; // Add or modify levels as needed
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);

        for (double dB : dBLevels) {
            double y = height - (dB - MIN_DB) / (-MIN_DB) * height;
            gc.strokeLine(0, y, canvas.getWidth(), y); // Draw horizontal line across the canvas
        }
    }


    public void startAnimation(Canvas canvas) {
        if (animationTimer != null) animationTimer.stop();

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (currentFrame >= fftFrames.size()) {
                    stop();
                    return;
                }
                update(fftFrames.get(currentFrame), currentFrame, canvas);
                currentFrame++;
            }
        };
        animationTimer.start();
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    public void reset(Canvas canvas) {
        stopAnimation();
        currentFrame = 0;
        clear(canvas);
    }

    public void clear(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public boolean computationOver() {
        return isComputing;
    }
}
