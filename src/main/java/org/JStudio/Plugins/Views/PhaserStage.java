package org.JStudio.Plugins.Views;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.JStudio.Plugins.Controllers.PhaserFXMLController;
import org.JStudio.SettingsController;

/**
 * Phaser Window
 * @author Theodore Georgiou
 */
public class PhaserStage extends Stage{
    public static Scene scene;
    public static PhaserFXMLController controller;
    
    /**
     * Creates the stage
     */
    public PhaserStage() {
        setTitle("Phaser");
        initModality(Modality.APPLICATION_MODAL);
        initPlugin();
    }
    
    /**
     * Initializes the fxml file
     */
    private void initPlugin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/phaser_layout.fxml"));
            controller = new PhaserFXMLController();
            fxmlLoader.setController(controller);
            controller.setWindow(this);

            Parent root = fxmlLoader.load();
            scene = new Scene(root, 640, 480);
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
