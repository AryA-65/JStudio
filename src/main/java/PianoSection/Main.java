package PianoSection;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(ClassLoader.getSystemResource("other_fxmls/Notes.fxml"));
        Scene scene = new Scene(root,1500,900);
        stage.setScene(scene);
        stage.show();
    }
    
}
