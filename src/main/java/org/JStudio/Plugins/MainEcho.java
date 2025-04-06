package org.JStudio.Plugins;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Plays audio with echo effect
 * @author Theodore Georgiou
 */
public class MainEcho extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //  Range of 5000-50000 for diffusion
        EchoPlugin echo = new EchoPlugin(10000, 0.5, 20000, 10, 0.5);
        echo.setEchoEffect();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
