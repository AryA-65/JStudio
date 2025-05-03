package org.JStudio.Utils;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;
import java.util.List;

public class Spectrograph {
    private static final int FFT_SIZE = 1024;
    private static final int BANDS = 128;
    private static final double MIN_DB = -60; // Minimum decibels for visualization

    private final Canvas canvas;
    private final DoubleFFT_1D fft;
    private final LinearGradient gradient;

    private double[] magnitudes;
    private double[] previousMagnitudes;
    private AnimationTimer animationTimer;
    private List<double[]> fftFrames;
    private int currentFrame;
    private boolean isComputing;

    public Spectrograph(Canvas canvas) {
        this.canvas = canvas;
        this.fft = new DoubleFFT_1D(FFT_SIZE);
        this.gradient = createColorGradient();
        this.magnitudes = new double[BANDS];
        this.previousMagnitudes = new double[BANDS];
        this.fftFrames = new ArrayList<>();

        // Initialize with empty data
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

    public void computeFFTFrames(byte[] audioBytes) {
        if (isComputing) return;
        isComputing = true;

        new Thread(() -> {
            fftFrames.clear();
            short[] audioData = bytesToShorts(audioBytes);
            int totalFrames = audioData.length / (FFT_SIZE / 2); // 50% overlap

            for (int i = 0; i < audioData.length - FFT_SIZE; i += FFT_SIZE / 2) {
                double[] chunk = new double[FFT_SIZE];

                // Convert to double and apply window
                for (int j = 0; j < FFT_SIZE; j++) {
                    chunk[j] = audioData[i + j] / 32768.0 * hammingWindow(j, FFT_SIZE);
                }

                // Compute FFT (in-place)
                fft.realForward(chunk);
                fftFrames.add(chunk.clone());
            }

            isComputing = false;
            currentFrame = 0;
        }).start();
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

    public void update(double[] fftData) {
        for (int band = 0; band < BANDS; band++) {
            double startFreq = Math.pow(2, band * 10.0 / BANDS) - 1;
            double endFreq = Math.pow(2, (band + 1) * 10.0 / BANDS) - 1;

            int startBin = (int)(startFreq * (FFT_SIZE/2) / 10);
            int endBin = (int)(endFreq * (FFT_SIZE/2) / 10);
            endBin = Math.min(endBin, FFT_SIZE/2 - 1);

            double sum = 0;
            int count = 0;

            for (int bin = startBin; bin <= endBin; bin++) {
                double re, im;
                if (bin == 0 || bin == FFT_SIZE/2) {
                    re = fftData[bin];
                    im = 0;
                } else {
                    re = fftData[2*bin];
                    im = fftData[2*bin + 1];
                }

                double magnitude = 20 * Math.log10(Math.sqrt(re*re + im*im) + 1e-12); // dB scale
                sum += magnitude;
                count++;
            }

            double avgMagnitude = (count > 0) ? sum/count : MIN_DB;
            magnitudes[band] = Math.max(MIN_DB, 0.5 * avgMagnitude + 0.5 * previousMagnitudes[band]);
            previousMagnitudes[band] = magnitudes[band];
        }

        drawSpectrogram();
    }

    private void drawSpectrogram() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double bandWidth = width / BANDS;

        // Clear with fading effect
        gc.setFill(new Color(0, 0, 0, 0.1));
        gc.fillRect(0, 0, width, height);

        for (int band = 0; band < BANDS; band++) {
            double magnitude = magnitudes[band];
            double normalized = (magnitude - MIN_DB) / (-MIN_DB); // Normalize to [0,1]
            normalized = Math.min(1.0, Math.max(0.0, normalized));

            double bandHeight = normalized * height;

            gc.setFill(gradient);
            gc.fillRect(band * bandWidth, height - bandHeight,
                    bandWidth, bandHeight);

            gc.setStroke(Color.BLACK);
            gc.strokeRect(band * bandWidth, height - bandHeight,
                    bandWidth, bandHeight);
        }
    }

    public void startAnimation() {
        if (animationTimer != null) animationTimer.stop();

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (currentFrame >= fftFrames.size()) {
                    stop();
                    return;
                }
                update(fftFrames.get(currentFrame));
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

    public void reset() {
        stopAnimation();
        currentFrame = 0;
        clear();
    }

    public void clear() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public boolean isComputing() {
        return isComputing;
    }
}