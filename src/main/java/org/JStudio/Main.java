package org.JStudio;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
//    public String currentUser;

    private UIController controller;

    @Override
    public void start(Stage stage) throws Exception {
//        FileLoader fileLoader = new FileLoader();

//        Reverb reverb = new Reverb();
//        reverb.play();

        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("JStudio-UI.fxml"));
        Parent root = loader.load();

        controller = loader.getController();
        controller.setStage(stage);
        controller.setScreenSize();


//        LoadingScreen loadingScreen = new LoadingScreen();

//        Scene scene = new Scene(loadingScreen, 1280, 720);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());

        stage.setScene(scene);
//        stage.setTitle("JStudio");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(true);
        stage.show();

        controller.setSplitRatio();

//        Reverb testreverb = new Reverb();
//        testreverb.start(new Stage());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
