package PianoManualSynth;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.scene.control.TextField;
import org.JStudio.Plugins.Controllers.PopUpController;
import static org.JStudio.Plugins.Views.ReverbStage.scene;
import org.JStudio.SettingsController;

public class Controller {

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
    private Slider tone1, tone2, tone3, volume1, volume2, volume3;//, playSpeed;
    @FXML
    private Canvas waveformCanvas;
    @FXML
    private Button addTrackButton;
    @FXML
    private Button playSynthButton;
    @FXML
    private TextField frequencyTextField;
    private boolean shouldGenerate;
    private int wavePos;
//    private double speedFactor = 1;
    private NotesController notesController;
    private double frequency = 0;
    private PopUpController popUpController;

    public void setNotesController(NotesController nc) {
        notesController = nc;
    }

    @FXML
    public void initialize() {
        popUpController = new PopUpController();

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

//        sliderSetUp(playSpeed, 10);
//        updateSlider(tone1, 0, true);
//        updateSlider(tone2, 1, true);
//        updateSlider(tone3, 2, true);
//
//        updateSlider(volume1, 0, false);
//        updateSlider(volume2, 1, false);
//        updateSlider(volume3, 2, false);
        frequencyTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                frequencyTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        playSynthButton.setOnMousePressed(e -> {
            if (frequencyTextField.getText().isBlank()) {
                popUpController.showWarningPopup("Please enter a frequency");
                tempStage.requestFocus();
                tempStage.toFront();
            } else {
                frequency = Double.parseDouble(frequencyTextField.getText());

                oscillatorFrequencies[0] = Utility.Math.offsetTone(frequency, tone1.getValue());
                oscillatorFrequencies[1] = Utility.Math.offsetTone(frequency, tone2.getValue());
                oscillatorFrequencies[2] = Utility.Math.offsetTone(frequency, tone3.getValue());

                playAudio(auTh, frequency, txt1, txt2, txt3, tone1.getValue(), tone2.getValue(), tone3.getValue(), volume1.getValue(), volume2.getValue(), volume3.getValue());
            }
        });

        playSynthButton.setOnMouseReleased(e -> {
            stopAudio(auTh);
        });

        addTrackButton.setOnAction(event -> {
            if (frequencyTextField.getText().isBlank()) {
                popUpController.showWarningPopup("Please enter a frequency");
                tempStage.requestFocus();
                tempStage.toFront();
            } else {
                frequency = Double.parseDouble(frequencyTextField.getText());

                notesController.addTrack(frequency, txt1, txt2, txt3, tone1.getValue(), tone2.getValue(), tone3.getValue(), volume1.getValue(), volume2.getValue(), volume3.getValue());
                tempStage.close();
            }
        });

        auTh = new AudioThread(() -> {
            if (!shouldGenerate) {
                return null;
            }
            short[] s = new short[AudioThread.BUFFER_SIZE];

            for (int i = 0; i < AudioThread.BUFFER_SIZE; i++) {
                double mixedSample = 0;

                if (tone1.getValue() != 0) {
                    mixedSample += (generateWaveSample(txt1, oscillatorFrequencies[0], wavePos) * volume1.getValue()) / NORMALIZER;
                }
                if (tone2.getValue() != 0) {
                    mixedSample += (generateWaveSample(txt2, oscillatorFrequencies[1], wavePos) * volume2.getValue()) / NORMALIZER;
                }
                if (tone3.getValue() != 0) {
                    mixedSample += (generateWaveSample(txt3, oscillatorFrequencies[2], wavePos) * volume3.getValue()) / NORMALIZER;
                }

                s[i] = (short) (Short.MAX_VALUE * mixedSample);
                wavePos += 1;//(int) (speedFactor);
            }
            drawWaveform(s);
            return s;
        });

//        for (int i = Utility.AudioInfo.STARTING_KEY, key = 0; i < (Utility.AudioInfo.KEYS).length * Utility.AudioInfo.KEY_FREQUENCY_INCREMENT + Utility.AudioInfo.STARTING_KEY; i += Utility.AudioInfo.KEY_FREQUENCY_INCREMENT, ++key) {
//            KEY_FREQUENCIES.put(Utility.AudioInfo.KEYS[key], Utility.Math.getKeyFrequency(i));
//        }
        //playSpeed.valueProperty().addListener((obs, oldValue, newValue) -> setSpeedFactor(newValue.doubleValue()));
    }

