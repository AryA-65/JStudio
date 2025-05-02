package org.JStudio;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.JStudio.Utils.Descriptions;

public class HelpController {
    @FXML
    VBox toolBox;

    public void initialize() {
        // Create your array of Description objects
        Descriptions[] tools = {
                new Descriptions(
                        "Text Analyzer",
                        "Analyzes text for word count, character count, and other metrics",
                        new String[]{"Input text"},
                        "Analysis results"
                ),
                new Descriptions(
                        "Image Resizer",
                        "Resizes images to specified dimensions",
                        new String[]{"Image file", "Width", "Height"},
                        "Resized image"
                )
        };

        // Main container VBox
        toolBox = new VBox(20); // 20px spacing between items
        toolBox.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");

        // Loop through each Description object and create a VBox for it
        for (Descriptions tool : tools) {
            // Create individual VBox for each tool
            VBox toolVBox = new VBox(8); // 8px spacing between fields
            toolVBox.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 5;");

            // Add all fields with proper formatting
            Label nameLabel = new Label("Tool: " + tool.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

            Label descLabel = new Label("Description: " + tool.getDescription());
            descLabel.setWrapText(true);

            // Format inputs
            StringBuilder inputs = new StringBuilder("Inputs: ");
            for (String input : tool.getInputs()) {
                inputs.append("\n- ").append(input);
            }
            Label inputsLabel = new Label(inputs.toString());

            Label outputLabel = new Label("Output: " + tool.getOutput());


            // Add all components to the tool's VBox
            toolVBox.getChildren().addAll(
                    nameLabel,
                    descLabel,
                    inputsLabel,
                    outputLabel
            );

            // Add the tool's VBox to the main container
            toolBox.getChildren().add(toolVBox);
        }

        // Set up the scene
        ScrollPane scrollPane = new ScrollPane(toolBox);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane, 600, 500);
//        stage.setTitle("Tool Descriptions");
//        stage.setScene(scene);
//        stage.show();

    }

}
