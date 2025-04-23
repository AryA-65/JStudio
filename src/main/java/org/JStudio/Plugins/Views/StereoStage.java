package org.JStudio.Plugins.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.JStudio.Plugins.Controllers.FlangerFXMLController;
import org.JStudio.Plugins.Controllers.StereoFXMLController;

import java.io.IOException;

public class StereoStage extends Stage {
    public static Scene scene;

    /**
     * Creates the stage
     */
    public StereoStage() {
        setTitle("Stereo");
        initModality(Modality.APPLICATION_MODAL);
        initPlugin();
    }

    /**
     * Initializes the fxml file
     */
    private void initPlugin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/stereo.fxml"));
            fxmlLoader.setController(new StereoFXMLController());

            Parent root = fxmlLoader.load();
            scene = new Scene(root, 100, 200);
            sizeToScene();
            setScene(scene);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
