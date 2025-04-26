package org.JStudio;

import PianoManualSynth.SynthPiano;
import PianoSection.Piano;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.JStudio.Core.Song;
import org.JStudio.Core.Track;
import org.JStudio.Plugins.Views.*;
import org.JStudio.UI.TrackUI;
import org.JStudio.Utils.AudioVisualizer;
import org.JStudio.Utils.SystemMonitor;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.Scene;
import org.JStudio.Plugins.MainEqualizer;

/*
MAKE A INTERFACE CONTROLLER CLASS TO IMPLEMENT ALL DIFFERENT UIs AND THEIR RESPECTIVE CONTROLLERS
 */

public class UIController {
    private Scene scene;
    @FXML
    private ImageView snap_btn;
    @FXML
    private Button reverbBtn;
    @FXML
    private Button flangerBtn;
    @FXML
    private Button chorusBtn;
    @FXML
    private Button echoBtn;
    @FXML
    private Button phaserBtn;
    @FXML
    private Button equalizerBtn;
    @FXML
    private Button pianoBtn;
    @FXML
    private Button synthPianoBtn;

    @FXML
    private Button stereoBtn, butterworthBtn, basicFilterBtn, amplitudeBtn, synthesizerBtn;
    
    @FXML
    private TextField search_samples;
    @FXML
    private Canvas audio_vis_top;
    @FXML
    private Canvas amp_audio_top;
    @FXML
    private Canvas pc_stats;
    @FXML
    private ImageView settings_btn;
    //place each node in its own group
    @FXML
    private ImageView metronome_control;
    @FXML
    private SplitPane splitpane;
    @FXML
    private ImageView maxim_btn;
    @FXML
    private GridPane grid_root;
    @FXML
    private ScrollPane track_id_scrollpane;
    @FXML
    private HBox tracks_channels;
    @FXML
    private ScrollPane timeline_scrollpane;
    @FXML
    private Canvas timeline_canvas;
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

    private SystemMonitor sm;

    private double xOffset = 0, yOffset = 0, startX = 0, xResize = 0, yResize = 0, initialWidth, initialHeight;
    private boolean resizing = false;
    private FileLoader fileLoader;

    //testing params
    private String curUser;
    private FileChooserController fileLoaderController;
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    //part of test params, but this should its own thing
    private Song song = new Song("test");

    //stage controller functions
    public void setStage(Stage stage) {
        rootStage = stage;
        rootStage.setMaximized(true);
    }

    public Stage getStage() {return rootStage;}

    /**
     * Sets the main scene
     * @param scene 
     */
    public void setScene(Scene scene)  {
        this.scene = scene;
    }
    
    /**
     * Gets the main scene
     * @return the main scene
     */
    public Scene getScene() {
        return scene;
    }
    
    /**
     * Updates style of main scene (light or dark)
     */
    public void updateStyle() {
        scene.getStylesheets().clear();
        if (SettingsController.getStyle()) {
            scene.getStylesheets().add(ClassLoader.getSystemResource("darkmode.css").toExternalForm());
        } else {
            scene.getStylesheets().add(ClassLoader.getSystemResource("styles.css").toExternalForm());
        }
    }
    
