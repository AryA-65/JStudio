package org.JStudio;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Settings window
 * @author Theodore Georgiou
 */
public class SettingsWindow extends Stage{
    Scene scene;
    /**
     * Creates the stage
     */
    public SettingsWindow() {
        setTitle("Settings");
        initModality(Modality.APPLICATION_MODAL);
        openSettings();
    }
    
    /**
     * Initializes the fxml file
     */
    private void openSettings() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("settings_layout.fxml"));
            fxmlLoader.setController(new SettingsController());

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
