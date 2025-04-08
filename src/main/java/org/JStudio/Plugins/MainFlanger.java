package org.JStudio.Plugins;

import org.JStudio.Plugins.Controllers.FlangerFXMLController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class that plays the audio with flanger effect
 * @author Theodore Georgiou
 */
public class MainFlanger extends Application{
    public static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/flanger_layout.fxml"));
        fxmlLoader.setController(new FlangerFXMLController());

        Parent root = fxmlLoader.load();
        scene = new Scene(root, 640, 480);
        primaryStage.sizeToScene();
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
        primaryStage.setAlwaysOnTop(false);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
