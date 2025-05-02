package org.JStudio;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.shape.MeshView;

public class UnitTestingController {

    @FXML
    private Label fileNameLabel;
    @FXML
    private TabPane leftTabPane;

    @FXML
    private Canvas leftSpectrographCanvas, rightSpectrographCanvas;

    @FXML
    private MeshView leftMeshView, rightMeshView;

    @FXML
    private Slider leftSlider, rightSlider;

    @FXML
    private ProgressBar leftProgressBar, rightProgressBar;

    @FXML
    private Button loadButton,playButton, rightTabPane, pauseButton, computeButton;

    @FXML
    private void initialize() {
        // Initialization logic here (optional)
    }


}
