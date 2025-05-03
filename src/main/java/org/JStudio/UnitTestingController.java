package org.JStudio;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.JStudio.Plugins.Models.Plugin;
import org.JStudio.Utils.FFTHandler;
import org.JStudio.Utils.Spectrograph;

import java.util.Arrays;

public class UnitTestingController extends Plugin{

    @FXML
    private Label fileNameLabel;
    @FXML
    private Tab SpectrographBtn1, SpectrographBtn2;
    @FXML
    private Tab dimBtn1, dimBtn2;
    @FXML
    private Slider leftSlider, rightSlider;
    @FXML
    private ProgressBar leftProgressBar, rightProgressBar;
    @FXML
    private ComboBox<String> pluginsChooser;
    @FXML
    private Button loadBtn, computeBtn, playBtn, pauseBtn, resetBtn;
    @FXML
    private Canvas effectCanvas, normalCanvas;

    // Audio Processing
    private Spectrograph spectrograph;

    public String chosenEffect;
    private Stage rootStage;
    private Scene rootScene;

    @FXML
    private void initialize() {
        leftSlider.valueProperty().bindBidirectional(rightSlider.valueProperty());
        spectrograph = new Spectrograph(normalCanvas);

        leftProgressBar.setVisible(false);
        rightProgressBar.setVisible(false);
        computeBtn.setDisable(true);
        playBtn.setDisable(true);
        pauseBtn.setDisable(true);
        resetBtn.setDisable(true);

        pluginsChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
            chosenEffect = newValue;
        });


        loadBtn.setOnMouseClicked(event -> {
            convertAudioFileToByteArray();
            fileNameLabel.setText("File Name: " + getFileName());
            computeBtn.setDisable(false);
        });

        computeBtn.setOnMouseClicked(event -> {
            spectrograph.computeFFTFrames(originalAudio, leftProgressBar);
            leftProgressBar.setVisible(true);
            rightProgressBar.setVisible(true);
            playBtn.setDisable(false);
            pauseBtn.setDisable(false);
            resetBtn.setDisable(false);
        });
        playBtn.setOnAction(event -> spectrograph.startAnimation());
        pauseBtn.setOnAction(event -> spectrograph.stopAnimation());
        resetBtn.setOnAction(event -> spectrograph.reset());
    }

    public void setStage(Stage stage) {
        rootStage = stage;
    }

    public void setScene(Scene scene)  {
        this.rootScene = scene;
    }
}
