package org.JStudio.Plugins;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainReverb extends Application{
    public static final String REVERB_SCENE = "reverb_layout_TG";
    public static Scene scene;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = loadFXML(REVERB_SCENE, new ReverbFXML());
        scene = new Scene(root, 640, 480);
        primaryStage.sizeToScene();
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
        primaryStage.setAlwaysOnTop(false);
    }
    
    public static Parent loadFXML(String fxmlFile, Object fxmlController) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainReverb.class.getResource("/fxml/" + fxmlFile + ".fxml"));
        fxmlLoader.setController(fxmlController);
        return fxmlLoader.load();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
