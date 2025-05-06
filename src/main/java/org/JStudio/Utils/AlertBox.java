package org.JStudio.Utils;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;
import org.JStudio.Controllers.SettingsController;

/**
 * Class that provides functionality to display a modal alert window with a title and message
 * The alert window includes a label with a message and a "Close" button to close the window.
 */
public class AlertBox {
    /**
     * Displays a modal alert box with the given title and message
     *
     * @param title   The title of the alert box
     * @param message The message to be displayed in the alert box
     */
    public static void display(String title, String message) {
        Stage window = new Stage();

        // Set the modality of the window to prevent interaction with other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        // Create a label and button for the alert box
        Label label = new Label();
        label.setId("alertLabel");
        label.setText(message);
        Button closeButton = new Button("Close");
        closeButton.setId("alertButton");
        closeButton.setOnAction(event -> window.close());

        // Create a layout to arrange the label and button
        VBox layout = new VBox(10);
        layout.setId("alertLayout");
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        // Set the scene with the layout
        Scene scene = new Scene(layout);

        // Apply the appropriate stylesheet based on user preferences
        if (SettingsController.getStyle()) {
            scene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
        } else {
            scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
        }

        // Set the scene and show the window
        window.setScene(scene);
        window.showAndWait();
    }
}
