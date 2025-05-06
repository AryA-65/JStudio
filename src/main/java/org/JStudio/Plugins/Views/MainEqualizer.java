package org.JStudio.Plugins.Views;

import org.JStudio.Plugins.Views.EqualizerView;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.JStudio.Controllers.SettingsController;

public class MainEqualizer {
    
    private EqualizerView eqView = new EqualizerView();
    
    public EqualizerView getEQView(){
        return eqView;
    }

    public void openEQ() {
        //creates a new stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        //sets the view in the EQView
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
        stage.setOnCloseRequest(e -> {
            try {
                eqView.getEqualizerController().getEqualizer().stopAudio();
                if (eqView.getEqualizerController().getEqualizer().getPlayingThread() != null) {
                    eqView.getEqualizerController().getEqualizer().getPlayingThread().interrupt();
                }
                if (eqView.getEqualizerController().getEqualizer().getLine() != null) {
                    eqView.getEqualizerController().getEqualizer().getLine().close();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });

        stage.show();
    }

}
