/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.JStudio.Plugins;

import org.JStudio.Plugins.Views.EqualizerView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author alexa
 */
// used temporarily to run the equalizer
public class MainEqualizer{

    public void openEQ() {
        
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        //open new scene
        EqualizerView eqView = new EqualizerView();
        Scene scene = new Scene(eqView);
        stage.setScene(scene);
        stage.show();
    }
    
}