    @FXML
    public void initialize() throws Exception {
        reverbBtn.setOnAction(e -> {
            ReverbStage reverb = new ReverbStage();
            reverb.show();
        });
        
        flangerBtn.setOnAction(e -> {
            FlangerStage flanger = new FlangerStage();
            flanger.show();
        });
        
        chorusBtn.setOnAction(e -> {
            ChorusStage chorus = new ChorusStage();
            chorus.show();
        });
        
        echoBtn.setOnAction(e -> {
            EchoStage echo = new EchoStage();
            echo.show();
        });
        
        phaserBtn.setOnAction(e -> {
            PhaserStage phaser = new PhaserStage();
            phaser.show();
        });
        
        equalizerBtn.setOnAction(e -> {
            MainEqualizer mainEQ = new MainEqualizer();
            mainEQ.openEQ();
        });
        
        equalizerBtn.setOnAction(e -> {
            MainEqualizer mainEQ = new MainEqualizer();
            mainEQ.openEQ();
        });
        
        pianoBtn.setOnAction(e -> {
            Piano piano = new Piano();
            piano.openPiano();
        });
        
        synthPianoBtn.setOnAction(e -> {
            SynthPiano synthPiano = new SynthPiano();
            synthPiano.openSynthPiano();
        });
        
        
        settings_btn.setOnMouseClicked(e -> {
            SettingsWindow settings = new SettingsWindow();
            SettingsController.setWindow(settings);
            settings.show();
        });

        stereoBtn.setOnMouseClicked(e -> {
            StereoStage stereo = new StereoStage();
            stereo.show();
        });

        butterworthBtn.setOnMouseClicked(e -> {
            ButterWorthStage butterWorth = new ButterWorthStage();
            butterWorth.show();
        });

        basicFilterBtn.setOnMouseClicked(e -> {
            BaseFiltersStage baseFilters = new BaseFiltersStage();
            baseFilters.show();
        });

        amplitudeBtn.setOnMouseClicked(e -> {
            AudioAmplitudeStage audioAmplitude = new AudioAmplitudeStage();
            audioAmplitude.show();
        });

        synthesizerBtn.setOnMouseClicked(e -> {
            SynthesizerStage synthesizerStage = new SynthesizerStage();
            synthesizerStage.synth();
        });

        //initializing nodes (loading images and other stuff)
        open_song_btn.setImage(new Image("/icons/load.png"));
        open_song_btn.setCursor(Cursor.HAND);
        save_song_btn.setImage(new Image("/icons/save.png"));
        save_song_btn.setCursor(Cursor.HAND);
        export_song_btn.setImage(new Image("/icons/export.png"));
        export_song_btn.setCursor(Cursor.HAND);
        close_btn.setImage(new Image("/icons/close.png"));
        close_btn.setCursor(Cursor.HAND);
//        close_btn.setEffect(blend);
        minim_btn.setImage(new Image("/icons/inconify.png"));
        minim_btn.setCursor(Cursor.HAND);
        maxim_btn.setImage(new Image("/icons/minimize.png"));
        maxim_btn.setCursor(Cursor.HAND);
        metronome_control.setImage(new Image("/icons/metronome.png"));
        metronome_control.setCursor(Cursor.HAND);
        settings_btn.setImage(new Image("/icons/settings.png"));
        settings_btn.setCursor(Cursor.HAND);
        snap_btn.setImage(new Image("/icons/snap.png"));
        snap_btn.setCursor(Cursor.HAND);

        snap_btn.getParent().setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: solid; -fx-border-radius: 5px");

        playback_pos.setText(timeToString(0));

        grid_root.setStyle("-fx-background-color: #D9D9D9");
        song_name.getParent().setStyle("-fx-border-width: 1px; -fx-border-radius: 5px; -fx-border-style: solid; -fx-border-color: black; -fx-background-color: #D9D9D9; -fx-background-radius: 5px");
        song_name.setStyle("-fx-background-color: transparent; -fx-border-width: 0px 1px 0px 1px; -fx-border-color: black; -fx-border-style: solid; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        export_song_btn.getParent().setStyle("-fx-border-style: solid; -fx-border-width: 0px 0px 0px 1px; -fx-border-color: black; -fx-background-color: transparent");
        bpm_control.setStyle("-fx-background-color: transparent; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-border-color: black; -fx-border-style: solid; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        record_control.getParent().setStyle("-fx-border-color: black; -fx-border-radius: 100%; -fx-border-width: 1px; -fx-border-style: solid");
        metronome_control.getParent().setStyle("-fx-border-width: 1px; -fx-border-radius: 5px; -fx-border-style: solid; -fx-border-color: black;");
        playback_pos.setStyle("-fx-background-color: transparent; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-border-color: black; -fx-border-style: solid;");
        audio_vis_top.getParent().setStyle("-fx-border-color: black; -fx-border-radius: 5px; -fx-border-width: 1px; -fx-border-style: solid");
        amp_audio_top.getParent().setStyle("-fx-border-color: black; -fx-border-radius: 5px; -fx-border-width: 1px; -fx-border-style: solid");
        pc_stats.getParent().setStyle("-fx-border-color: black; -fx-border-radius: 5px; -fx-border-width: 1px; -fx-border-style: solid");
        tab_vbox.setStyle("-fx-background-color: transparent");

        initialHeight = Screen.getPrimary().getVisualBounds().getHeight();
        initialWidth = Screen.getPrimary().getVisualBounds().getWidth();


        //adding different code for nodes
        tab_vbox.setSpacing(15);
        tab_vbox.setPrefHeight(Region.USE_COMPUTED_SIZE);

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

        bpm_control.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {

        });

        grid_root.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            xResize = event.getSceneX();
            yResize = event.getSceneY();

            if (event.getX() >= grid_root.getWidth() - 10 && event.getY() >= grid_root.getHeight() - 10) {
                resizing = true;
                rootStage.getScene().setCursor(Cursor.SE_RESIZE);
            }
        });

