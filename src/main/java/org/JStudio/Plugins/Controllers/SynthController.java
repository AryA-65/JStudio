package org.JStudio.Plugins.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javafx.scene.paint.Color;

import org.JStudio.Plugins.SynthUtil.AudioThread;
import org.JStudio.Plugins.SynthUtil.Utility;
import org.JStudio.SettingsController;

/**
 * Controller class for the synthesizer
 */
public class SynthController {
    private final StringProperty name = new SimpleStringProperty("Synthesizer");
    public static final HashMap<Character, Double> KEY_FREQUENCIES = new HashMap<>();
    private final Map<MenuButton, String> waveformSelection = new HashMap<>();
    private final double[] oscillatorFrequencies = new double[3];
    private final int NORMALIZER = 6;
    Random random = new Random();
    String txt1 = "Sine", txt2 = "Sine", txt3 = "Sine";
    private AudioThread auTh;
    private Scene tempScene;
    private Stage tempStage;
    @FXML
    private MenuButton functionChooser1, functionChooser2, functionChooser3;
    @FXML
    private Slider tone1, tone2, tone3, volume1, volume2, volume3, playSpeed, glideSp;
    @FXML
    private Canvas waveformCanvas;
    @FXML
    private Button recordButton, smothButton;
    private boolean shouldGenerate;
    private int wavePos;
    private double speedFactor = 1;
    private boolean isRecording = false;
    private ByteArrayOutputStream recordingBuffer;
    private GraphicsContext gc;
    private final Set<Character> pressedKeys = new HashSet<>();

    private double[] currentFrequencies = new double[3];
    private double glideSpeed; // Adjust for faster/slower glides

    private short[] delayBuffer; // Buffer for storing delayed audio samples
    private int delayBufferIndex = 0; // Current index in the delay buffer
    private int delayOffsetSamples = 1000; // The number of samples to delay by (adjust as needed)
    private double delayFeedback = 0.5;

    private boolean glideEnabled = false;

    @FXML
    public void initialize() {
        setupMenu(functionChooser1);
        setupMenu(functionChooser2);
        setupMenu(functionChooser3);

        setDefaultMenuSelection(functionChooser1);
        setDefaultMenuSelection(functionChooser2);
        setDefaultMenuSelection(functionChooser3);

        sliderSetUp(tone1, 2);
        sliderSetUp(tone2, 2);
        sliderSetUp(tone3, 2);
        sliderSetUp(volume1, 1);
        sliderSetUp(volume2, 1);
        sliderSetUp(volume3, 1);

        sliderSetUp(playSpeed, 10);
        sliderSetUp(glideSp, 0.001);

        updateSlider(tone1, 0, true);
        updateSlider(tone2, 1, true);
        updateSlider(tone3, 2, true);

        updateSlider(volume1, 0, false);
        updateSlider(volume2, 1, false);
        updateSlider(volume3, 2, false);

        smothButton.setOnAction(event -> {
            glideEnabled = !glideEnabled;
            smothButton.setText(glideEnabled ? "Glide ON" : "Glide OFF");
        });

        glideSp.valueProperty().addListener((obs, oldValue, newValue) -> {
            glideSpeed = newValue.doubleValue() / 100;
        });

        recordButton.setOnAction(event -> {
            if (!isRecording) {
                isRecording = true;
                shouldGenerate = true;
                recordingBuffer = new ByteArrayOutputStream();
                recordButton.setText("Stop Recording");
            } else {
                isRecording = false;
                recordButton.setText("Record");

                String userHome = System.getProperty("user.home");
                String outputDir = userHome + File.separator + "Music" + File.separator + "JStudio" + File.separator + "audio_Files" + File.separator + "Synthesizer";

                // Save the recording to a generic path
                saveRecordingAsWav(recordingBuffer, outputDir);

                recordingBuffer = null;
            }
        });

        final AudioThread audioThread = new AudioThread(() -> {
            int bufferSize = AudioThread.SAMPLE_RATE;
            delayBuffer = new short[bufferSize];
            if (!shouldGenerate) {
                return null;
            }
            short[] s = new short[AudioThread.BUFFER_SIZE];

            for (int i = 0; i < AudioThread.BUFFER_SIZE; i++) {
                double mixedSample = 0;

                for (int j = 0; j < 3; j++) {
                    double targetFreq = oscillatorFrequencies[j];

                    if (glideEnabled) {
                        currentFrequencies[j] += (targetFreq - currentFrequencies[j]) * glideSpeed;
                    } else {
                        currentFrequencies[j] = targetFreq;
                    }
                }

                if (oscillatorFrequencies[0] != 0 && tone1.getValue() != 0) {
                    mixedSample += (generateWaveSample(txt1, currentFrequencies[0], wavePos) * volume1.getValue()) / NORMALIZER;
                }
                if (oscillatorFrequencies[1] != 0 && tone2.getValue() != 0) {
                    mixedSample += (generateWaveSample(txt2, currentFrequencies[1], wavePos) * volume2.getValue()) / NORMALIZER;
                }
                if (oscillatorFrequencies[2] != 0 && tone3.getValue() != 0) {
                    mixedSample += (generateWaveSample(txt3, currentFrequencies[2], wavePos) * volume3.getValue()) / NORMALIZER;
                }

                short rawSample = (short) (Short.MAX_VALUE * mixedSample);

                // Fetch the delayed sample from the delay buffer
                int delayedIndex = (delayBufferIndex - delayOffsetSamples + delayBuffer.length) % delayBuffer.length;
                short delayedSample = delayBuffer[delayedIndex];

                // Combine the raw sample with the delayed sample, applying feedback
                short finalSample = (short) (rawSample + delayFeedback * delayedSample);

                // Store the final sample in the delay buffer
                delayBuffer[delayBufferIndex] = finalSample;
                delayBufferIndex = (delayBufferIndex + 1) % delayBuffer.length;

                s[i] = rawSample;
                if (isRecording) {
                    recordingBuffer.write(finalSample & 0xFF);          // low byte
                    recordingBuffer.write((finalSample >> 8) & 0xFF);   // high byte
                }
                wavePos += (int) (speedFactor);
            }
            drawWaveform(s);
            return s;
        });


        this.auTh = audioThread;

        for (int i = Utility.AudioInfo.STARTING_KEY, key = 0; i < (Utility.AudioInfo.KEYS).length * Utility.AudioInfo.KEY_FREQUENCY_INCREMENT + Utility.AudioInfo.STARTING_KEY; i += Utility.AudioInfo.KEY_FREQUENCY_INCREMENT, ++key) {
            KEY_FREQUENCIES.put(Utility.AudioInfo.KEYS[key], Utility.Math.getKeyFrequency(i));
        }
        playSpeed.valueProperty().addListener((obs, oldValue, newValue) -> setSpeedFactor(newValue.doubleValue()));
        this.auTh = audioThread;

    }

