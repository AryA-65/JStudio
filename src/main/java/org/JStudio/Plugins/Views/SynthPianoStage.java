package org.JStudio.Plugins.Views;

import org.JStudio.Plugins.Controllers.SynthPianoController;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.JStudio.SettingsController;

public class SynthPianoStage {

    public void openSynthPiano() {
        try {
            //creates a new stage
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setMaximized(true);

            //loads the fxml file
            FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/SynthPiano.fxml"));
            Parent root = fxmlLoader.load();

            SynthPianoController synthPianoController = fxmlLoader.getController();

            //creates a scene and loads the selected theme (dark/light mode)
            Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
            if (SettingsController.getStyle()) {
                scene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
            } else {
                scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
            }

            //stop audio playback on close
            stage.setOnCloseRequest(e -> {
                synthPianoController.getSynthPiano().setShouldStopPlayback(true);
            });

            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
