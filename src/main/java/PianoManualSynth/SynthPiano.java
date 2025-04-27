package PianoManualSynth;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.JStudio.SettingsController;

public class SynthPiano{

    public void openSynthPiano() {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Parent root = FXMLLoader.load(ClassLoader.getSystemResource("other_fxmls/Notes_1.fxml"));
            Scene scene = new Scene(root,1500,900);
            if (SettingsController.getStyle()) {
            scene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
            } else {
                scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
            }
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
}
