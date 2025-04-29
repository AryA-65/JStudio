package PianoManualSynth;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import org.JStudio.SettingsController;

public class SynthMain_Piano {
    private SynthPianoController notesController;
    
    public void setNotesController(SynthPianoController nc){
        notesController = nc;
    }
    
    public void open() throws IOException {
        Stage stage = new Stage();
        
        FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/synthPianoWaveGen.fxml"));
        Parent root = fxmlLoader.load();

        SynthController_Piano myController = fxmlLoader.getController();
        myController.setNotesController(notesController);
        Scene mainScene = new Scene(root, 650, 450);
        if (SettingsController.getStyle()) {
            mainScene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
        } else {
            mainScene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
        }
        stage.setScene(mainScene);
        
        if (myController != null) {
            System.out.println("Is not null");
            myController.setScene(mainScene);
            myController.setStage(stage);
        } else {
            System.err.println("Error!");
        }
        stage.show();
    }
}
