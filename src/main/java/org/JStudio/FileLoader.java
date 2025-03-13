package org.JStudio;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.util.LinkedList;

public class FileLoader {
    //keeping it simple for now

    private String curUser;

    FileLoader() {
        curUser = System.getProperty("user.name");
        System.out.println(curUser);
        try {
            load("C:\\Users\\" + curUser + "\\Music\\JStudio\\audio_Files");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void load(String path) throws Exception {
        System.out.println(path);

        File file = new File(path);
        if (file.exists() && file.isDirectory() && file.listFiles() != null) {
            int folderIntex = 0;
            for (File f : file.listFiles()) {
//                if (folderIntex > 0) return;
                if (f.isFile()) {
//                    Sections.get(folderIntex).addFile(new TestFile(f));
//                    TestFile testFile = new TestFile(f);
                    System.out.println("File added");
                } else if (f.isDirectory()) {
                    folderIntex++;
                    System.out.println("Num Folders: " + folderIntex);
                    load(f.getAbsolutePath());
                }
            }
        }
    }

    //remove from here
    public Node audioSection() {
        Image image = new Image("/down.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);

        VBox rootVBox = new VBox();
        VBox.setMargin(rootVBox, new Insets(0,0,10,0));

        Button expandSectionBtn = new Button("Section", imageView);
        expandSectionBtn.setPrefWidth(256 - (5 * 2));
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

}
