package org.JStudio.Plugins;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.JStudio.Plugins.Views.ChorusStage;
import org.JStudio.Plugins.Views.FlangerStage;
import org.JStudio.Plugins.Views.ReverbStage;

/**
 * Sample class to test running Plugins as stages
 * @author Theodore Georgiou
 */
public class MainUIPlugins extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pane = new Pane();
        
        Button btnReverb = new Button();
        btnReverb.setOnAction(e -> {
            ReverbStage reverb = new ReverbStage();
            reverb.show();
        });
        
        btnReverb.setText("Reverb");
        
        Button btnFlanger = new Button();
        btnFlanger.setOnAction(e -> {
            FlangerStage flanger = new FlangerStage();
            flanger.show();
        });
        
        btnFlanger.setText("Flanger");
        btnFlanger.setTranslateX(50);
        
        Button btnChorus = new Button();
        btnChorus.setOnAction(e -> {
            ChorusStage chorus = new ChorusStage();
            chorus.show();
        });
        
        btnChorus.setText("Chorus");
        btnChorus.setTranslateX(100);
        
        pane.getChildren().addAll(btnReverb, btnFlanger, btnChorus);
        Scene scene = new Scene(pane, 800, 500);
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