        grid_root.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (resizing) {
                rootStage.setWidth(initialWidth + (event.getSceneX() - xResize));
                rootStage.setHeight(initialHeight + (event.getSceneY() - yResize));
            }
        });

        grid_root.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (resizing) {
                initialWidth = rootStage.getWidth();
                initialHeight = rootStage.getHeight();
                setSplitRatio();
                resizing = false;
                rootStage.getScene().setCursor(Cursor.DEFAULT);
            }
        });

        minim_btn.setOnMouseClicked(event -> {
            rootStage.setIconified(true);
        });

        maxim_btn.setOnMouseClicked(event -> {
            if (!rootStage.isMaximized()) {
                rootStage.setX(0);
                rootStage.setY(0);
                rootStage.setMaximized(true);
                maxim_btn.setImage(new Image("/icons/minimize.png"));
            } else {
                rootStage.setMaximized(false);
                rootStage.setWidth(initialWidth);
                rootStage.setHeight(initialHeight);
                maxim_btn.setImage(new Image("/icons/maximize.png"));
            }
            setSplitRatio();
        });

        close_btn.setOnMouseClicked(event -> {
            sm.stop();
            rootStage.close();
        });

        track_vbox.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.isShiftDown()) {
                double delta = e.getDeltaX() * 0.005;
                tracks_scrollpane.setHvalue(tracks_scrollpane.getHvalue() - delta);
                e.consume();
            }
        });

        tracks_scrollpane.vvalueProperty().bindBidirectional(track_id_scrollpane.vvalueProperty());

        tracks_scrollpane.hvalueProperty().bindBidirectional(timeline_scrollpane.hvalueProperty());

        int num = 0;
        for (Track track : song.getTracks()) {
            track_vbox.getChildren().addAll(new TrackUI((song.getBpm() * 32), track));
//            track_vbox.getChildren().add(track.getContainer((int) (song.getBpm() * 32), num));
//            track_id_vbox.getChildren().add(track.addTrackID());
//            channel_rack.getChildren().add(track.createChannel((byte) (num + 1)));
//            num++;
//            System.out.println(track.getId());
        }

        //Test functions
        timeline_canvas.setWidth(song.getBpm() * 32);
        curUser = System.getProperty("user.name");
