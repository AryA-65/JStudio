import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import javafx.util.Duration;

public class HelloApplication extends Application {
    int screenWidth = 192;

    public void start(Stage primaryStage) {
        VBox root = new VBox();

//        root.getChildren().addAll(addAudioFileUI(), addAudioFileUI());
        root.getChildren().addAll(audioSection(), audioSection());

        Scene scene = new Scene(root, screenWidth, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Audio File UI");
        primaryStage.show();
    }

    public Node audioSection() {
        Image image = new Image("/down.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);

        VBox rootVBox = new VBox();
        VBox.setMargin(rootVBox, new Insets(0,0,10,0));

        Button expandSectionBtn = new Button("Section", imageView);
        expandSectionBtn.setPrefWidth(screenWidth - (5 * 2));
        expandSectionBtn.setAlignment(Pos.TOP_LEFT);
        VBox.setMargin(expandSectionBtn, new Insets(0, 5, 10, 5));

        VBox fileSectionList = new VBox();
//        fileSectionList.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        fileSectionList.setAlignment(Pos.TOP_LEFT);

//        fileSectionList.setPrefHeight(0);
//        fileSectionList.setVisible(false);
        VBox.setMargin(fileSectionList, new Insets(0, 10, 0, 10));

        Timeline expandTimeline = new Timeline(
                new KeyFrame(Duration.millis(150), new KeyValue(fileSectionList.prefHeightProperty(), Region.USE_COMPUTED_SIZE, Interpolator.EASE_IN))
        );

        Timeline shrinkTimeline = new Timeline(
                new KeyFrame(Duration.millis(150), new KeyValue(fileSectionList.prefHeightProperty(), 0, Interpolator.EASE_OUT))
        );

        RotateTransition rotateTransition = new RotateTransition(Duration.millis(150), imageView);
        rotateTransition.setByAngle(-180);

        expandSectionBtn.setOnAction(e -> {
            if (fileSectionList.isVisible()) {
//                imageView.setRotate(imageView.getRotate() - 180);
                rotateTransition.play();
                shrinkTimeline.play();
                shrinkTimeline.setOnFinished(event -> fileSectionList.setVisible(false));
            } else {
//                imageView.setRotate(imageView.getRotate() + 180);
                rotateTransition.play();
                fileSectionList.setVisible(true);
                expandTimeline.play();
            }
        });

        fileSectionList.getChildren().addAll(addAudioFileUI(), addAudioFileUI());
        fileSectionList.setSpacing(10);

        rootVBox.getChildren().addAll(expandSectionBtn, fileSectionList);

        return rootVBox;
    }

    public Node addAudioFileUI() {
        VBox rootVBox = new VBox();
        rootVBox.setPrefHeight(Region.USE_COMPUTED_SIZE);

        Pane container = new Pane();
        container.setPrefSize(172, 64);
        container.setStyle("-fx-background-color: green; -fx-background-radius: 5px");

        Timeline expandTimeline = new Timeline(
                new KeyFrame(Duration.millis(150),
                        new KeyValue(container.prefHeightProperty(), 80, Interpolator.EASE_IN))
        );

        Timeline shrinkTimeline = new Timeline(
                new KeyFrame(Duration.millis(150),
                        new KeyValue(container.prefHeightProperty(), 64, Interpolator.EASE_OUT))
        );

        Canvas audioFileDataVis = new Canvas();
        audioFileDataVis.setWidth(172);
        audioFileDataVis.setHeight(64);
        audioFileDataVis.setStyle("-fx-background-color: transparent;");

        GraphicsContext gc = audioFileDataVis.getGraphicsContext2D();
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRoundRect(0, 0, audioFileDataVis.getWidth(), audioFileDataVis.getHeight(), 10, 10);

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

        return rootVBox;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
