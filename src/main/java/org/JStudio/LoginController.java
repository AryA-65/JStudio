package org.JStudio;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.JStudio.Login.EncryptionAndDecryption;
import org.JStudio.Login.User;
import org.JStudio.Login.UserDataController;
import org.JStudio.Utils.AlertBox;

public class LoginController {
    private Stage rootStage;
    private Scene rootScene;

    private UserDataController userDataController;
    private EncryptionAndDecryption encryptionAndDecryption;

    @FXML
    private TextField userIdField, userPasswordField, key1Field, key2Field;

    @FXML
    private Button createAccount, enterBtn;
    @FXML
    private ImageView logo;
    private String userId, userPass;

    private int key1, key2;

    private User user;

    public LoginController() {
    }

    @FXML
    public void initialize() {
        logo.setImage(new Image("JS_ico.png"));
        userDataController = new UserDataController();
        csvSetUp();
        entryLimiters();
        createAccount.setOnAction(event -> {
            if (userId == null || userPass == null || userId.length() < 6 || userPass.length() < 6) {
                AlertBox.display("Credentials do not meet expectations","The username / password is too short ( minimum of 6 characters).");
                return;
            }
            encryptionAndDecryption = new EncryptionAndDecryption(userPass, key1, key2);
            user = new User(userId, encryptionAndDecryption.encryption(), key1, key2);
            userDataController.writeToFile(user);
            lunchMainApp();
        });

        enterBtn.setOnAction(event -> {
            userDataController.readFile();
            if (userDataController.isUserInFile(userId)) {
                User existingUser = userDataController.getUsers().get(userId);
                encryptionAndDecryption = new EncryptionAndDecryption(existingUser.getPassword(), existingUser.getKey1(), existingUser.getKey2());
                String decryptedPassword = encryptionAndDecryption.decryption();
                if (decryptedPassword.equalsIgnoreCase(userPass)) {
                    lunchMainApp();
                } else {
                    AlertBox.display("Error", "The entered user password does not correspond to this user.");
                }
            } else {
                AlertBox.display("Error", "The entered user does not have prior history.");
            }
        });
    }

    private void lunchMainApp() {
        try {
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("JStudio-UI.fxml"));
            Parent root = loader.load();

            UIController controller = loader.getController();

            Stage mainStage = new Stage();
            controller.setStage(mainStage);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
            controller.setScene(scene);
            mainStage.setScene(scene);
            mainStage.initStyle(StageStyle.TRANSPARENT);
            mainStage.setResizable(true);
            mainStage.show();

            SettingsController.setController(controller);

            rootStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void csvSetUp() {
        userDataController.createFile();
        userDataController.readFile();
    }


    private void entryLimiters() {
        userIdField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.contains(" ")) {
                userIdField.setText(newText.replace(" ", ""));
            }
            userId = newText;
        });

        userPasswordField.textProperty().addListener((obs, oldText, newText) -> {
            String cleanedText = newText.replaceAll("[^a-zA-Z]", "").replaceAll(" ", ""); // all text that is not a character

            userPasswordField.setText(cleanedText);
            userPass = newText;
        });

        // Allow only digits in key1 and key2 fields
        key1Field.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) { // if all chacacters are digits 0-9
                key1Field.setText(newText.replaceAll("[^\\d]", ""));
            }
            key1 = Integer.parseInt(newText);
        });

        key2Field.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                key2Field.setText(newText.replaceAll("[^\\d]", ""));
            }
            key2 = Integer.parseInt(newText);
        });
    }

    public void setRootStage(Stage stage) {
        rootStage = stage;
        rootStage.setResizable(false);
    }

    public void setRootScene(Scene rootScene) {
        this.rootScene = rootScene;
        rootScene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
    }
}
