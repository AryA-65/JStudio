package org.JStudio.Plugins.Views;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.JStudio.Plugins.Controllers.ChorusFXMLController;
import org.JStudio.Controllers.SettingsController;


public class ChorusStage extends Stage {
    public static Scene scene;
    public static ChorusFXMLController controller;
    
    /**
     * Creates the stage
     */
    public ChorusStage() {
        setTitle("Chorus");
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
            FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/chorus_layout.fxml"));
            controller = new ChorusFXMLController();
            fxmlLoader.setController(controller);
            controller.setWindow(this);

            Parent root = fxmlLoader.load();
            scene = new Scene(root, 600, 200);
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
