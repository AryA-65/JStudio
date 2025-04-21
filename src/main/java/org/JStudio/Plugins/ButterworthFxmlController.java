package org.JStudio.Plugins;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class ButterworthFxmlController {
    @FXML
    private Button applyButton, exportButton;

    @FXML
    private RadioButton lowPassRadio, highPassRadio, bandPassRadio, bandStopRadio;

    @FXML
    private TextField frequencyField, qualityField;

    private ToggleGroup group;

    public void initialize() {
        applyButton.setOnAction(event -> {
            System.out.println("Apply button clicked");
        });

        exportButton.setOnAction(event -> {
            System.out.println("Export button clicked");
        });

        group = new ToggleGroup();
        lowPassRadio.setToggleGroup(group);
        highPassRadio.setToggleGroup(group);
        bandPassRadio.setToggleGroup(group);
        bandStopRadio.setToggleGroup(group);
    }
}
