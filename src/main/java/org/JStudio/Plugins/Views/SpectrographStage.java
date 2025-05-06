package org.JStudio.Plugins.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.JStudio.Plugins.Controllers.EchoFXMLController;
import org.JStudio.Controllers.SettingsController;
import org.JStudio.TESTING.UnitTestingController;
import org.JStudio.Utils.Spectrograph;

import java.io.IOException;

public class SpectrographStage extends Stage {
    public static Scene scene;
    public static UnitTestingController controller;

    public byte[] input;
    public byte[] output;

    /**
     * Creates the stage
     */
    public SpectrographStage() {
        setTitle("Spectrograph");
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
            FXMLLoader testLoader = new FXMLLoader(getClass().getResource("/JStudioTestUI.fxml"));
            controller = new UnitTestingController();
            testLoader.setController(controller);
            Parent root = testLoader.load(); // Must load first

            scene = new Scene(root);
            if (SettingsController.getStyle()) {
                scene.getStylesheets().add(getClass().getResource("/darkmode.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            }

            sizeToScene();
            setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
