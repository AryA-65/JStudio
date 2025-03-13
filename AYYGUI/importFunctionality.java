import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.File;

public class FileImportApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button importButton = new Button("Import File");

        importButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a File");

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac")
            );

            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                System.out.println("File selected: " + selectedFile.getAbsolutePath());
            } else {
                System.out.println("File selection canceled.");
            }
        });

        VBox root = new VBox(10, importButton);
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("JavaFX File Import");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}