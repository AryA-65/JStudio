package org.JStudio;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.JStudio.Plugins.Models.Reverb;
import org.JStudio.Plugins.Views.EchoStage;
import org.JStudio.Plugins.Views.FlangerStage;
import org.JStudio.Plugins.Views.ReverbStage;
import org.JStudio.TESTING.UnitTestingController;

public class Main extends Application {
    private UIController controller;

    private Scene scene;

    boolean isTesting = false;

    @Override
    public void start(Stage stage) throws Exception {
//        if (isTesting) {
//            FXMLLoader testLoader = new FXMLLoader(ClassLoader.getSystemResource("JStudioTestUI.fxml"));
//            Parent root = testLoader.load();
//
//            UnitTestingController testController = testLoader.getController();
//            testController.setStage(stage);
//
//            scene = new Scene(root);
////            scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
//            testController.setScene(scene);
////            stage.getIcons().add(new Image("/JS_ico.png"));
//            stage.setScene(scene);
////            stage.initStyle(StageStyle.TRANSPARENT);
////            stage.setResizable(true);
//            stage.show();
//
//        } else {
//            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("JStudio-UI.fxml"));
//            Parent root = loader.load();
//
//            controller = loader.getController();
//            controller.setStage(stage);
//
//            scene = new Scene(root);
//            scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
//            controller.setScene(scene);
//            stage.getIcons().add(new Image("/JS_ico.png"));
//            stage.setScene(scene);
//            stage.initStyle(StageStyle.TRANSPARENT);
//            stage.setResizable(true);
//            stage.show();
//
//            SettingsController.setController(controller);
//        }


        /**
         * to initialize the login page
         */
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/loginLayout.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setRootStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        loginController.setRootScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
