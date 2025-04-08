package org.JStudio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
//    public String currentUser;

    private UIController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("JStudio-UI.fxml"));
        Parent root = loader.load();

        controller = loader.getController();
        controller.setStage(stage);
        controller.setScreenSize();


        Scene scene = new Scene(root);
        scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());

        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(true);
        stage.show();

        controller.setSplitRatio();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
