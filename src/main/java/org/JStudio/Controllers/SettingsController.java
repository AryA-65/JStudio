package org.JStudio.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.JStudio.Views.SettingsWindow;

import java.io.IOException;

/**
 * Controller Class for the settings page
 */
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

    /**
     * Method that initializes the UI elements
     */
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
        
        // Wave and note coloring
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
        
        // Wave coloring
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

    /**
     * Gets the current UI style.
     *
     * @return The current style, true if it is dark mode, false otherwise.
     */
    public static boolean getStyle() {
        return style;
    }

    /**
     * Sets the UI style.
     *
     * @param style A boolean indicating whether to set the style to dark mode (true) or light mode (false).
     */
    public static void setStyle(boolean style) {
        SettingsController.style = style;
    }

    /**
     * Sets the main UI controller for the application.
     *
     * @param controller The UIController object that manages the main UI.
     */
    public static void setController(UIController controller) {
        SettingsController.controller = controller;
    }

    /**
     * Sets the settings window for the application.
     *
     * @param window The SettingsWindow object that handles the settings UI.
     */
    public static void setWindow(SettingsWindow window) {
        SettingsController.window = window;
    }

    /**
     * Updates the UI style of the main application interface.
     * Calls the updateStyle method of the main UI controller.
     */
    public void updateMainUIStyle() {
        SettingsController.controller.updateStyle();
    }

    /**
     * Updates the UI style of the settings interface.
     * Calls the updateStyle method of the settings window.
     */
    public void updateSettingsUIStyle() {
        SettingsController.window.updateStyle();
    }

    /**
     * Sets the main stage for the application.
     *
     * @param stage The primary JavaFX Stage to display the application's main window.
     */
    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    /**
     * Gets the color used for notes in the UI.
     *
     * @return A string representing the color of notes.
     */
    public static String getNoteColor() {
        return noteColour;
    }

    /**
     * Gets the color used for the waveform in the UI.
     *
     * @return A string representing the color of the waveform.
     */
    public static String getWaveColor() {
        return waveColour;
    }

    /**
     * Checks if the application is in testing mode.
     *
     * @return true if the application is in testing mode, false otherwise.
     */
    public static boolean isTesting() {
        return isTesting;
    }

    /**
     * Sets the testing mode flag for the application.
     *
     * @param isTesting A boolean indicating whether the application should enter testing mode.
     */
    public static void setTesting(boolean isTesting) {
        SettingsController.isTesting = isTesting;
    }
}
