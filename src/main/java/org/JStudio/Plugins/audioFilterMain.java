package org.JStudio.Plugins;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.JStudio.Plugins.Models.audioFilters;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static org.JStudio.Plugins.Models.audioFilters.*;

public class audioFilterMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/low_highCutOffFilter.fxml"));
        fxmlLoader.setController(new audioFilterFXMLController());

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 600, 200);

        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }

}
