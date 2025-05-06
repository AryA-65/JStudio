package org.JStudio.Plugins.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.JStudio.Plugins.Controllers.AudioFilterFXMLController;

import java.io.IOException;
import org.JStudio.SettingsController;

public class BaseFiltersStage extends Stage {
    public static Scene scene;
    public static AudioFilterFXMLController controller = new AudioFilterFXMLController();

    /**
     * Creates the stage
     */
    public BaseFiltersStage() {
        setTitle("Base Filters");
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);
        initPlugin();
    }

    /**
     * Initializes the fxml file
     */
    private void initPlugin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/other_fxmls/low_highCutOffFilter.fxml"));
            controller.setStage(this);

            fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();
            scene = new Scene(root, 600, 200);
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
