package org.JStudio.Plugins.Views;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.JStudio.Plugins.Controllers.ReverbFXMLController;

/**
 * Reverb Window
 * @author Theodore Georgiou
 */
public class ReverbStage extends Stage{
    public static Scene scene;
    
    /**
     * Creates the stage
     */
    public ReverbStage() {
        setTitle("Reverb");
        initModality(Modality.APPLICATION_MODAL);
        initPlugin();
    }
    
    /**
     * Initializes the fxml file
     */
    private void initPlugin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/reverb_layout.fxml"));
            fxmlLoader.setController(new ReverbFXMLController());

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
