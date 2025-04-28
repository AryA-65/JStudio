package org.JStudio;

import PianoManualSynth.SynthPiano;
import PianoSection.Piano;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
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
import org.JStudio.UI.ChannelUI;
import org.JStudio.UI.MixerUI;
import org.JStudio.UI.TrackIDUI;
import org.JStudio.UI.TrackUI;
import org.JStudio.Utils.SystemMonitor;

import java.util.*;

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
    private ImageView record_control;
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
    private FileCacheController fileLoaderController;
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
        minim_btn.getParent().setCursor(Cursor.HAND);
        maxim_btn.setImage(new Image("/icons/minimize.png"));
        maxim_btn.getParent().setCursor(Cursor.HAND);
        metronome_control.setImage(new Image("/icons/metronome.png"));
        metronome_control.getParent().setCursor(Cursor.HAND);
        settings_btn.setImage(new Image("/icons/settings.png"));
        settings_btn.getParent().setCursor(Cursor.HAND);
        snap_btn.setImage(new Image("/icons/snap.png"));
        snap_btn.getParent().setCursor(Cursor.HAND);
        record_control.setImage(new Image("/icons/record.png"));
        record_control.getParent().setCursor(Cursor.HAND);

        playback_pos.setText(timeToString(0));

        grid_root.setId("grid_root");
        song_name.getParent().setId("song_name_parent");
        song_name.setId("song_name");
        export_song_btn.getParent().setId("export_song_btn_parent");
        bpm_control.setId("bpm_control");
        record_control.getParent().setId("record_control_parent");
        metronome_control.getParent().setId("metronome_control_parent");
        playback_pos.setId("playback_pos");
        audio_vis_top.getParent().setId("audio_vis_top_parent");
        amp_audio_top.getParent().setId("amp_audio_top_parent");
        pc_stats.getParent().setId("pc_stats_parent");
        tab_vbox.setId("tab_vbox");
        snap_btn.getParent().setId("snap_btn_parent");

        initialHeight = Screen.getPrimary().getVisualBounds().getHeight();
        initialWidth = Screen.getPrimary().getVisualBounds().getWidth();

        //more testing
        metronome_control.getParent().setOnMousePressed(e -> {
            metronome_control.getParent().getStyleClass().add("pressed");
        });

        metronome_control.getParent().setOnMouseReleased(e -> {
            metronome_control.getParent().getStyleClass().remove("pressed");
        });

        snap_btn.getParent().setOnMousePressed(e -> {
            snap_btn.getParent().getStyleClass().add("pressed");
        });

        snap_btn.getParent().setOnMouseReleased(e -> {
            snap_btn.getParent().getStyleClass().remove("pressed");
        });

        //testing
        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(2);

        for (Node node : song_name.getParent().getChildrenUnmodifiable()) {
            if (node instanceof Rectangle) {
                node.setEffect(blur);
            }
        }

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

        for (Track track : song.getTracks()) {
            track_vbox.getChildren().addAll(new TrackUI((song.getBpm() * 32), track));
            track_id_vbox.getChildren().add(new TrackIDUI(track));
            channel_rack.getChildren().add(new ChannelUI(track));
        }

        //Test functions
        timeline_canvas.setWidth(song.getBpm() * 32);
        curUser = System.getProperty("user.name");
//        System.out.println(curUser);

        channel_rack.getChildren().add(0, new MixerUI()); //test

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

    private void drawTimeline() {
        GraphicsContext gc = timeline_canvas.getGraphicsContext2D();

        Rectangle clip = new Rectangle(timeline_canvas.getWidth(), timeline_canvas.getHeight());
        clip.setArcHeight(10);
        clip.setArcWidth(10);

        timeline_canvas.setClip(clip);

        gc.clearRect(0, 0, timeline_canvas.getWidth(), timeline_canvas.getHeight());

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
                gc.fillRect(i - 1, timeline_canvas.getHeight() - 10, (i % 32 != 0) ? 1 : 2, 8);
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
