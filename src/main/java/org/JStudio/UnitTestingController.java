package org.JStudio;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.shape.MeshView;
import javafx.stage.Stage;
import org.JStudio.Utils.FFTHandler;

public class UnitTestingController {

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

    private Stage rootStage;
    private Scene rootScene;


    @FXML
    private void initialize() {
        fft = new FFTHandler();

        leftSlider.valueProperty().bindBidirectional(rightSlider.valueProperty());





    }


    public void setStage(Stage stage) {
        rootStage = stage;
        rootStage.setMaximized(true);
    }

    public void setScene(Scene scene)  {
        this.rootScene = scene;
    }
}
