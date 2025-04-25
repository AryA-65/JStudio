package org.JStudio;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class SettingsController {
    private static boolean style = false; // false means light, true means dark
    private ToggleGroup group;

    @FXML
    RadioButton lightRadio, darkRadio;
    @FXML
    public void initialize() {
        group = new ToggleGroup();
        lightRadio.setToggleGroup(group);
        darkRadio.setToggleGroup(group);

        RadioButton selected = (RadioButton) group.getSelectedToggle();

        switch (selected.getId()) {
            case "lightRadio":
                style = false;
                return;
            case "darkRadio":
                style = true;
            default:
                style = false;
        }


        //todo implement the other funcitons
    }

    public static boolean getStyle() {
        return style;
    }

    public static void setStyle(boolean style) {
        SettingsController.style = style;
    }
}
