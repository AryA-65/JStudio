package org.JStudio.Plugins.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.JStudio.Plugins.Controllers.ButterworthFXMLController;
import org.JStudio.Controllers.SettingsController;

import java.io.IOException;

public class ButterWorthStage extends Stage {
    public static Scene scene;
    public static ButterworthFXMLController controller = new ButterworthFXMLController();

    /**
     * Creates the stage
     */
    public ButterWorthStage() {
        setTitle("Butterworth Filter");
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);
        initPlugin();
    }

    /**
     * Initializes the fxml file
     */
    private void initPlugin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/ButterworthFilter.fxml"));
            controller.setStage(this);

            fxmlLoader.setController(controller);
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
