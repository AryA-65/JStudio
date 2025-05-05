package org.JStudio;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.JStudio.Plugins.Views.SettingsWindow;

import java.io.IOException;

public class SettingsController {
    private static boolean style; // false means light, true means dark
    private static Stage mainStage;

    private Scene helpScene;
    private ToggleGroup group;
    private static RadioButton selected;
    private static UIController controller;
    private static SettingsWindow window;
    private static String noteColour = "Blue";
    private static String waveColour = "Blue";
    @FXML
    private static MenuButton noteColourSelector;
    @FXML
    private static MenuButton waveColourSelector;
    @FXML
    private MenuItem blueNote, greenNote, redNote, blueWave, greenWave, redWave;
    @FXML
    private Label noteColourLabel, waveColourLabel;
    @FXML
    private Button helpButton;

    @FXML
    private CheckBox testingBox;

    public static boolean isTesting = false;

    @FXML
    RadioButton lightRadio, darkRadio;

    public void initialize() {
        // Style mode toggling
        group = new ToggleGroup();
        lightRadio.setToggleGroup(group);
        darkRadio.setToggleGroup(group);

        if (selected != null) {
            if (!style) {
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
        
        // Wave and note colouring
        switch (noteColour) {
            case "Blue":
                noteColourLabel.setText("Selected: Blue");
                break;
            case "Green":
                noteColourLabel.setText("Selected: Green");
                break;
            case "Red":
                noteColourLabel.setText("Selected: Red");
                break;
        }
        
        switch (waveColour) {
            case "Blue":
                waveColourLabel.setText("Selected: Blue");
                break;
            case "Green":
                waveColourLabel.setText("Selected: Green");
                break;
            case "Red":
                waveColourLabel.setText("Selected: Red");
                break;
        }    
        
        blueNote.setOnAction(event -> {
            noteColour = "Blue";
            noteColourLabel.setText("Selected: Blue");
        });
        greenNote.setOnAction(event -> {
            noteColour = "Green";
            noteColourLabel.setText("Selected: Green");
        });
        redNote.setOnAction(event -> {
            noteColour = "Red";
            noteColourLabel.setText("Selected: Red");
        });
        
        // Wave colouring
        blueWave.setOnAction(event -> {
            waveColour = "Blue";
            waveColourLabel.setText("Selected: Blue");
        });
        greenWave.setOnAction(event -> {
            waveColour = "Green";
            waveColourLabel.setText("Selected: Green");
        });
        redWave.setOnAction(event -> {
            waveColour = "Red";
            waveColourLabel.setText("Selected: Red");
        });
        
        // Help menu
        helpButton.setOnMouseClicked(event -> {
            helpButton.setDisable(true);
            Stage childStage = new Stage();
            childStage.initOwner(mainStage);

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("tools_descriptions.fxml"));
                fxmlLoader.setController(new HelpController());

                Parent root = fxmlLoader.load();
                helpScene = new Scene(root, 640, 480);
                if (SettingsController.getStyle()) {
                    helpScene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
                } else {
                    helpScene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
                }
                childStage.setScene(helpScene);
                childStage.setTitle("Help Menu");
                childStage.sizeToScene();
                childStage.show();

                childStage.setOnHidden(e -> helpButton.setDisable(false));
            } catch (IOException e) {
                helpButton.setDisable(false);
            }
        });

        testingBox.setSelected(isTesting);


        testingBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            isTesting = isNowSelected;
        });
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

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    public static String getNoteColor() {
        return noteColour;
    }
    
    public static String getWaveColor(){
        return waveColour;
    }

    public static boolean isTesting() {
        return isTesting;
    }

    public static void setTesting(boolean isTesting) {
        SettingsController.isTesting = isTesting;
    }
}
