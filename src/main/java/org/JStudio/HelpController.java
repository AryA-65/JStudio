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

    @FXML
    ScrollPane scrollPane;

    Descriptions[] tools = {
            new Descriptions(
                    "Text Analyzer",
                    "Analyzes text for word count, character count, and other metrics",
                    new String[]{"Input text"},
                    "Analysis results"
            ),
            new Descriptions(
                    "Text Analyzer",
                    "Analyzes text for word count, character count, and other metrics",
                    new String[]{"Input text"},
                    "Analysis results"
            ),
            new Descriptions(
                    "Text Analyzer",
                    "Analyzes text for word count, character count, and other metrics",
                    new String[]{"Input text"},
                    "Analysis results"
            ),
            new Descriptions(
                    "Text Analyzer",
                    "Analyzes text for word count, character count, and other metrics",
                    new String[]{"Input text"},
                    "Analysis results"
            ),
            new Descriptions(
                    "Text Analyzer",
                    "Analyzes text for word count, character count, and other metrics",
                    new String[]{"Input text"},
                    "Analysis results"
            ),
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

    public void initialize() {
        toolBox.setSpacing(20);
        toolBox.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");

        for (Descriptions tool : tools) {
            VBox toolVBox = new VBox(8);
            toolVBox.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 5;");

            Label nameLabel = new Label("Tool: " + tool.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

            Label descLabel = new Label("Description: " + tool.getDescription());
            descLabel.setWrapText(true);

            StringBuilder inputs = new StringBuilder("Inputs: ");
            for (String input : tool.getInputs()) {
                inputs.append("\n- ").append(input);
            }
            Label inputsLabel = new Label(inputs.toString());

            Label outputLabel = new Label("Output: " + tool.getOutput());

            toolVBox.getChildren().addAll(
                    nameLabel,
                    descLabel,
                    inputsLabel,
                    outputLabel
            );

            toolBox.getChildren().add(toolVBox);
        }
    }
}
