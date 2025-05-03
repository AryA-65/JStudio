package org.JStudio;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.shape.MeshView;
import javafx.stage.Stage;
import org.JStudio.Plugins.Models.Plugin;
import org.JStudio.Utils.FFTHandler;
import org.JStudio.Utils.Spectrograph;

import java.util.Random;

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
    private Button loadBtn, computeBtn,playBtn, pauseBtn,resetBtn;


    //<--- Fields --->
    private FFTHandler fft;
    private Spectrograph spectrograph;

    private Stage rootStage;
    private Scene rootScene;

    @FXML
    private Canvas effectCanvas, normalCanvas;


    @FXML
    private void initialize() {
//        fft = new FFTHandler();
        leftSlider.valueProperty().bindBidirectional(rightSlider.valueProperty());

        spectrograph = new Spectrograph(normalCanvas);

        loadBtn.setOnMouseClicked(event -> {
            originalAudio = null;
            convertAudioFileToByteArray();
            fileNameLabel.setText("File Name: " + getFileName());
        });

        computeBtn.setOnMouseClicked(event -> spectrograph.computeFFTFrames());

        playBtn.setOnMouseClicked(event -> spectrograph.startSpectrographAnimation());

        pauseBtn.setOnMouseClicked(event -> spectrograph.stopSpectrographAnimation());

        resetBtn.setOnMouseClicked(event -> spectrograph.resetSpectrograph());
    }
//    private void processAudioForSpectrogram() {
//        if (originalAudio == null) return;
//
//        short[] audioData = convertToShortArray();
//
//        int chunkSize = 1024; // FFT size
//        for (int i = 0; i < audioData.length - chunkSize; i += chunkSize/2) { // 50% overlap
//            short[] chunk = new short[chunkSize];
//            System.arraycopy(audioData, i, chunk, 0, chunkSize);
//
//            // Convert to double[] for FFT
//            double[] doubleChunk = shortToDouble(chunk);
//
//            // Update spectrogram (you'll need to modify your Spectrograph class)
//            spectrograph.update(doubleChunk);
//
//            try {
//                Thread.sleep(10); // Control playback speed
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }

    public void setStage(Stage stage) {
        rootStage = stage;
//        rootStage.setMaximized(true);
    }

    public void setScene(Scene scene)  {
        this.rootScene = scene;
    }

}
