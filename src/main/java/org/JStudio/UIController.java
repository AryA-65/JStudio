package org.JStudio;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/*
MAKE A INTERFACE CONTROLLER CLASS TO IMPLEMENT ALL DIFFERENT UIs AND THEIR RESPECTIVE CONTROLLERS
 */

public class UIController {

    @FXML
    private GridPane grid_root;
    @FXML
    private ScrollPane track_id_scrollpane;
    @FXML
    private HBox tracks_channels;
    @FXML
    private ScrollPane beat_scrollpane;
    @FXML
    private Canvas beat_canvas;
    @FXML
    private ScrollPane tracks_scrollpane;
    @FXML
    private VBox track_id_vbox;
    @FXML
    private VBox track_vbox;
    @FXML
    private Circle record_control;
    @FXML
    private HBox info_panel;
    @FXML
    private HBox channel_rack;
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

    //testing params
    private double temporaryBPM = 120;
    private String curUser;
    private byte audioChannels = 16;
    private fileChooserController fileLoaderController;
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    //part of test params, but this should its own thing
    private Song song = new Song("test");

    //stage controller functions
    public void setStage(Stage stage) {
        rootStage = stage;
    }
    public Stage getStage() {return rootStage;}

    @FXML
    public void initialize() throws IOException {
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

        track_vbox.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.isControlDown()) {
                double delta = e.getDeltaY() * 0.005;
                for (Node track : track_vbox.getChildren()) {
                    track.setScaleX(track.getScaleX() + delta);
                }
//                track_vbox.setPrefWidth(track_vbox.getBoundsInParent().getWidth());
                e.consume();
            }
            if (e.isShiftDown()) {
                double delta = e.getDeltaX() * 0.005;
//                System.out.println("Shift pressed");
                //tempo fix (replace 1533 with something else)
                tracks_scrollpane.setHvalue(tracks_scrollpane.getHvalue() - delta);
                e.consume();
            }
        });

        tracks_scrollpane.vvalueProperty().bindBidirectional(track_id_scrollpane.vvalueProperty());

        tracks_scrollpane.hvalueProperty().bindBidirectional(beat_scrollpane.hvalueProperty());

        for (Track track : song.getTracks()) {
            track_vbox.getChildren().add(track.addTrack());
            track_id_vbox.getChildren().add(track.addTrackID());
            channel_rack.getChildren().add(track.createChannel((byte) (track.getId() - 1)));
            System.out.println(track.getId());
        }

        //Test functions
        curUser = System.getProperty("user.name");
