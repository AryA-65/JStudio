package org.JStudio.Plugins.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.JStudio.Plugins.Controllers.SynthController;
import org.JStudio.SettingsController;


import java.io.IOException;

public class SynthesizerStage {
    public static Scene mainScene;
    public static Stage stage;

    public void synth() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/other_fxmls/waveGen.fxml"));
            Parent root = fxmlLoader.load();

            SynthController myController = fxmlLoader.getController();
            mainScene = new Scene(root, 650, 460);

            stage.setScene(mainScene);

            if (myController != null) {
                System.out.println("Is not null");
                myController.setScene(mainScene);
                myController.setStage(stage);
            } else {
                System.err.println("Error!");
            }

            if (SettingsController.getStyle()) {
                mainScene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
            } else {
                mainScene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
            }
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
