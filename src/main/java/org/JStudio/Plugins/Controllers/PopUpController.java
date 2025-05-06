package org.JStudio.Plugins.Controllers;

import java.util.Optional;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.JStudio.Controllers.SettingsController;

public class PopUpController {
    //show a popup with a text field for the user to input text
    public String showTextInputPopup() {
        //creates a new dialog
        Dialog<String> dialog = new Dialog<>();
        
        //set dark/light mode
        if (SettingsController.getStyle()) {
            dialog.getDialogPane().getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
        } else {
            dialog.getDialogPane().getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
        }
        
        dialog.setTitle("Add Track");
        dialog.setHeaderText(null);

        //sets the UI
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton);

        TextField inputNameField = new TextField();
        inputNameField.setId("popupTextField");
        inputNameField.setPromptText("Name");

        VBox content = new VBox(inputNameField);
        content.setSpacing(10);
        dialog.getDialogPane().setContent(content);

        // Convert result to String when "Add" is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                return inputNameField.getText();
            }
            return null;
        });

        //shows the dialog and waits for the result
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
    
    public void showWarningPopup(String message) {
        //creates a new dialog
        Dialog<String> dialog = new Dialog<>();
        
        //set dark/light mode
        if (SettingsController.getStyle()) {
            dialog.getDialogPane().getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
        } else {
            dialog.getDialogPane().getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
        }
        
        dialog.setTitle("Warning");
        dialog.setHeaderText(null);

        //sets the UI
        ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton);

        Label messageLabel = new Label(message);

        VBox content = new VBox(messageLabel);
        content.setSpacing(10);
        dialog.getDialogPane().setContent(content);
        
        //shows the dialog
        dialog.showAndWait();
    }
}
