package org.JStudio.Plugins.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.JStudio.Plugins.Controllers.AudioAmplitudeFXMLController;
import org.JStudio.SettingsController;

import java.io.IOException;

public class AudioAmplitudeStage extends Stage {
    public static Scene scene;

    /**
     * Creates the stage
     */
    public AudioAmplitudeStage() {
        setTitle("Audio Amplitude");
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);
        initPlugin();
    }

    /**
     * Initializes the fxml file
     */
    private void initPlugin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/other_fxmls/audioAmplitude.fxml"));
            AudioAmplitudeFXMLController controller = new AudioAmplitudeFXMLController();
            controller.setStage(this);
            fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();
            scene = new Scene(root, 600, 600);
            if (SettingsController.getStyle()) {
                scene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
            } else {
                scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
            }
            sizeToScene();
            setScene(scene);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