    /**
     * Saves the played keys as a wav file
     */
    public static void saveRecordingAsWav(ByteArrayOutputStream recordingBuffer, String filePath) {
        try {
            byte[] audioBytes = recordingBuffer.toByteArray();

            // Ensure the byte array length is even (since we're using 16-bit samples)
            if (audioBytes.length % 2 != 0) {
                byte[] correctedBytes = new byte[audioBytes.length - 1];
                System.arraycopy(audioBytes, 0, correctedBytes, 0, correctedBytes.length);
                audioBytes = correctedBytes;
            }

            AudioFormat format = new AudioFormat(
                    AudioThread.SAMPLE_RATE,  // sample rate
                    16,                      // sample size in bits
                    1,                       // channels
                    true,                    // signed
                    false                    // bigEndian
            );

            ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
            AudioInputStream audioInputStream = new AudioInputStream(
                    bais,
                    format,
                    audioBytes.length / 2  // frames = total bytes / 2 (16-bit samples)
            );

            // Ensure directory exists
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String uniqueFileName = filePath + "/synth_output_" + System.currentTimeMillis() + ".wav";
            File wavFile = new File(uniqueFileName);

            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
            System.out.println("WAV file saved successfully: " + wavFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double generateWaveSample(String waveformType, double frequency, int wavePosition) {
        double tDivP = (wavePosition / (double) Utility.AudioInfo.SAMPLE_RATE) / (1d / frequency);

        final double a = 2d * (tDivP - Math.floor(0.5 + tDivP));
        return switch (waveformType) {
            case "Sine" ->
                    Math.sin(Utility.Math.frequencyToAngularFrequency(frequency) * wavePosition / Utility.AudioInfo.SAMPLE_RATE);
            case "Square" ->
                    Math.signum(Math.sin(Utility.Math.frequencyToAngularFrequency(frequency) * wavePosition / Utility.AudioInfo.SAMPLE_RATE));
            case "Saw" -> a;
            case "Triangle" -> 2d * Math.abs(a) - 1;
            case "Noise" -> random.nextDouble() * 2 - 1;
            default -> throw new RuntimeException("Oscillator is set to unknown waveform");
        };
    }

    private void setupKeyboardListeners() {
        if (tempScene == null) {
            System.err.println("tempScene is not set, cannot set up keyboard listeners");
            return;
        }

        tempScene.setOnKeyPressed(event -> {
            char key = event.getText().isEmpty() ? '\0' : event.getText().charAt(0);
            if (KEY_FREQUENCIES.containsKey(key)) {
                pressedKeys.add(key);
                updateOscillatorFrequencies();
                shouldGenerate = true;
                if (!auTh.isRunning()) {
                    auTh.triggerPlayback();
                }
            }
        });

        tempScene.setOnKeyReleased(event -> {
            char key = event.getText().isEmpty() ? '\0' : event.getText().charAt(0);
            pressedKeys.remove(key);
            updateOscillatorFrequencies();
            if (pressedKeys.isEmpty()) {
                shouldGenerate = false;
            }
        });
    }

    /**
     * Slider updating method
     */
    private void updateSlider(Slider slider, int index, boolean isToneSlider) {
        slider.valueProperty().addListener((obs, oldValue, newValue) -> {

            if (isToneSlider && tempScene != null) {
                tempScene.setOnKeyPressed(event -> {
                    char key = event.getText().isEmpty() ? '\0' : event.getText().charAt(0);
                    if (KEY_FREQUENCIES.containsKey(key)) {
                        double frequency = KEY_FREQUENCIES.get(key);
                        oscillatorFrequencies[index] = Utility.Math.offsetTone(frequency, newValue.doubleValue());
                    }

                    if (!auTh.isRunning() && (
                            volume1.getValue() > 0 || volume2.getValue() > 0 || volume3.getValue() > 0
                    )) {
                        shouldGenerate = true;
                        auTh.triggerPlayback();
                    }
                });
            }
        });
    }

    private void closeApplication() {
        if (tempStage == null) {
            System.err.println("tempStage is not set, cannot close application");
            return;
        }

        tempStage.setOnCloseRequest(event -> {
            event.consume();
            auTh.close();
            tempStage.close();
        });
    }

    private void sliderSetUp(Slider slider, double border) {
        slider.setMax(border);
        if (border == 1) {
            slider.setValue(border);
            slider.setMax(border);
            slider.setMin(0);
        } else if (border == 10) {
            slider.setValue(1);
            slider.setMin(1);
            slider.setMax(border);
        } else if (border == 0.001) {
            slider.setMin(0.001);
            slider.setMax(1);
        } else {
            slider.setValue(0);
            slider.setMin(-border);
            slider.setMax(border);
        }
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
    }

    private void setupMenu(MenuButton menuButton) {
        for (MenuItem item : menuButton.getItems()) {
            item.setOnAction(event -> {
                menuButton.setText(item.getText());
                waveformSelection.put(menuButton, item.getText());
                if (menuButton == functionChooser1) {
                    txt1 = item.getText();
                } else if (menuButton == functionChooser2) {
                    txt2 = item.getText();
                } else if (menuButton == functionChooser3) {
                    txt3 = item.getText();
                }
            });
        }
    }

    private void setDefaultMenuSelection(MenuButton menuButton) {
        for (MenuItem item : menuButton.getItems()) {
            if (item.getText().equals("Sine")) {
                menuButton.setText(item.getText());
                break;
            }
        }
    }

    public void setScene(Scene scene) {
        this.tempScene = scene;
        setupKeyboardListeners();
    }

    public void setStage(Stage stage) {
        this.tempStage = stage;
        closeApplication();
    }

    private void drawWaveform(short[] audioBuffer) {
        gc = waveformCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, waveformCanvas.getWidth(), waveformCanvas.getHeight()); // Clear previous waveform

        //set color of the wave depending on the settings
        switch (SettingsController.getWaveColor()) {
            case "Blue":
                gc.setStroke(Color.web("#118AB2"));
                break;
            case "Green":
                gc.setStroke(javafx.scene.paint.Color.GREEN);
                break;
            case "Red":
                gc.setStroke(javafx.scene.paint.Color.RED);
                break;
        }
        
        gc.setLineWidth(1);

        double centerY = waveformCanvas.getHeight() / 2;
        double scale = waveformCanvas.getHeight() / 2.0;

        // Loop through the audio buffer and plot the waveform
        for (int i = 0; i < audioBuffer.length - 1; i++) {
            // calculates the first x point
            double x1 = i * (waveformCanvas.getWidth() / (double) audioBuffer.length);
            // calculates the first y point
            double y1 = centerY - (audioBuffer[i] / (double) Short.MAX_VALUE) * scale;
            // calculates the second x point
            double x2 = (i + 1) * (waveformCanvas.getWidth() / (double) audioBuffer.length);
            // calculates the second y point
            double y2 = centerY - (audioBuffer[i + 1] / (double) Short.MAX_VALUE) * scale;

            // connects the 2 points
            gc.strokeLine(x1, y1, x2, y2);
        }
    }

    public void setSpeedFactor(double speedFactor) {
        this.speedFactor = speedFactor;
    }

    private void updateOscillatorFrequencies() {
        int index = 0;
        for (char key : pressedKeys) {
            double frequency = KEY_FREQUENCIES.get(key);
            if (index < 3) {
                oscillatorFrequencies[index] = Utility.Math.offsetTone(frequency,
                        (index == 0 ? tone1.getValue() :
                                (index == 1 ? tone2.getValue() : tone3.getValue())));
                index++;
            }
        }
    }
}
