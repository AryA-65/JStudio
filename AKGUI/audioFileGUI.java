import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import javafx.util.Duration;

public class HelloApplication extends Application {

    public void start(Stage primaryStage) {
        VBox root = new VBox();

        root.getChildren().addAll(addAudioFileUI(), addAudioFileUI());

        Scene scene = new Scene(root, 192, 256);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Audio File UI");
        primaryStage.show();
    }

    public Node addAudioFileUI() {
        VBox rootVBox = new VBox();
        rootVBox.setPrefHeight(Region.USE_COMPUTED_SIZE);

        Pane container = new Pane();
        container.setPrefSize(192, 64);
        container.setStyle("-fx-background-color: green;");

        Timeline expandTimeline = new Timeline(
                new KeyFrame(Duration.millis(150),
                        new KeyValue(container.prefHeightProperty(), 96, Interpolator.EASE_IN))
        );

        Timeline shrinkTimeline = new Timeline(
                new KeyFrame(Duration.millis(150),
                        new KeyValue(container.prefHeightProperty(), 64, Interpolator.EASE_OUT))
        );

        Canvas audioFileDataVis = new Canvas();
        audioFileDataVis.setWidth(192);
        audioFileDataVis.setHeight(64);
        audioFileDataVis.setStyle("-fx-background-color: transparent;");

        GraphicsContext gc = audioFileDataVis.getGraphicsContext2D();
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(0, 0, 192, 64);

        audioFileDataVis.setOnMouseEntered(e -> {
            expandTimeline.play();
//            System.out.println("entered" + container.getPrefHeight());
        });

        audioFileDataVis.setOnMouseExited(e -> {
            shrinkTimeline.play();
//            System.out.println("exited" + container.getPrefHeight());
        });

        HBox audioFileInfo = new HBox();

        Label audioFileName = new Label("test");
        Label audioFileExt = new Label(".wav");
        Label audioFileLength = new Label("2:38");

        audioFileInfo.getChildren().addAll(audioFileName, audioFileExt, audioFileLength);
        audioFileInfo.setStyle("-fx-background-color: transparent;");

        container.getChildren().addAll(audioFileInfo, audioFileDataVis);

        rootVBox.getChildren().add(container);
        VBox.setMargin(container, new Insets(0, 0, 10, 0));

        return rootVBox;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
