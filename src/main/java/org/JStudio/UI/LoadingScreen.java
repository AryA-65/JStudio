package org.JStudio.UI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Queue;

public class LoadingScreen {

    @FXML
    private Label loading_label;
    @FXML
    private ProgressBar loading_progress;
    @FXML
    private Label build_version;
    @FXML
    private ImageView loading_logo;
    @FXML
    private Stage rootStage;

    public void setStage(Stage stage) {
        this.rootStage = stage;
    }

    @FXML
    private void initialize() {
        loading_progress.setProgress(0);
        rootStage.initStyle(StageStyle.TRANSPARENT);
    }

    public boolean setLoading_label(String text, double progress) {
        if (text.equals("done")) {
            try {
                rootStage.wait(250);
            } catch (Exception e) {
                e.printStackTrace();
            }
            rootStage.close();
            return true;
        }
        loading_label.setText(text);
        loading_progress.setProgress(progress);
        return false;
    }
}
