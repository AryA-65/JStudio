package org.JStudio.Plugins.Controllers;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import org.JStudio.Plugins.Views.SpectrographStage;
import org.JStudio.SettingsController;
import org.JStudio.UI.Knob;
import org.JStudio.Utils.AlertBox;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.JStudio.UI.Knob.Type.REG;

/**
 * Class that applies the basic audio filters
 */
public class AudioAmplitudeFXMLController {
    private String fileName;

    private StringProperty name = new SimpleStringProperty("BaseFilter");

    private Stage stage;
    @FXML
    Button exportButton, playButton;
    @FXML
    Canvas waveformCanvas;
    @FXML
    VBox vbox;

    private final Knob knob = new Knob(50, false, 0, REG);

    private File audioFile;
    private byte[] audioBytesOriginal;   // Raw input bytes
    private byte[] audioBytesProcessed;  // Processed bytes
    private double amp;
    private SourceDataLine line;
    private AudioFormat format;
    
    public static boolean isFileSelected = false;

    /**
     * Method that determines the logic of the basic audio filter plugin
     */
    public void initialize() {
        knob.setValues(-5, 5);
        vbox.getChildren().add(knob);
        knob.valueProperty().addListener((ObservableValue<? extends Number> obs, Number oldVal, Number newVal) -> {
            amp = knob.getValue();
            applyAmplitudeChange();
        });
        knob.setValue(1);

        handleImportAudio();

        playButton.setOnAction(event -> {
            if (audioFile == null || audioBytesProcessed == null || audioBytesProcessed.length == 0) {
                AlertBox.display("Playback Error", "No audio file loaded.");
                return;
            }

            playButton.setDisable(true);
            playAudio();

            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> playButton.setDisable(false));
            delay.play();
        });

        exportButton.setOnAction(event -> {
            stopAudio();

            if (SettingsController.isTesting()) {
                stage.close();
                SpectrographStage spectrographStage = new SpectrographStage();
                try {
                    if (SpectrographStage.controller != null) {
                        SpectrographStage.controller.setArrays(audioBytesOriginal, audioBytesProcessed);
                    }
                } catch (Exception e) {
                    AlertBox.display("Export Error", "Failed to load Unit Testing interface.");
                }
                spectrographStage.show();
            }
        });
    }

    /**
     * Method that applies the amplitude change
     */
    private void applyAmplitudeChange() {
        if (audioBytesOriginal == null) return;

        audioBytesProcessed = new byte[audioBytesOriginal.length];

        for (int i = 0; i < audioBytesOriginal.length; i += 2) {
            int low = audioBytesOriginal[i] & 0xFF;
            int high = audioBytesOriginal[i + 1];
            short sample = (short) ((high << 8) | low);

            sample = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, sample * amp));

            audioBytesProcessed[i] = (byte) (sample & 0xFF);
            audioBytesProcessed[i + 1] = (byte) ((sample >> 8) & 0xFF);
        }

        drawWaveform(waveformCanvas, amp);
    }

    /**
     * Method that imports the audio file and analyzes it
     */
    private void handleImportAudio() {
        isFileSelected = false;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Audio File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("WAV Files", "*.wav")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        fileName = selectedFile.getName();

        if (selectedFile != null) {
            isFileSelected = true;
            audioFile = selectedFile;
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
                format = audioInputStream.getFormat();
                loadAudioData(audioFile);
                applyAmplitudeChange();
            } catch (Exception e) {
                AlertBox.display("Import Error", "Failed to load audio: " + e.getMessage());
            }
        } else {
            AlertBox.display("Input Error", "No audio file selected.");
        }
    }

    /**
     * Method that creates an array of bytes that represent the audio file
     * @param file the imported audio file
     */
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

            audioBytesOriginal = out.toByteArray();
            audioBytesProcessed = audioBytesOriginal.clone();
        } catch (Exception e) {
            AlertBox.display("Audio Load Error", "Failed to load audio file: " + e.getMessage());
        }
    }

    /**
     * Method that draws the amplitude of the audio file
     * @param canvas the canva that the wave of the file will be drawn to
     * @param amplitudeFactor the amplitude factor that multiplies with the initial graph
     */
    private void drawWaveform(Canvas canvas, double amplitudeFactor) {
        if (audioBytesOriginal == null || audioBytesOriginal.length == 0) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLUE);

        double midY = canvas.getHeight() / 2;
        double scaleX = canvas.getWidth() / (audioBytesOriginal.length / 2.0);
        double scaleY = midY * amplitudeFactor;

        gc.beginPath();
        for (int i = 0; i < audioBytesOriginal.length; i += 2) {
            int low = audioBytesOriginal[i] & 0xFF;
            int high = audioBytesOriginal[i + 1];
            short sample = (short) ((high << 8) | low);
            double x = (i / 2) * scaleX;
            double y = midY - (sample / 32768.0) * scaleY;

            if (i == 0) gc.moveTo(x, y);
            else gc.lineTo(x, y);
        }
        gc.stroke();
    }

    /**
     * Method that plays the byte array
     */
    private void playAudio() { // this
        if (audioBytesProcessed == null || audioBytesProcessed.length == 0) {
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

                line.write(audioBytesProcessed, 0, audioBytesProcessed.length);
                line.drain();
                line.close();
                stream.close();
            } catch (Exception e) {
                AlertBox.display("Playback Error", "An error occurred while playing the audio.");
            }
        }).start();
    }

    /**
     * Method that sets the stage
     * @param stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setOnCloseRequest(event -> stopAudio());
    }


    /**
     * Method that stops audio from playing
     */
    public void stopAudio() { // this
        if (line != null && line.isOpen()) {
            line.flush();
            line.stop();
            line.close();
        }
    }

    /**
     * Method to export an audio file given a name
     * @param pluginName the name of the exported audio file
     */
    public void export(String pluginName){
        try {
            //create AudioInputStream from the byte array
            ByteArrayInputStream bais = new ByteArrayInputStream(audioBytesProcessed);
            AudioInputStream audioInputStream = new AudioInputStream(bais, format, audioBytesProcessed.length / format.getFrameSize());

            //make sure the file path exists
            File dir = new File(System.getProperty("user.home") + File.separator + "Music" + File.separator + "JStudio" + File.separator + "audio_Files" + File.separator + "Plugins");
            if (!dir.exists()) {
                Files.createDirectories(dir.toPath());
            }

            File wavFile;

            //save to wav file
            if (new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName +".wav").exists()) {
                int i = 1;
                while(new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName + i +".wav").exists()){
                    i++;
                }
                wavFile = new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName + i +".wav");
            } else {
                wavFile = new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName +".wav");
            }

            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
