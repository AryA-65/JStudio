package org.JStudio;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static java.lang.reflect.Array.getInt;

public class UIController {

    @FXML
    private Circle record_control;
    @FXML
    private HBox info_panel;
    @FXML
    private Label playback_pos;
    @FXML
    private TextField bpm_control;
    @FXML
    private TextField song_name;
    @FXML
    private ImageView open_song_btn;
    @FXML
    private ImageView save_song_btn;
    @FXML
    private ImageView export_song_btn;
    @FXML
    private ImageView minim_btn;
    @FXML
    private ImageView close_btn;
    @FXML
    private ScrollPane tab_scroll;
    @FXML
    private VBox tab_vbox;
    @FXML
    private Stage rootStage;

    private double xOffset = 0, yOffset = 0, startX = 0;
    private double temporaryBPM = 120;
    private String curUser;

    public void setStage(Stage stage) {
        rootStage = stage;
    }

    @FXML
    public void initialize() {
        //initializing nodes (loading images and other stuff)
        open_song_btn.setImage(new Image("/copy-document.png"));
        save_song_btn.setImage(new Image("/save.png"));
        export_song_btn.setImage(new Image("/import-export.png"));
        close_btn.setImage(new Image("/close.png"));
        minim_btn.setImage(new Image("/minimize.png"));

        //adding missing nodes
//        VBox testBox = new VBox();
//        testBox.setId("testBox");
//        tab_scroll.getChildrenUnmodifiable().add(testBox);

        //adding different code for nodes
        tab_vbox.setSpacing(15);
        tab_vbox.setPrefHeight(Region.USE_COMPUTED_SIZE);

        //initializing events for nodes
        info_panel.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            xOffset = rootStage.getX() - event.getScreenX();
            yOffset = rootStage.getY() - event.getScreenY();
        });

        info_panel.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            rootStage.setX(event.getScreenX() + xOffset);
            rootStage.setY(event.getScreenY() + yOffset);
        });

        record_control.setOnMouseClicked(event -> {
            System.out.println("Pressed record_control");
        });

        bpm_control.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                startX = event.getScreenX();
            }
        });

        bpm_control.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                double currentX = event.getScreenX();
                double deltaX = currentX - startX;

                temporaryBPM += Math.min(999, deltaX);
                temporaryBPM = Math.max(temporaryBPM, 0);
                bpm_control.setText(String.valueOf(temporaryBPM));

                startX = currentX; // Reset for smooth adjustment
            }
        });

        close_btn.setOnMouseClicked(event -> {
            rootStage.close();
        });

        curUser = System.getProperty("user.name");
//        System.out.println(curUser);
        try {
            loadFolders("C:\\Users\\" + curUser + "\\Music\\JStudio\\audio_Files");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //temporary function (this shit needs to be optimized and put into another class)
    public void loadFolders(String path) throws Exception {
//        System.out.println(path);

        File file = new File(path);
        if (file.exists() && file.isDirectory() && file.listFiles() != null) {
            int folderIntex = 0;
            for (File f : file.listFiles()) {
                if (f.isDirectory()) {
                    folderIntex++;
//                    System.out.println("Num Folders: " + folderIntex);
                    Node section = audioSection(f);
                    tab_vbox.getChildren().add(section);
                }
            }
        }
    }

    public Node audioSection(File f) {
        Image image = new Image("/down.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);

        VBox rootVBox = new VBox();
        rootVBox.setId(f.getName());
//        VBox.setMargin(rootVBox, new Insets(0,0,0,0));

        Button expandSectionBtn = new Button(f.getName(), imageView);
        expandSectionBtn.setPrefWidth(256 - (5 * 2));
        expandSectionBtn.setAlignment(Pos.TOP_LEFT);
        VBox.setMargin(expandSectionBtn, new Insets(0, 5, 10, 5));

        VBox fileSectionList = new VBox();
        fileSectionList.setAlignment(Pos.TOP_LEFT);

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
                rotateTransition.play();
                shrinkTimeline.play();
                shrinkTimeline.setOnFinished(event -> fileSectionList.setVisible(false));
            } else {
                rotateTransition.play();
                fileSectionList.setVisible(true);
                expandTimeline.play();
            }
        });

        System.out.println(f.getName());
        for (File file : f.listFiles()) {
            if (file.exists() && file.isFile()) {
//                System.out.println(file.getName());
                fileSectionList.getChildren().add(addAudioFileUI(file));
            }
        }
        fileSectionList.setSpacing(10);

        rootVBox.getChildren().addAll(expandSectionBtn, fileSectionList);

        return rootVBox;
    }

    public Node addAudioFileUI(File file) {
        VBox rootVBox = new VBox();

        String fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
        String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);

        rootVBox.setId(fileName);
        rootVBox.setPrefHeight(Region.USE_COMPUTED_SIZE);

        Pane container = new Pane();
        container.setPrefSize(234, 64);
        container.setStyle("-fx-background-color: #808080; -fx-background-radius: 5px");

        Label audioFileName = new Label(fileName);
        audioFileName.setMaxWidth(128);
        audioFileName.setTextOverrun(OverrunStyle.ELLIPSIS);
        Label audioFileExt = new Label(extension);
        audioFileExt.setMaxWidth(32);
        audioFileExt.setTextOverrun(OverrunStyle.ELLIPSIS);
        Label audioFileLength = new Label("0:00");
        audioFileLength.setMaxWidth(32);
        audioFileLength.setTextOverrun(OverrunStyle.ELLIPSIS);

        Canvas audioFileDataVis = new Canvas();
        audioFileDataVis.setWidth(234);
        audioFileDataVis.setHeight(64);
        audioFileDataVis.setStyle("-fx-background-color: transparent;");

        GraphicsContext gc = audioFileDataVis.getGraphicsContext2D();
        gc.setFill(Color.web("D9D9D9"));
        gc.fillRoundRect(0, 0, audioFileDataVis.getWidth(), audioFileDataVis.getHeight(), 10, 10);

        HBox audioFileInfo = new HBox();
        audioFileInfo.setSpacing(5);
        audioFileInfo.setLayoutY(container.getPrefHeight() - 18);
        audioFileInfo.setLayoutX(2);
        container.prefHeightProperty().addListener((observable, oldValue, newValue) -> {
            audioFileInfo.setLayoutY(container.getPrefHeight() - 18);
        });

        Timeline expandTimeline = new Timeline(
                new KeyFrame(Duration.millis(150),
                        new KeyValue(container.prefHeightProperty(), 80, Interpolator.EASE_IN))
        );

        Timeline shrinkTimeline = new Timeline(
                new KeyFrame(Duration.millis(150),
                        new KeyValue(container.prefHeightProperty(), 64, Interpolator.EASE_OUT))
        );

        audioFileDataVis.setOnMouseEntered(e -> {
            expandTimeline.play();
//            System.out.println("entered" + container.getPrefHeight());
        });

        audioFileDataVis.setOnMouseExited(e -> {
            shrinkTimeline.play();
//            System.out.println("exited" + container.getPrefHeight());
        });

        audioFileInfo.getChildren().addAll(audioFileName, audioFileExt, audioFileLength);
        audioFileInfo.setStyle("-fx-background-color: transparent;");

        container.getChildren().addAll(audioFileInfo, audioFileDataVis);

        rootVBox.getChildren().add(container);

        return rootVBox;
    }

    public VBox loadFile() {



        return null;
    }
}
