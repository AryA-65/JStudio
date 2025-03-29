package org.JStudio;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.util.Duration;

import java.io.*;
import java.util.*;
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
    private FileLoader fileLoader;

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
    public void initialize() throws Exception {
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
        channel_rack.setSpacing(1);

        track_vbox.setSpacing(1);
        track_id_vbox.setSpacing(1);
        channel_rack.getChildren().add(0, creatingMasterChannel()); //test

        song_name.setText(song.getSongName());

        fileLoader = new FileLoader(tab_vbox);

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

//        ClipAndNoteLayoutManager cnlm = new ClipAndNoteLayoutManager();
//        cnlm.init(new Stage());

//        Knob testKnob = new Knob(0, );
//        testKnob.setAngle(180);

        grid_root.setOnKeyPressed(e -> {
            pressedKeys.add(e.getCode());
            System.out.println("pressedKey: " + e.getCode());
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
            if (pressedKeys.contains(KeyCode.CONTROL) && pressedKeys.contains(KeyCode.Q)) {
                //get all the children in the track vbox and increase their width to the bpm * n (n being the increment -> default = 1, can increase in fractions or ints)
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

    //test
    public void getWavData(File filePath) {
        if (!filePath.getName().endsWith(".wav")) {return;}
        System.out.println(filePath.getName());
        try (FileInputStream fis = new FileInputStream(filePath);
             DataInputStream dis = new DataInputStream(fis)) {

            // Read "RIFF" Chunk Descriptor
            byte[] chunkID = new byte[4];
            dis.read(chunkID); // Should be "RIFF"
            String chunkIDStr = new String(chunkID, "UTF-8");

            int chunkSize = Integer.reverseBytes(dis.readInt()); // File size - 8

            byte[] format = new byte[4];
            dis.read(format); // Should be "WAVE"
            String formatStr = new String(format, "UTF-8");

            // Read "fmt " Subchunk
            byte[] subchunk1ID = new byte[4];
            dis.read(subchunk1ID); // Should be "fmt "
            String subchunk1IDStr = new String(subchunk1ID, "UTF-8");

            int subchunk1Size = Integer.reverseBytes(dis.readInt()); // 16 for PCM
            short audioFormat = Short.reverseBytes(dis.readShort()); // 1 = PCM, other = compressed
            short numChannels = Short.reverseBytes(dis.readShort()); // 1 = Mono, 2 = Stereo
            int sampleRate = Integer.reverseBytes(dis.readInt()); // e.g., 44100 Hz
            int byteRate = Integer.reverseBytes(dis.readInt()); // SampleRate * NumChannels * BitsPerSample/8
            short blockAlign = Short.reverseBytes(dis.readShort()); // NumChannels * BitsPerSample/8
            short bitsPerSample = Short.reverseBytes(dis.readShort()); // e.g., 16 bits

            // Read "data" Subchunk
            byte[] subchunk2ID = new byte[4];
            dis.read(subchunk2ID); // Should be "data"
            String subchunk2IDStr = new String(subchunk2ID, "UTF-8");

            int subchunk2Size = Integer.reverseBytes(dis.readInt()); // Data size in bytes

            // Print the extracted information
            System.out.println("Chunk ID: " + chunkIDStr);
            System.out.println("Chunk Size: " + chunkSize);
            System.out.println("Format: " + formatStr);
            System.out.println("Subchunk1 ID: " + subchunk1IDStr);
            System.out.println("Subchunk1 Size: " + subchunk1Size);
            System.out.println("Audio Format: " + (audioFormat == 1 ? "PCM" : "Compressed"));
            System.out.println("Number of Channels: " + numChannels);
            System.out.println("Sample Rate: " + sampleRate);
            System.out.println("Byte Rate: " + byteRate);
            System.out.println("Block Align: " + blockAlign);
            System.out.println("Bits Per Sample: " + bitsPerSample);
            System.out.println("Subchunk2 ID: " + subchunk2IDStr);
            System.out.println("Subchunk2 Size: " + subchunk2Size);

        } catch (IOException e) {
            System.err.println("Error reading WAV file: " + e.getMessage());
        }
    }
}
