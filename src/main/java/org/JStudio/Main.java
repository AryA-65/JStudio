package org.JStudio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.JStudio.Plugins.Controllers.ButterworthFXMLController;

public class Main extends Application {
//    public String currentUser;

    private UIController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/ButterworthFilter.fxml"));

        ButterworthFXMLController butterworthFxmlController = new ButterworthFXMLController();
        loader.setController(butterworthFxmlController);

        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();




//        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("JStudio-UI.fxml"));

//        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/ButterworthFilter.fxml"));
//
//        ButterworthFxmlController butterworthFxmlController = new ButterworthFxmlController();
//        loader.setController(butterworthFxmlController);
//

//        Parent root = loader.load();
//
//        Scene scene = new Scene(root);
//        scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
//        stage.setScene(scene);
//        stage.show();



//        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("JStudio-UI.fxml"));
//        Parent root = loader.load();
//
//        controller = loader.getController();
//        controller.setStage(stage);
//        controller.setScreenSize();
//
//
//        Scene scene = new Scene(root);
//        scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
//
//        stage.setScene(scene);
//        stage.initStyle(StageStyle.TRANSPARENT);
//        stage.setResizable(true);
//        stage.show();
//
//        controller.setSplitRatio();

        /**
         * to initialize the login page
         */
//        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/loginLayout.fxml"));
//        Parent root = loader.load();
//
//        LoginController loginController = loader.getController();
//        loginController.setRootStage(stage);
//
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        loginController.setRootScene(scene);
//        stage.setResizable(false);
//        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
