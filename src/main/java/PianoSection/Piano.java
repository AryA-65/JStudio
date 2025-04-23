package PianoSection;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Piano {
    public void openPiano() {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            
            Parent root = FXMLLoader.load(ClassLoader.getSystemResource("other_fxmls/Notes.fxml"));
            Scene scene = new Scene(root,1500,900);
            scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
            scene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
