package org.JStudio.Plugins.Controllers;

import java.util.Optional;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class PopUpController {
    public String showTextInputPopup() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Track");
        dialog.setHeaderText(null);

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        TextField inputNameField = new TextField();
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

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
}
