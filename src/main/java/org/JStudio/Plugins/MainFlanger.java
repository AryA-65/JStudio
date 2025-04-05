package org.JStudio.Plugins;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class that plays the audio with flanger effect
 * @author Theodore georgiou
 */
public class MainFlanger extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        FlangerPlugin flanger = new FlangerPlugin(0.2, 0.5, 2000);
        flanger.applyFlangerEffect();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
