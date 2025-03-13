package org.JStudio;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LoadingScreen extends VBox {
    //add a variable for the OS
    private String settingsFolder;

    LoadingScreen() {
        Image image = new Image("/app_ico.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);

        Label label = new Label("Loading...");
        Text text = new Text();

        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(0);

        getChildren().addAll(imageView, label, progressBar, text);

    }

    public String getCurrentUser() {

        return "";
    }

}
