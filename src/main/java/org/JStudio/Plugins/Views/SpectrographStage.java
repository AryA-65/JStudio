package org.JStudio.Plugins.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.JStudio.Plugins.Controllers.EchoFXMLController;
import org.JStudio.SettingsController;
import org.JStudio.TESTING.UnitTestingController;
import org.JStudio.Utils.Spectrograph;

import java.io.IOException;

public class SpectrographStage extends Stage {
    public static Scene scene;
    public static UnitTestingController controller;

    /**
     * Creates the stage
     */
    public SpectrographStage() {
        setTitle("SpectrographS");
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);
        setResizable(false);
        initPlugin();
    }

    /**
     * Initializes the fxml file
     */
    private void initPlugin() {
        try {
            FXMLLoader testLoader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/JStudioTestUI.fxml"));
            controller = testLoader.getController();
            testLoader.setController(controller);
            Parent root = testLoader.load();

            scene = new Scene(root);
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
