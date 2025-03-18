package com.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {
    private double[] audioData;
    private File audioFile = new File("src/main/resources/15640-laser_gun_shot_3 copy.wav");

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(600, 200);
        Slider amplitudeSlider = new Slider(0.1, 2.0, 1.0); // Adjust amplitude
        Button playButton = new Button("Play");

        amplitudeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            drawWaveform(canvas, newVal.doubleValue());
        });

        playButton.setOnAction(e -> playAudio(amplitudeSlider.getValue()));

        loadAudioFile(audioFile); // Load and extract waveform
        drawWaveform(canvas, 1.0);

        VBox root = new VBox(canvas, amplitudeSlider, playButton);
        primaryStage.setScene(new Scene(root, 600, 300));
        primaryStage.setTitle("Waveform Editor");
        primaryStage.show();
    }

    private void loadAudioFile(File file) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioStream.getFormat();
            byte[] bytes = audioStream.readAllBytes();

            audioData = new double[bytes.length / 2];
            for (int i = 0, j = 0; i < bytes.length - 1; i += 2, j++) {
                audioData[j] = ((bytes[i + 1] << 8) | (bytes[i] & 0xFF)) / 32768.0; // Normalize
            }

            audioStream.close();
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    private void drawWaveform(Canvas canvas, double amplitudeFactor) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLUE);

        double midY = canvas.getHeight() / 2;
        double scaleX = canvas.getWidth() / (double) audioData.length;
        double scaleY = midY * amplitudeFactor;

        gc.beginPath();
        for (int i = 0; i < audioData.length; i++) {
            double x = i * scaleX;
            double y = midY - (audioData[i] * scaleY);
            if (i == 0) gc.moveTo(x, y);
            else gc.lineTo(x, y);
        }
        gc.stroke();
    }

    private void playAudio(double amplitudeFactor) {
        new Thread(() -> {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioStream.getFormat();
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = audioStream.read(buffer)) != -1) {
                    for (int i = 0; i < bytesRead - 1; i += 2) {
                        // Convert bytes to sample
                        short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF));

                        // Apply amplitude factor
                        sample = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, sample * amplitudeFactor));

                        // Convert back to bytes
                        buffer[i] = (byte) (sample & 0xFF);
                        buffer[i + 1] = (byte) ((sample >> 8) & 0xFF);
                    }

                    line.write(buffer, 0, bytesRead);
                }

                line.drain();
                line.close();
                audioStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
