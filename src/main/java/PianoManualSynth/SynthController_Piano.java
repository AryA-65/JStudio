package PianoManualSynth;

import javafx.fxml.FXML;
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
import org.JStudio.Plugins.Synthesizer.Utility;
import org.JStudio.SettingsController;

public class SynthController_Piano {

    public static final HashMap<Character, Double> KEY_FREQUENCIES = new HashMap<>();
    private final Map<MenuButton, String> waveformSelection = new HashMap<>();
    private final double[] oscillatorFrequencies = new double[3];
    private double frequency = 0;
    private final int NORMALIZER = 6;
    private int wavePos;
    private boolean shouldGenerate;
    private Random random = new Random();
    private String txt1 = "Sine", txt2 = "Sine", txt3 = "Sine";
    private AudioThread auTh;
    private Stage tempStage;
    private SynthPianoController notesController;
    private PopUpController popUpController;
    private GraphicsContext gc;
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

    //setter
    public void setNotesController(SynthPianoController nc) {
        notesController = nc;
    }

    @FXML
    public void initialize() {
        //initialize popup controller to make popups if necessary
        popUpController = new PopUpController();

        //setup UI elements
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

        //Add listener to automatically remove any non-numerical values from the frequency text field
        frequencyTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                frequencyTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        //plays the synth depending on the sliders, the waveform, and the frequency set by the user
        playSynthButton.setOnMousePressed(e -> {
            //asks the user to input a frequency if they have not
            if (frequencyTextField.getText().isBlank()) {
                popUpController.showWarningPopup("Please enter a frequency");
                tempStage.requestFocus();
                tempStage.toFront();
            } else {
                //calculates the frequency depending on the tone slider and plays the synth audio
                frequency = Double.parseDouble(frequencyTextField.getText());

                oscillatorFrequencies[0] = Utility.Math.offsetTone(frequency, tone1.getValue());
                oscillatorFrequencies[1] = Utility.Math.offsetTone(frequency, tone2.getValue());
                oscillatorFrequencies[2] = Utility.Math.offsetTone(frequency, tone3.getValue());

                playAudio(auTh, frequency, txt1, txt2, txt3, tone1.getValue(), tone2.getValue(), tone3.getValue(), volume1.getValue(), volume2.getValue(), volume3.getValue());
            }
        });

        //stop audio when user releases the play button
        playSynthButton.setOnMouseReleased(e -> {
            stopAudio(auTh);
        });

        //Adds a new track with the user's synth settings
        addTrackButton.setOnAction(event -> {
            //asks the user to input a frequency if they have not
            if (frequencyTextField.getText().isBlank()) {
                popUpController.showWarningPopup("Please enter a frequency");
                tempStage.requestFocus();
                tempStage.toFront();
            } else {
                //adds a new track
                frequency = Double.parseDouble(frequencyTextField.getText());

                notesController.addTrack(frequency, txt1, txt2, txt3, tone1.getValue(), tone2.getValue(), tone3.getValue(), volume1.getValue(), volume2.getValue(), volume3.getValue());
                tempStage.close();
            }
        });

        //creates a new thread that draws the waveform while playing the synth audio
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
                wavePos += 1;
            }
            drawWaveform(s);
            return s;
        });
    }

    //generates the audio samples to be played depending on the user's set parameters
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
    
    //plays the synth audio
    public void playAudio(AudioThread auTh, double frequency, String txt1, String txt2, String txt3, double tone1Value, double tone2Value, double tone3Value, double volume1Value, double volume2Value, double volume3Value) {
        gc = waveformCanvas.getGraphicsContext2D();
        shouldGenerate = true;
        auTh.triggerPlayback();
    }

    //stops the synth audio and clears the canvas that draws the waveform
    public void stopAudio(AudioThread auTh) {
        gc.clearRect(0, 0, waveformCanvas.getWidth(), waveformCanvas.getHeight());
        auTh.pause();
    }
    
    //sets onCloseRequest to properly close any threads
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

    //sets slider parameters
    private void sliderSetUp(Slider slider, int border) {
        slider.setMax(border);
        if (border == 1) {
            slider.setValue(border);
            slider.setMax(border);
            slider.setMin(0);
        } else {
            slider.setValue(0);
            slider.setMin(-border);
            slider.setMax(border);
        }
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);
    }

    //sets menu button parameters
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

    //sets the initial selection of the menu buttons
    private void setDefaultMenuSelection(MenuButton menuButton) {
        for (MenuItem item : menuButton.getItems()) {
            if (item.getText().equals("Sine")) {
                menuButton.setText(item.getText());
                break;
            }
        }
    }

    //sets the stage
    public void setStage(Stage stage) {
        this.tempStage = stage;
        closeApplication();
    }

    //Draws the synth audio waveform depending on the user's set parameters
    private void drawWaveform(short[] audioBuffer) {
        gc.clearRect(0, 0, waveformCanvas.getWidth(), waveformCanvas.getHeight()); // Clear previous waveform

        //change color of the wave depending on dark/light mode
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
}
