package org.JStudio.Plugins;

import org.JStudio.Plugins.Views.EqualizerView;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.JStudio.SettingsController;

public class MainEqualizer{

    public void openEQ() {
        
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        
        EqualizerView eqView = new EqualizerView();
        eqView.setStage(stage);
        
        Scene scene = new Scene(eqView);
        if (SettingsController.getStyle()) {
            scene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
        } else {
            scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
        }
        stage.setScene(scene);
        
        stage.setOnCloseRequest(e ->{
            eqView.getEqualizerController().getLine().close();
        });
        
        stage.show();
    }
    
}
