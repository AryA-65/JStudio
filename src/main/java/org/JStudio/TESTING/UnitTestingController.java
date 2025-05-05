package org.JStudio.TESTING;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.JStudio.Plugins.Models.Plugin;
import org.JStudio.Utils.Spectrograph;

public class UnitTestingController extends Plugin {

    @FXML
    private Label fileNameLabel;
    @FXML
    private Slider leftSlider, rightSlider;
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


    public byte[] originalArray;
    public byte[] modifiedArray;

    public void setArrays(byte[] originalArray, byte[] modifiedArray) {
        this.originalArray = originalArray;
        this.modifiedArray = modifiedArray;
    }

    @FXML
    private void initialize() {
        leftSlider.valueProperty().bindBidirectional(rightSlider.valueProperty());
        originalSpectrograph = new Spectrograph();
        modifiedSpectrograph = new Spectrograph();

        leftProgressBar.setVisible(false);
        rightProgressBar.setVisible(false);
        playBtn.setDisable(true);
        pauseBtn.setDisable(true);
        resetBtn.setDisable(true);


        computeBtn.setOnMouseClicked(event -> {
            leftProgressBar.setVisible(true);
            rightProgressBar.setVisible(true);

            originalSpectrograph.computeFFTFrames(originalArray, leftProgressBar);
            modifiedSpectrograph.computeFFTFrames(modifiedArray, rightProgressBar);

            playBtn.setDisable(false);
            pauseBtn.setDisable(false);
            resetBtn.setDisable(false);
        });


        playBtn.setOnAction(event -> {
            originalSpectrograph.startAnimation(normalCanvas);
            modifiedSpectrograph.startAnimation(effectCanvas);
        });

        pauseBtn.setOnAction(event -> {
            originalSpectrograph.stopAnimation();
            modifiedSpectrograph.stopAnimation();
        });

        resetBtn.setOnAction(event -> {
            originalSpectrograph.reset(normalCanvas);
            modifiedSpectrograph.reset(effectCanvas);
        });

    }

    public void setStage(Stage stage) {
        rootStage = stage;
    }

    public void setScene(Scene scene)  {
        this.rootScene = scene;
    }

}
