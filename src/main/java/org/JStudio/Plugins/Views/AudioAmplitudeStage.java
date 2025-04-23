package org.JStudio.Plugins.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.JStudio.Plugins.Controllers.AudioAmplitudeFXMLController;

import java.io.IOException;

public class AudioAmplitudeStage extends Stage {
    public static Scene scene;

    public AudioAmplitudeStage() {
        setTitle("Audio Amplitude");
        initModality(Modality.APPLICATION_MODAL);
        initPlugin();
    }

    private void initPlugin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/other_fxmls/audioAmplitude.fxml"));
            AudioAmplitudeFXMLController controller = new AudioAmplitudeFXMLController();
            controller.setStage(this);

            fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();
            scene = new Scene(root, 600, 200);
            sizeToScene();
            setScene(scene);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
