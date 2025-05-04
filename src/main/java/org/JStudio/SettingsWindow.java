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
    private Scene scene;
    /**
     * Creates the stage
     */
    public SettingsWindow() {
        setTitle("Settings");
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);
        openSettings();
    }
    
    /**
     * Initializes the fxml file
     */
    private void openSettings() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("settings_layout.fxml"));
            fxmlLoader.setController(new SettingsController());
            SettingsController.setMainStage(this);

            Parent root = fxmlLoader.load();
            scene = new Scene(root, 600, 400);
            updateStyle();
            sizeToScene();
            setScene(scene);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    /**
     * Updates style of main scene (light or dark)
     */
    public void updateStyle() {
        scene.getStylesheets().clear();
        if (SettingsController.getStyle()) {
            scene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
        } else if (!SettingsController.getStyle()) {
            scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
        }
    }
}
