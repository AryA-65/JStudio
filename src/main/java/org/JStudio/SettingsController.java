package org.JStudio;

import javafx.fxml.FXML;

public class SettingsController {
    private static boolean style = false; // false means light, true means dark
    
    // For now opening settings turns plugin to dark mode
    @FXML
    public void initialize() {
        style = true;
    }

    public static boolean getStyle() {
        return style;
    }

    public static void setStyle(boolean style) {
        SettingsController.style = style;
    }
}
