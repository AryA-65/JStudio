package SynthPiano;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import org.JStudio.SettingsController;

public class SynthMain_Piano {
    private SynthPianoController notesController;
    
    //setter
    public void setNotesController(SynthPianoController nc){
        notesController = nc;
    }
    
    //Open the synth-piano plugin stage
    public void open() throws IOException {
        //creates a new stage
        Stage stage = new Stage();
        
        //loads the fxml file
        FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("other_fxmls/synthPianoWaveGen.fxml"));
        Parent root = fxmlLoader.load();
        
        //gets the fxml controller
        SynthController_Piano synthController = fxmlLoader.getController();
        synthController.getSynth().setNotesController(notesController);
        
        //creates a scene and loads the selected theme (dark/light mode)
        Scene mainScene = new Scene(root, 650, 450);
        if (SettingsController.getStyle()) {
            mainScene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
        } else {
            mainScene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
        }
        
        stage.setScene(mainScene);
        
        synthController.getSynth().setTempStage(stage);
        
        stage.setOnCloseRequest(e -> {
            synthController.getSynth().getAuTh().close();
        });
        
        stage.show();
    }
}
