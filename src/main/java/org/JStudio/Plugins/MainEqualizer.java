package org.JStudio.Plugins;

import org.JStudio.Plugins.Views.EqualizerView;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.JStudio.SettingsController;

public class MainEqualizer{

    public void openEQ() {
        //creates a new stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        //creates an equalizer view for the UI elements
        EqualizerView eqView = new EqualizerView();
        eqView.setStage(stage);
        
        //creates a scene and loads the selected theme (dark/light mode)
        Scene scene = new Scene(eqView);
        if (SettingsController.getStyle()) {
            scene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
        } else {
            scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
        }
        stage.setScene(scene);
        
        //stop any audio when the equalizer closes
        stage.setOnCloseRequest(e ->{
            try{
            eqView.getEqualizerController().getLine().close();
            } catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        });
        
        stage.show();
    }
    
}
