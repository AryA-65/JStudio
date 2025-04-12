package org.JStudio.Plugins.Views;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.JStudio.Plugins.Controllers.ChorusFXMLController;


public class ChorusStage extends Stage{
    public static Scene scene;
    
    /**
     * Creates the stage
     */
    public ChorusStage() {
        setTitle("Chorus");
        initModality(Modality.APPLICATION_MODAL);
        initPlugin();
    }
    
    /**
     * Initializes the fxml file
     */
    private void initPlugin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/chorus_layout.fxml"));
            fxmlLoader.setController(new ChorusFXMLController());

            Parent root = fxmlLoader.load();
            scene = new Scene(root, 640, 480);
            sizeToScene();
            setScene(scene);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
