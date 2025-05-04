package org.JStudio.Plugins.Controllers;

import org.JStudio.Plugins.Models.Synth;
import org.JStudio.Plugins.Models.SynthPianoAudioThread;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.event.Event;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import org.JStudio.Plugins.Controllers.PopUpController;
import org.JStudio.Plugins.Synthesizer.Utility;
import org.JStudio.SettingsController;


public class SynthController_Piano {
    Synth synth = new Synth();

    @FXML
    private MenuButton functionChooser1, functionChooser2, functionChooser3;
    @FXML
    private Slider tone1, tone2, tone3, volume1, volume2, volume3;
    @FXML
    private Canvas waveformCanvas;
    @FXML
    private Button addTrackButton;
    @FXML
    private Button playSynthButton;
    @FXML
    private TextField frequencyTextField;

    //setter
    public Synth getSynth() {
        return synth;
    }

    @FXML
    public void initialize() {
        //initialize popup controller to make popups if necessary
        synth.setPopUpController(new PopUpController());

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
                synth.getPopUpController().showWarningPopup("Please enter a frequency");
                synth.getTempStage().requestFocus();
                synth.getTempStage().toFront();
            } else {
                //calculates the frequency depending on the tone slider and plays the synth audio
                synth.setFrequency(Double.parseDouble(frequencyTextField.getText()));

                synth.getOscillatorFrequencies()[0] = Utility.Math.offsetTone(synth.getFrequency(), tone1.getValue());
                synth.getOscillatorFrequencies()[1] = Utility.Math.offsetTone(synth.getFrequency(), tone2.getValue());
                synth.getOscillatorFrequencies()[2] = Utility.Math.offsetTone(synth.getFrequency(), tone3.getValue());

                playAudio(synth.getAuTh());
            }
        });

        //stop audio when user releases the play button
        playSynthButton.setOnMouseReleased(e -> {
            stopAudio(synth.getAuTh());
        });

        //Adds a new track with the user's synth settings
        addTrackButton.setOnAction(event -> {
            //asks the user to input a frequency if they have not
            if (frequencyTextField.getText().isBlank()) {
                synth.getPopUpController().showWarningPopup("Please enter a frequency");
                synth.getTempStage().requestFocus();
                synth.getTempStage().toFront();
            } else {
                //adds a new track
                synth.setFrequency(Double.parseDouble(frequencyTextField.getText()));

                synth.getNotesController().addTrack(synth.getFrequency(), synth.getTxt1(), synth.getTxt2(), synth.getTxt3(), tone1.getValue(), tone2.getValue(), tone3.getValue(), volume1.getValue(), volume2.getValue(), volume3.getValue());
                
                //close the stage/window
                WindowEvent closeEvent = new WindowEvent(synth.getTempStage(), WindowEvent.WINDOW_CLOSE_REQUEST);
                Event.fireEvent(synth.getTempStage(), closeEvent);

            }
        });

        //creates a new thread that draws the waveform while playing the synth audio
        synth.setAuTh(new SynthPianoAudioThread(() -> {
            if (!synth.isShouldGenerate()) {
                return null;
            }
            short[] s = new short[SynthPianoAudioThread.getBufferSize()];

            for (int i = 0; i < SynthPianoAudioThread.getBufferSize(); i++) {
                double mixedSample = 0;

                if (tone1.getValue() != 0) {
                    mixedSample += (generateWaveSample(synth.getTxt1(), synth.getOscillatorFrequencies()[0], synth.getWavePos()) * volume1.getValue()) / synth.getNormalizer();
                }
                if (tone2.getValue() != 0) {
                    mixedSample += (generateWaveSample(synth.getTxt2(), synth.getOscillatorFrequencies()[1], synth.getWavePos()) * volume2.getValue()) / synth.getNormalizer();
                }
                if (tone3.getValue() != 0) {
                    mixedSample += (generateWaveSample(synth.getTxt3(), synth.getOscillatorFrequencies()[2], synth.getWavePos()) * volume3.getValue()) / synth.getNormalizer();
                }

                s[i] = (short) (Short.MAX_VALUE * mixedSample);
                synth.setWavePos(synth.getWavePos() + 1);
            }
            drawWaveform(s);
            return s;
        }));
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
                synth.getRandom().nextDouble() * 2 - 1;
            default ->
                throw new RuntimeException("Oscillator is set to unknown waveform");
        };
    }
    
    //plays the synth audio
    public void playAudio(SynthPianoAudioThread auTh) {
        synth.setGc(waveformCanvas.getGraphicsContext2D());
        synth.setShouldGenerate(true);
        auTh.triggerPlayback();
    }

    //stops the synth audio and clears the canvas that draws the waveform
    public void stopAudio(SynthPianoAudioThread auTh) {
        synth.getGc().clearRect(0, 0, waveformCanvas.getWidth(), waveformCanvas.getHeight());
        auTh.pause();
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
                synth.getWaveformSelection().put(menuButton, item.getText());
                if (menuButton == functionChooser1) {
                    synth.setTxt1(item.getText());
                } else if (menuButton == functionChooser2) {
                    synth.setTxt2(item.getText());
                } else if (menuButton == functionChooser3) {
                    synth.setTxt3(item.getText());
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

    //Draws the synth audio waveform depending on the user's set parameters
    private void drawWaveform(short[] audioBuffer) {
        synth.getGc().clearRect(0, 0, waveformCanvas.getWidth(), waveformCanvas.getHeight()); // Clear previous waveform
        
        //set color of the wave depending on the settings
        switch (SettingsController.getWaveColor()) {
            case "Blue":
                synth.getGc().setStroke(javafx.scene.paint.Color.BLUE);
                break;
            case "Green":
                synth.getGc().setStroke(javafx.scene.paint.Color.GREEN);
                break;
            case "Red":
                synth.getGc().setStroke(javafx.scene.paint.Color.RED);
                break;
        }

        synth.getGc().setLineWidth(1);

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
            synth.getGc().strokeLine(x1, y1, x2, y2);
        }
    }
}