//        System.out.println(curUser);
        try {
            loadFolders("C:\\Users\\" + curUser + "\\Music\\JStudio\\audio_Files");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        channel_rack.setSpacing(1);

        track_vbox.setSpacing(1);
        track_id_vbox.setSpacing(1);
        channel_rack.getChildren().add(0, creatingMasterChannel()); //test

//        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fileLoader-UI.fxml"));
//        Parent root = loader.load();

//        fileLoaderController = loader.getController();
//
//        Stage fileLoaderStage = new Stage();
//        Scene fileLoaderScene = new Scene(root);
//
//        fileLoaderController.setStage(fileLoaderStage);
//
//        fileLoaderStage.setScene(fileLoaderScene);
//        fileLoaderStage.initStyle(StageStyle.TRANSPARENT);
//        fileLoaderStage.setResizable(false);
//        fileLoaderStage.show();

        addTimeLine();

        grid_root.setOnKeyPressed(e -> {
            pressedKeys.add(e.getCode());
            if (pressedKeys.contains(KeyCode.CONTROL) && pressedKeys.contains(KeyCode.A)) {
                Track addedTrack = new Track("");
                song.addTrack(addedTrack);
                track_vbox.getChildren().add(addedTrack.addTrack());
                track_id_vbox.getChildren().add(addedTrack.addTrackID());
                System.out.println("Track added");
            }
            if (pressedKeys.contains(KeyCode.CONTROL) && pressedKeys.contains(KeyCode.D)) {
                song.removeTrack();
                track_vbox.getChildren().remove(track_vbox.getChildren().size() - 1);
                track_id_vbox.getChildren().remove(track_id_vbox.getChildren().size() - 1);
                System.out.println("Track removed");
            }
        });

        grid_root.setOnKeyReleased(e -> {
            pressedKeys.remove(e.getCode());
        });

        tracks_channels.setStyle("-fx-background-color: black;");
        beat_scrollpane.setStyle("-fx-background-color: transparent;");
        beat_canvas.setStyle("-fx-background-color: transparent;");
        track_vbox.setStyle("-fx-background-color: transparent;");
        track_id_vbox.setStyle("-fx-background-color: transparent;");
        tracks_scrollpane.setStyle("-fx-background-color: transparent;");
        track_id_scrollpane.setStyle("-fx-background-color: transparent;");
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

        rootVBox.setOnDragDetected(event -> {
            Dragboard db = rootVBox.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putFiles(Collections.singletonList(file));
            db.setContent(content);
            event.consume();
        });

        return rootVBox;
    }

    public Node creatingMasterChannel() {
        AtomicBoolean clicked = new AtomicBoolean(false);

        HBox masterContainer = new HBox();
        masterContainer.setPrefHeight(256);
        masterContainer.setPrefWidth(64);
        masterContainer.setStyle("-fx-background-color:  #D9D9D9; -fx-background-radius: 5px;");
        masterContainer.setAlignment(Pos.TOP_CENTER);
        HBox.setMargin(masterContainer, new Insets(0, 5, 0, 0));

        VBox masterVisContainer = new VBox();
        masterVisContainer.setPrefHeight(256);
        masterVisContainer.setPrefWidth(32);
        masterVisContainer.setAlignment(Pos.TOP_CENTER);

        Label masterLabel = new Label("Out");
        masterLabel.setFont(new Font("Inter Regular", 8));
        VBox.setMargin(masterLabel, new Insets(2, 0, 2, 0));

        StackPane masterVis = new StackPane();
        masterVis.setPrefSize(18,243);
        masterVis.setStyle("-fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 5px");
        VBox.setMargin(masterVis, new Insets(4,5,4,5));

        Canvas masterChannelVis = new Canvas();
        masterChannelVis.setHeight(40);
        masterChannelVis.setWidth(16);

        //testing canvas, remove later
        GraphicsContext gc = masterChannelVis.getGraphicsContext2D();
        gc.setFill(Color.RED);
        gc.fillRect(0, 0, masterChannelVis.getWidth(), masterChannelVis.getHeight());

        VBox masterChannelContainer = new VBox();
        masterChannelContainer.setPrefHeight(256);
        masterChannelContainer.setPrefWidth(32);
        masterChannelContainer.setAlignment(Pos.TOP_CENTER);

        Label channelID = new Label("Master");
        channelID.setFont(new Font("Inter Regular", 8));
        VBox.setMargin(channelID, new Insets(2, 0, 2, 0));

        Pane channelContainer = new Pane();
        channelContainer.setPrefHeight(243);
        channelContainer.setPrefWidth(32);
        channelContainer.setStyle("-fx-background-color: #404040; -fx-background-radius: 5px");

        Pane channelVisContainer = new Pane();
        channelVisContainer.setPrefHeight(64);
        channelVisContainer.setPrefWidth(32);
        channelVisContainer.setStyle("-fx-background-color: #808080; -fx-background-radius: 5px");

        StackPane visContainer = new StackPane();
        visContainer.setPrefSize(18,42);
        visContainer.setLayoutX(7);
        visContainer.setLayoutY(4);
        visContainer.setStyle("-fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 5px");

        Canvas channelVis = new Canvas();
        channelVis.setHeight(40);
        channelVis.setWidth(16);

        Pane activeBtn = new Pane();
        activeBtn.setPrefHeight(8);
        activeBtn.setPrefWidth(8);
        activeBtn.setLayoutX(12);
        activeBtn.setLayoutY(224);
        activeBtn.toFront();
        activeBtn.getStyleClass().add("active");
        activeBtn.setStyle("-fx-background-color: #00FD11; -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");

        activeBtn.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                clicked.set(true);
            }
        });

        activeBtn.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY && activeBtn.contains(e.getX(), e.getY()) && clicked.get()) {
                clicked.set(false);
                if (activeBtn.getStyleClass().contains("active")) {
                    activeBtn.getStyleClass().remove("active");
                    activeBtn.getStyleClass().add("disabled");
                    activeBtn.setStyle("-fx-background-color: rgb(0,90,6); -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");
                } else {
                    activeBtn.getStyleClass().remove("disabled");
                    activeBtn.getStyleClass().add("active");
                    activeBtn.setStyle("-fx-background-color: #00FD11; -fx-border-width: 1px; -fx-border-color: black; -fx-border-radius: 4px; -fx-background-radius: 4px");
                }
//                        System.out.println("Registered");
            } else clicked.set(false);

//                    System.out.println("Released");
        });

        Slider channelAmp = new Slider(0,100,100);
        channelAmp.setOrientation(Orientation.VERTICAL);
        channelAmp.setPrefHeight(96);
        channelAmp.setLayoutY(96);
        channelAmp.setLayoutX(9);

        visContainer.getChildren().add(channelVis);

        masterVis.getChildren().add(masterChannelVis);

        channelVisContainer.getChildren().add(visContainer);

        channelContainer.getChildren().addAll(channelVisContainer, channelAmp, activeBtn);

        masterVisContainer.getChildren().addAll(masterLabel, masterVis);

        masterChannelContainer.getChildren().addAll(channelID, channelContainer);

        masterContainer.getChildren().addAll(masterVisContainer, masterChannelContainer);

        return masterContainer;
    }

    private void addTimeLine() {
        GraphicsContext gc = beat_canvas.getGraphicsContext2D();

        gc.setFill(Color.GREY);
        gc.fillRoundRect(0,0,beat_canvas.getWidth(), beat_canvas.getHeight(), 10, 10);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Inter Regular", 12));

        for (int i = 0; i < beat_canvas.getWidth(); i++) {
            if (i % 32 == 0) {
                byte mult = (byte) (i / 32);
                gc.fillText(String.valueOf(mult), i + 2, beat_canvas.getHeight() - 4);
            }
        }
    }

    //idk if this works, but still its part of the testing functions and shit
    private void handleDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event, GraphicsContext gc) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            List<File> files = db.getFiles();

            double dropX = event.getX();

            gc.setFill(Color.BLACK);
            gc.fillRoundRect(dropX, 0, 128, 32, 10, 10);

            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }
}
