package org.JStudio.Plugins.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.JStudio.Plugins.Controllers.audioFilterFXMLController;
import org.JStudio.Plugins.Synthesizer.Controller;

import java.io.IOException;
import org.JStudio.SettingsController;

public class SynthesizerStage extends Stage {
    public static Scene scene;

    public SynthesizerStage() {
        setTitle("Synthesizer");
        initModality(Modality.APPLICATION_MODAL);
        initPlugin();
    }

    private void initPlugin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("other_fxmls/waveGenerator.fxml"));
            Controller myController = fxmlLoader.getController();

            if (myController != null) {
                System.out.println("Is not null");
                myController.setScene(scene);
                myController.setStage(this);
            } else {
                System.err.println("Error!");
            }

            Parent root = fxmlLoader.load();
            scene = new Scene(root, 650, 400);
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
