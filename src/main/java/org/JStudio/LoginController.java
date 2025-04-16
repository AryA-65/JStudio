package org.JStudio;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import login.EncryptionAndDecryption;
import login.User;
import login.UserDataController;
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

    private String userId, userPass;

    private int key1, key2;

    private boolean entry = false;

    private User user;

    private boolean isCorrect = false;

    public LoginController() {
    }

    @FXML
    public void initialize() {
        userDataController = new UserDataController();
        csvSetUp();
        entryLimiters();
        createAccount.setOnAction(event -> {
            if (isCorrect) {
                AlertBox.display("Credentials do not meet expectations","The username / password is too short ( minimum of 6 characters).");
            }
            encryptionAndDecryption = new EncryptionAndDecryption(userPass, key1, key2);
            user = new User(userId, encryptionAndDecryption.encryption(), key1, key2);
            userDataController.writeToFile(user);
            entry = true;
            lunchMainApp();
        });

        enterBtn.setOnAction(event -> {
            userDataController.readFile();
            if (userDataController.isUserInFile(userId)) {
                User existingUser = userDataController.getUsers().get(userId);
                encryptionAndDecryption = new EncryptionAndDecryption(existingUser.getPassword(), existingUser.getKey1(), existingUser.getKey2());
                String decryptedPassword = encryptionAndDecryption.decryption();
                if (decryptedPassword.equals(userPass)) {
                    entry = true;
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
            controller.setScreenSize();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());

            mainStage.setScene(scene);
            mainStage.initStyle(StageStyle.TRANSPARENT);
            mainStage.setResizable(true);
            mainStage.show();

            controller.setSplitRatio();

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
                System.out.println(userIdField);
            } else if (newText.length() <= 6) {
                isCorrect = true;
            }
            userId = newText;
        });

        userPasswordField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.contains(" ")) {
                userPasswordField.setText(newText.replace(" ", ""));
                System.out.println(userPasswordField);
            } else if (newText.length() <= 6) {
//                AlertBox.display("Password not meet expectations","The password is too short. Please choose one that is 6 characters minimum.");
                isCorrect = true;
            }
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
    }

    public Stage getRootStage() {return rootStage;}

    public Scene getRootScene() {return rootScene;}

    public void setRootScene(Scene rootScene) {
        this.rootScene = rootScene;
    }
}
