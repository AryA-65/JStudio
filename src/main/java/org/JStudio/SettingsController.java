package org.JStudio;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

public class SettingsController {
    private static boolean style; // false means light, true means dark
    private static Stage mainStage;

    private Scene helpScene;
    private ToggleGroup group;
    private static RadioButton selected;
    private static UIController controller;
    private static SettingsWindow window;
    private static String noteColor = "Blue";
    private static String waveColor = "Blue";
    @FXML
    private static MenuButton noteColourSelector;
    @FXML
    private static MenuButton waveColourSelector;
    @FXML
    private MenuItem blueNote, greenNote, redNote, blueWave, greenWave, redWave;
    
    @FXML
    private Button helpButton;

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
        
        // Note colouring
        blueNote.setOnAction(event -> {
            noteColor = "Blue";
        });
        greenNote.setOnAction(event -> {
            noteColor = "Green";
        });
        redNote.setOnAction(event -> {
            noteColor = "Red";
        });
        
        // Wave colouring
        blueWave.setOnAction(event -> {
            waveColor = "Blue";
        });
        greenWave.setOnAction(event -> {
            waveColor = "Green";
        });
        redWave.setOnAction(event -> {
            waveColor = "Red";
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
        return noteColor;
    }
    
    public static String getWaveColor(){
        return waveColor;
    }
}