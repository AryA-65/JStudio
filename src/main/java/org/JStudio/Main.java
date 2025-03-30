package org.JStudio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.JStudio.Plugins.Reverb;

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

//        LoadingScreen loadingScreen = new LoadingScreen();

//        Scene scene = new Scene(loadingScreen, 1280, 720);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());

        stage.setScene(scene);
//        stage.setTitle("JStudio");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(true);
        stage.show();

//        Reverb testreverb = new Reverb();
//        testreverb.start(new Stage());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
