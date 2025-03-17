package org.JStudio;

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

public class Note {
    public void loadAudioFile(File file, double[] audioData) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioStream.getFormat();
            byte[] bytes = audioStream.readAllBytes();

            audioData = new double[bytes.length / 2];
            for (int i = 0, j = 0; i < bytes.length - 1; i += 2, j++) {
                audioData[j] = ((bytes[i + 1] << 8) | (bytes[i] & 0xFF)) / 32768.0;
            }

            audioStream.close();
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    private void drawWaveform(Canvas canvas, double amplitudeFactor, double[] audioData) {
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

    private void playAudio(double amplitudeFactor, File audioFile) {
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
                        short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF));

                        sample = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, sample * amplitudeFactor));

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
}
