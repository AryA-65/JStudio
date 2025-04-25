package org.JStudio;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class SettingsController {
    private static boolean style = false; // false means light, true means dark
    private ToggleGroup group;

    @FXML
    RadioButton lightRadio, darkRadio;


    // For now opening settings turns plugin to dark mode
    @FXML
    public void initialize() {
//        group = new ToggleGroup(); //todo creates an error
//        lightRadio.setToggleGroup(group);
//        darkRadio.setToggleGroup(group);

        style = true;
    }

    public static boolean getStyle() {
        return style;
    }

    public static void setStyle(boolean style) {
        SettingsController.style = style;
    }
}
