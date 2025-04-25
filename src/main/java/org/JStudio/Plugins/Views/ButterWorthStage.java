package org.JStudio.Plugins.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.JStudio.Plugins.Controllers.ButterworthFXMLController;
import org.JStudio.Plugins.Controllers.ReverbFXMLController;
import org.JStudio.Plugins.Synthesizer.Controller;

import java.io.IOException;
import org.JStudio.SettingsController;

public class ButterWorthStage extends Stage {
    public static Scene scene;

    public ButterWorthStage() {
        setTitle("Butterworth Filter");
        initModality(Modality.APPLICATION_MODAL);
        initPlugin();
    }

    private void initPlugin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/ButterworthFilter.fxml"));
            fxmlLoader.setController(new ButterworthFXMLController());

            Parent root = fxmlLoader.load();
            scene = new Scene(root);
            if (SettingsController.getStyle()) {
                scene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
            } else {
                scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
            }
            sizeToScene();
            setScene(scene);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
