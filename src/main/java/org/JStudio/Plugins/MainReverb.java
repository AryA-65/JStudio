package org.JStudio.Plugins;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//comment: works better than the previous reverb, but still needs to be modified

public class MainReverb extends Application{
    public static final String REVERB_SCENE = "reverb_layout_TG";
    public static Scene scene;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/reverb_layout.fxml"));
        fxmlLoader.setController(new ReverbFXML());

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
