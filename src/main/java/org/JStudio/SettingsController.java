package org.JStudio;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class SettingsController {
    private static boolean style; // false means light, true means dark
    private ToggleGroup group;
    private static RadioButton selected;
    private static UIController controller;
    private static SettingsWindow window;
    
    @FXML
    RadioButton lightRadio, darkRadio;
    

    public void initialize() {
        group = new ToggleGroup();
        lightRadio.setToggleGroup(group);
        darkRadio.setToggleGroup(group);
        
        if (selected != null) {
            if (selected.equals(lightRadio)) {
                lightRadio.setSelected(true);
            } else {
                darkRadio.setSelected(true);
            }
        }

        // Check which button selected and set style accordingly
        group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> styleChoice, Toggle oldStyleChoice, Toggle newStyleChoice) -> {
            selected = (RadioButton) group.getSelectedToggle();
            System.out.println(selected.getId());
            switch (selected.getId()) {
            case "lightRadio":
                style = false;
                break;
            case "darkRadio":
                style = true;
                break;
            default:
                style = false;
                break;
            }
            updateMainUIStyle();
            updateSettingsUIStyle();
        });
        
        //todo implement the other funcitons
    }

    public static boolean getStyle() {
        return style;
    }

    public static void setStyle(boolean style) {
        SettingsController.style = style;
    }

    public static void setController(UIController controller) {
        SettingsController.controller = controller;
    }

    public static void setWindow(SettingsWindow window) {
        SettingsController.window = window;
    }
    
    public void updateMainUIStyle() {
        SettingsController.controller.updateStyle();
    }
    
    public void updateSettingsUIStyle() {
        SettingsController.window.updateStyle();
    }
}