    public double generateWaveSample(String waveformType, double frequency, int wavePosition) {
        double tDivP = (wavePosition / (double) Utility.AudioInfo.SAMPLE_RATE) / (1d / frequency);

        final double a = 2d * (tDivP - Math.floor(0.5 + tDivP));
        return switch (waveformType) {
            case "Sine" ->
                Math.sin(Utility.Math.frequencyToAngularFrequency(frequency) * wavePosition / Utility.AudioInfo.SAMPLE_RATE);
            case "Square" ->
                Math.signum(Math.sin(Utility.Math.frequencyToAngularFrequency(frequency) * wavePosition / Utility.AudioInfo.SAMPLE_RATE));
            case "Saw" ->
                a;
            case "Triangle" ->
                2d * Math.abs(a) - 1;
            case "Noise" ->
                random.nextDouble() * 2 - 1;
            default ->
                throw new RuntimeException("Oscillator is set to unknown waveform");
        };
    }

//    private void setupKeyboardListeners() {
//        if (tempScene == null) {
//            System.err.println("tempScene is not set, cannot set up keyboard listeners");
//            return;
//        }
//
//        tempScene.setOnKeyPressed(event -> {
//            if (!auTh.isRunning()) {
//                char key = event.getText().isEmpty() ? '\0' : event.getText().charAt(0);
//                if (KEY_FREQUENCIES.containsKey(key)) {
//
//                    double frequency = KEY_FREQUENCIES.get(key);
//
//                    oscillatorFrequencies[0] = Utility.Math.offsetTone(frequency, tone1.getValue());
//                    oscillatorFrequencies[1] = Utility.Math.offsetTone(frequency, tone2.getValue());
//                    oscillatorFrequencies[2] = Utility.Math.offsetTone(frequency, tone3.getValue());
//
//                    if (tone1.getValue() == 0 && tone2.getValue() == 0 && tone3.getValue() == 0) {
//                        return;
//                    }
//                    if (volume1.getValue() == 0 && volume2.getValue() == 0 && volume3.getValue() == 0) {
//                        return;
//                    }
//                    shouldGenerate = true;
//                    auTh.triggerPlayback();
//                }
//            }
//        });
//
//        tempScene.setOnKeyReleased(event -> {
//            if (tempScene.getOnKeyPressed() != null) {
//                shouldGenerate = false;
//            }
//        });
//    }
    public void playAudio(AudioThread auTh, double frequency, String txt1, String txt2, String txt3, double tone1Value, double tone2Value, double tone3Value, double volume1Value, double volume2Value, double volume3Value) {
        shouldGenerate = true;
        auTh.triggerPlayback();
    }

    public void stopAudio(AudioThread auTh) {
        auTh.pause();
    }

//    private void updateSlider(Slider slider, int index, boolean isToneSlider) {
//        slider.valueProperty().addListener((obs, oldValue, newValue) -> {
//
//            if (isToneSlider && tempScene != null) {
//                tempScene.setOnKeyPressed(event -> {
//                    char key = event.getText().isEmpty() ? '\0' : event.getText().charAt(0);
//                    if (KEY_FREQUENCIES.containsKey(key)) {
//                        double frequency = KEY_FREQUENCIES.get(key);
//                        oscillatorFrequencies[index] = Utility.Math.offsetTone(frequency, newValue.doubleValue());
//                    }
//
//                    if (!auTh.isRunning() && (volume1.getValue() > 0 || volume2.getValue() > 0 || volume3.getValue() > 0)) {
//                        shouldGenerate = true;
//                        auTh.triggerPlayback();
//                    }
//                });
//            }
//        });
//    }
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

    private void sliderSetUp(Slider slider, int border) {
        slider.setMax(border);
        if (border == 1) {
            slider.setValue(border);
            slider.setMax(border);
            slider.setMin(0);
        } else if (border == 10) {
            slider.setValue(1);
            slider.setMin(1);
            slider.setMax(border);
        } else {
            slider.setValue(0);
            slider.setMin(-border);
            slider.setMax(border);
        }
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);
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
        //setupKeyboardListeners();
    }

    public void setStage(Stage stage) {
        this.tempStage = stage;
        closeApplication();
    }

    private void drawWaveform(short[] audioBuffer) {
        GraphicsContext gc = waveformCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, waveformCanvas.getWidth(), waveformCanvas.getHeight()); // Clear previous waveform

        if (SettingsController.getStyle()) {
            gc.setStroke(javafx.scene.paint.Color.WHITE);
        } else {
            gc.setStroke(javafx.scene.paint.Color.BLACK);
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

//    public void setSpeedFactor(double speedFactor) {
//        this.speedFactor = speedFactor;
//    }
}
