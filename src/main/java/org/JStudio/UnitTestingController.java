package org.JStudio;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.shape.MeshView;

public class UnitTestingController {
    @FXML
    private Button loadButton;

    @FXML
    private Label fileNameLabel;
    @FXML
    private TabPane leftTabPane;

    @FXML
    private Canvas leftSpectrographCanvas;

    @FXML
    private MeshView leftMeshView;

    @FXML
    private Slider leftSlider;

    @FXML
    private ProgressBar leftProgressBar;

    // Middle column (GridPane columnIndex=1)
    @FXML
    private Button computeButton;

    @FXML
    private Button playButton, rightTabPane;

    @FXML
    private Button pauseButton;
    

    @FXML
    private Canvas rightSpectrographCanvas;

    @FXML
    private MeshView rightMeshView;

    @FXML
    private Slider rightSlider;

    @FXML
    private ProgressBar rightProgressBar;

    // Initialization method if needed
    @FXML
    private void initialize() {
        // Initialization logic here (optional)
    }


}
