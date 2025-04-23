package org.JStudio.Plugins.Views;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.JStudio.Plugins.Controllers.PhaserFXMLController;

/**
 * Phaser Window
 * @author Theodore Georgiou
 */
public class PhaserStage extends Stage{
    public static Scene scene;
    
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
            fxmlLoader.setController(new PhaserFXMLController());

            Parent root = fxmlLoader.load();
            scene = new Scene(root, 640, 480);
            scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
            scene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
            sizeToScene();
            setScene(scene);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
