package org.JStudio.TESTING;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.JStudio.Plugins.Models.Plugin;
import org.JStudio.Utils.Spectrograph;

/**
 * Class that is responsible for controlling the user interface and audio processing for a plugin unit test.
 */
public class UnitTestingController extends Plugin {

    @FXML
    private Label fileNameLabel;
    @FXML
    private ProgressBar leftProgressBar, rightProgressBar;
    @FXML
    private Button computeBtn, playBtn, pauseBtn, resetBtn;
    @FXML
    private Canvas effectCanvas, normalCanvas;

    // Audio Processing
    private Spectrograph originalSpectrograph;
    private Spectrograph modifiedSpectrograph;
    public String chosenEffect;
    private Stage rootStage;
    private Scene rootScene;

    // Arrays for storing original and modified audio data
    public byte[] originalArray;
    public byte[] modifiedArray;

    /**
     * Sets the original and modified audio data arrays.
     *
     * @param originalArray The original audio data.
     * @param modifiedArray The modified audio data after applying effects.
     */
    public void setArrays(byte[] originalArray, byte[] modifiedArray) {
        this.originalArray = originalArray;
        this.modifiedArray = modifiedArray;
    }

    /**
     * Initializes the user interface components and binds actions to buttons and sliders.
     */
    @FXML
    private void initialize() {
        // Initializing spectrographs for visualizing the audio data
        originalSpectrograph = new Spectrograph();
        modifiedSpectrograph = new Spectrograph();

        // Hiding progress bars initially
        leftProgressBar.setVisible(false);
        rightProgressBar.setVisible(false);

        // Disabling buttons initially
        playBtn.setDisable(true);
        pauseBtn.setDisable(true);
        resetBtn.setDisable(true);

        // Handling compute button clicks to compute FFT frames for both audio arrays
        computeBtn.setOnMouseClicked(event -> {
            leftProgressBar.setVisible(true);
            rightProgressBar.setVisible(true);

            originalSpectrograph.computeFFTFrames(originalArray, leftProgressBar);
            modifiedSpectrograph.computeFFTFrames(modifiedArray, rightProgressBar);

            playBtn.setDisable(false);
            pauseBtn.setDisable(false);
            resetBtn.setDisable(false);
        });

        // Handling play button action to start the spectrograph animations
        playBtn.setOnAction(event -> {
            originalSpectrograph.startAnimation(normalCanvas);
            modifiedSpectrograph.startAnimation(effectCanvas);
        });

        // Handling pause button action to stop the spectrograph animations
        pauseBtn.setOnAction(event -> {
            originalSpectrograph.stopAnimation();
            modifiedSpectrograph.stopAnimation();
        });

        // Handling reset button action to reset the spectrographs
        resetBtn.setOnAction(event -> {
            originalSpectrograph.reset(normalCanvas);
            modifiedSpectrograph.reset(effectCanvas);
        });

    }

    /**
     * Sets the stage for the unit testing scene
     *
     * @param stage The stage to set
     */
    public void setStage(Stage stage) {
        rootStage = stage;
    }

    /**
     * Sets the scene for the unit testing window
     *
     * @param scene The scene to set
     */
    public void setScene(Scene scene)  {
        this.rootScene = scene;
    }

}
