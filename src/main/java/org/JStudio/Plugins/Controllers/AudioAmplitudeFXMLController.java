package org.JStudio.Plugins.Controllers;

import javafx.animation.PauseTransition;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.JStudio.UI.Knob;
import org.JStudio.Utils.AlertBox;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.JStudio.UI.Knob.Type.REG;

public class AudioAmplitudeFXMLController {
    private Stage stage;
    @FXML
    Button exportButton, playButton;
    @FXML
    Canvas waveformCanvas;

    private final Knob knob = new Knob(50, false, 0, REG);

    private double[] audioData;

    private File audioFile;
    private double[] processedAudioData;

    public byte[] buffer;
    public byte[] outputArray;

    private double amp;

    private SourceDataLine line;
    @FXML
    VBox vbox;

    public void initialize() {
        knob.setValues(-5, 5);
        knob.setValue(1);
        vbox.getChildren().add(knob);

        knob.valueProperty().addListener((ObservableValue<? extends Number> obs, Number oldVal, Number newVal) -> {
            amp = knob.getValue();
            applyAmplitudeChange();
        });

        handleImportAudio();

        playButton.setOnAction(event -> {
            if (audioFile == null || processedAudioData.length == 0) {
                AlertBox.display("Playback Error", "No audio file loaded.");
                return;
            }
            if (audioData != null) {
                playButton.setDisable(true);
                playAudio();

                PauseTransition delay = new javafx.animation.PauseTransition(Duration.seconds(3));
                delay.setOnFinished(e -> playButton.setDisable(false));
                delay.play();
            }
        });


        exportButton.setOnAction(event -> {
            stopAudio();
            getProcessedAudio();
        });

    }

    private void applyAmplitudeChange() {
        if (audioData == null) return;

        processedAudioData = new double[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            processedAudioData[i] = audioData[i] * amp;
        }

        drawWaveform(waveformCanvas, amp);
    }

    private void handleImportAudio() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Audio File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("WAV Files", "*.wav")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            audioFile = selectedFile;
            try {
                loadAudioData(audioFile);
                drawWaveform(waveformCanvas, knob.getValue());
            } catch (Exception e) {
                AlertBox.display("Import Error", "Failed to load audio: " + e.getMessage());
            }
        } else {
            AlertBox.display("Input Error", "No audio file selected.");
        }
    }

    private void loadAudioData(File file) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = stream.getFormat();

            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                format = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        format.getSampleRate(),
                        16,
                        format.getChannels(),
                        format.getChannels() * 2,
                        format.getSampleRate(),
                        false
                );
                stream = AudioSystem.getAudioInputStream(format, stream);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            byte[] audioBytes = out.toByteArray();
            if (audioBytes.length == 0) {
                AlertBox.display("Load Error", "The selected file contains no audio data.");
                return;
            }

            int sampleCount = audioBytes.length / 2;
            audioData = new double[sampleCount];

            for (int i = 0; i < sampleCount; i++) {
                int low = audioBytes[i * 2] & 0xFF;
                int high = audioBytes[i * 2 + 1];
                short sample = (short) ((high << 8) | low);
                audioData[i] = sample / 32768.0; // Normalize
            }

        } catch (Exception e) {
            AlertBox.display("Audio Load Error", "Failed to load audio file: " + e.getMessage());
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

    private void playAudio() {
        if (processedAudioData == null || processedAudioData.length == 0) {
            AlertBox.display("Playback Error", "No processed audio data available.");
            return;
        }

        stopAudio();

        new Thread(() -> {
            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = stream.getFormat();

                line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();

                buffer = creatingByteArray(processedAudioData);

                line.write(buffer, 0, buffer.length);
                line.drain();
                line.close();
                stream.close();
            } catch (Exception e) {
                AlertBox.display("Playback Error", "An error occurred while playing the audio.");
            }
        }).start();
    }
    public void setStage(Stage stage) {
        this.stage = stage;

        this.stage.setOnCloseRequest(event -> {
            stopAudio();
        });
    }

    public byte[] getProcessedAudio() {
        if (processedAudioData == null || processedAudioData.length == 0) {
            AlertBox.display("Export Error", "No processed audio to export.");
            return null;
        }

        outputArray = creatingByteArray(processedAudioData);
        return outputArray;
    }

    public byte[] creatingByteArray(double[] array) {
        byte[] placeHolder = new byte[array.length * 2];

        for (int i = 0; i < array.length; i++) {
            short sample = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, array[i] * 32768));
            placeHolder[i * 2] = (byte) (sample & 0xFF);
            placeHolder[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
        }
        return placeHolder;
    }

    public void stopAudio() {
        if (line != null && line.isOpen()) {
            line.flush(); // discard non called data
            line.stop(); // stop
            line.close(); // close resources
        }
    }
}