//        System.out.println(curUser);

        channel_rack.getChildren().add(0, creatingMasterChannel()); //test

        song_name.setText(song.getSongName());

        search_samples.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAudioSections(newValue);
        });


        fileLoader = new FileLoader(tab_vbox);

        drawTimeline();

        grid_root.setOnKeyPressed(e -> {
            pressedKeys.add(e.getCode());
            if (pressedKeys.contains(KeyCode.CONTROL) && pressedKeys.contains(KeyCode.A)) {

            }
            if (pressedKeys.contains(KeyCode.CONTROL) && pressedKeys.contains(KeyCode.D)) {

            }
            if (pressedKeys.contains(KeyCode.CONTROL) && pressedKeys.contains(KeyCode.Q)) {
                //get all the children in the track vbox and increase their width to the bpm * n (n being the increment -> default = 1, can increase in fractions or ints)
            }
        });

        grid_root.setOnKeyReleased(e -> {
            pressedKeys.remove(e.getCode());
        });

        tracks_channels.setStyle("-fx-background-color: black;");
        timeline_scrollpane.setStyle("-fx-background-color: transparent;");
        timeline_canvas.setStyle("-fx-background-color: transparent;");
        track_vbox.setStyle("-fx-background-color: black;");
        track_id_vbox.setStyle("-fx-background-color: black;");
        tracks_scrollpane.setStyle("-fx-background-color: transparent;");
        track_id_scrollpane.setStyle("-fx-background-color: transparent;");

        Rectangle clipSM = new Rectangle(pc_stats.getWidth(), pc_stats.getHeight());
        clipSM.setArcHeight(10);
        clipSM.setArcWidth(10);

        pc_stats.setClip(clipSM);
        sm = new SystemMonitor(pc_stats);
        sm.start();

        Rectangle clipAmp = new Rectangle(amp_audio_top.getWidth(), amp_audio_top.getHeight());
        clipAmp.setArcHeight(10);
        clipAmp.setArcWidth(10);

        Rectangle clipWave = new Rectangle(audio_vis_top.getWidth(), audio_vis_top.getHeight());
        clipWave.setArcHeight(10);
        clipWave.setArcWidth(10);

        amp_audio_top.setClip(clipAmp);
        audio_vis_top.setClip(clipWave);
    }

    protected void setSplitRatio() {
        double ratio = ((splitpane.getHeight() - 289) / splitpane.getHeight());
        splitpane.setDividerPosition(0, ratio);
    }

    public void setScreenSize() {
        if (rootStage != null) {
            rootStage.setWidth(initialWidth);
            rootStage.setHeight(initialHeight);
        }
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
        VBox.setMargin(masterVis, new Insets(0,5,0,5));

        Canvas masterChannelVis = new Canvas();
        masterChannelVis.setHeight(40);
        masterChannelVis.setWidth(16);

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
            } else clicked.set(false);
        });

        Slider channelAmp = new Slider(0,100,100);
        channelAmp.setOrientation(Orientation.VERTICAL);
        channelAmp.setPrefHeight(96);
        channelAmp.setLayoutY(90);
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

    private void drawTimeline() {
        GraphicsContext gc = timeline_canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, timeline_canvas.getWidth(), timeline_canvas.getHeight());

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, timeline_canvas.getWidth(), timeline_canvas.getHeight());

        gc.setFill(Color.GREY);
        gc.fillRoundRect(0,0, timeline_canvas.getWidth(), timeline_canvas.getHeight(), 10, 10);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Inter Regular", 12));

        for (int i = 0; i < timeline_canvas.getWidth(); i++) {
            if (i % 32 == 0) {
                short mult = (short) ((i / 32) + 1);
                gc.fillText(String.valueOf(mult), i, timeline_canvas.getHeight() - 12);
            }
            if (i % 8 == 0) {
                gc.fillRect(i, timeline_canvas.getHeight() - 10, (i % 32 != 0) ? 1 : 2, 8);
            }
        }

    }

    public String timeToString(long time) {
        long minutes = (time / 60000);
        long seconds = (time % 60000) / 1000;
        long milliseconds = time % 1000;

        return String.format("%d:%02d:%02d", minutes, seconds, milliseconds);
    }

    public void filterAudioSections(String searchText) {

        if (searchText == null || searchText.trim().isEmpty()) {
            for (Node node : tab_vbox.getChildren()) {
                if (node instanceof VBox section) {
                    section.setVisible(true);
                    section.setManaged(true);

                    VBox fileSectionList = (VBox) section.getChildren().get(1);
                    for (Node fileNode : fileSectionList.getChildren()) {
                        fileNode.setVisible(true);
                        fileNode.setManaged(true);
                    }

                    fileSectionList.setVisible(true);
                    fileSectionList.setManaged(true);
                }
            }
            return;
        }

        boolean specificSearch = searchText.startsWith("?");

        searchText = searchText.toLowerCase().replaceFirst("[?]", ""); // Remove prefix for comparison

        for (Node node : tab_vbox.getChildren()) {
            if (node instanceof VBox section) {
                String sectionName = section.getId().toLowerCase();
                VBox fileSectionList = (VBox) section.getChildren().get(1);

                boolean sectionMatches = sectionName.contains(searchText);
                boolean fileMatches = false;

                for (Node fileNode : fileSectionList.getChildren()) {
                    if (fileNode instanceof VBox fileVBox) {
                        String fileName = fileVBox.getId().toLowerCase();
                        boolean match = fileName.contains(searchText);

                        if (!specificSearch) {
                            fileVBox.setVisible(match);
                            fileVBox.setManaged(match);
                        }

                        if (match) fileMatches = true;
                    }
                }

                boolean showSection;
                if (specificSearch) {
                    showSection = sectionMatches;
                } else {
                    showSection = sectionMatches || fileMatches;
                    if (!fileMatches) {
                        fileSectionList.setVisible(false);
                        fileSectionList.setManaged(false);
                    }
                }

                section.setVisible(showSection);
                section.setManaged(showSection);
            }
        }
    }
}
