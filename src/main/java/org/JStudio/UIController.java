package org.JStudio;

import javafx.application.Platform;
import org.JStudio.Core.*;
import org.JStudio.Plugins.Views.PianoRun;
import org.JStudio.Plugins.Views.SynthPianoStage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.JStudio.Plugins.Views.*;
import org.JStudio.UI.*;
import org.JStudio.Utils.*;

import java.io.IOException;
import java.util.*;

import javafx.scene.Scene;
import org.JStudio.Plugins.Views.MainEqualizer;

/**
 * Class that takes care of the logic of the main app.
 */
public class UIController {
    private Scene scene;

    //implement later
    @FXML
    private VBox plugin_btn_pane;
    @FXML
    private Tab plugins_tab;
    //**

    @FXML
    public Pane plugin_pane;
    @FXML
    public TabPane channel_pipeline; //make this private but have a return function for it
    @FXML
    private TextField search_samples;
    @FXML
    private Canvas audio_vis_top, amp_audio_top, pc_stats, timeline_canvas;
    @FXML
    private SplitPane splitpane;
    @FXML
    private GridPane grid_root;
    @FXML
    private ScrollPane track_id_scrollpane, timeline_scrollpane, tracks_scrollpane;
    @FXML
    private VBox track_id_vbox, track_vbox, tab_vbox;
    @FXML
    private HBox info_panel, channel_rack;
    @FXML
    private Label playback_pos;
    @FXML
    private TextField bpm_control, song_name;
    @FXML
    private ImageView open_song_btn, save_song_btn, export_song_btn, minim_btn, close_btn, record_control, settings_btn, snap_btn, metronome_control, maxim_btn, playback_btn;
    @FXML
    private Stage rootStage;
    @FXML
    private Button reverbBtn, flangerBtn, chorusBtn, echoBtn, phaserBtn, equalizerBtn, pianoBtn, synthPianoBtn, stereoBtn, butterworthBtn, basicFilterBtn, amplitudeBtn, synthesizerBtn;

    private SystemMonitor sm; //make this a static class that runs in the background and closes

    private double xOffset = 0, yOffset = 0, xResize = 0, yResize = 0, secondaryWidth, secondaryHeight;
    private boolean resizing = false;

    public static BooleanProperty snap = new SimpleBooleanProperty(true);

    private final Set<KeyCode> pressedKeys = new HashSet<>();

    private Song song = new Song("New Song");
    private Mixer mixer = new Mixer(song);

    private TimelineUI timelineUI;

    public Mixer getMixer() {return mixer;}

    public void setStage(Stage stage) {
        rootStage = stage;

        rootStage.setOnShown(e -> {
            rootStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
            rootStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            rootStage.setX(0);
            rootStage.setY(0);

            secondaryHeight = Screen.getPrimary().getVisualBounds().getHeight();
            secondaryWidth = Screen.getPrimary().getVisualBounds().getWidth();

            setSplitRatio();
        });
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
        timelineUI = new TimelineUI(timeline_canvas);

//        plugins_tab.setOnSelectionChanged(e -> {
//            FileLoader.init(tab_vbox);
//        });
        
        reverbBtn.setOnAction(e -> {
            ReverbStage reverb = new ReverbStage();
            reverb.show();
//            reverb.setOnCloseRequest(ex ->{
//                FileLoader.init(tab_vbox);
//            });
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
            PianoRun piano = new PianoRun();
            piano.openPiano();
        });

        synthPianoBtn.setOnAction(e -> {
            SynthPianoStage synthPiano = new SynthPianoStage();
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

        open_song_btn.getParent().setOnMouseClicked(e -> { //none functioning code
//            try {
//                this.song = ExporterImporter.loadSong();
//                assert this.song != null;
//                track_vbox.getChildren().clear();
//                track_id_vbox.getChildren().clear();
//                channel_rack.getChildren().clear();
//                for (Track track : this.song.getTracks()) {
//                    track_vbox.getChildren().addAll(new TrackUI((Song.bpm.get() * 32), track));
//                    track_id_vbox.getChildren().add(new TrackIDUI(track, this));
//                    channel_rack.getChildren().add(new ChannelUI(track, track.getLeftAmp(), track.getRightAmp()));
//                    if (track.getClips() == null || track.getClips().isEmpty()) {continue;}
//                    for (Clip clip : track.getClips()) {
//                        track.addClip(clip);
//                        ((Pane) track_vbox.getChildren()).getChildren().add(new ClipUI(clip));
//                    }
//                }
//            } catch (IOException | ClassNotFoundException ex) {
//                throw new RuntimeException(ex);
//            }
            System.out.println("Currently not working: " + e.getTarget());
        });

        save_song_btn.getParent().setOnMouseClicked(e -> {
            try {
                if (ExporterImporter.saveSong(this.song, this.song.getSongName().get())) AlertBox.display("Save Status", "Song Saved Successfully");
                else AlertBox.display("Save Status", "Song Saved Failed");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        export_song_btn.getParent().setOnMouseClicked(e -> {
            try {
                if (ExporterImporter.exportSong(this.song, this.song.getSongName().get(), this.mixer)) AlertBox.display("Export Status", "Song Exported Successfully");
                else AlertBox.display("Export Status", "Song Exported Failed");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        mixer.getPlayBackPos().addListener((obs, oldPos, newPos) -> {
            double positionSeconds = mixer.getCurrentSample() / 44100.0;
            timelineUI.drawPlaybackMarker(positionSeconds * (Song.bpm.get() / 60) * 32);
            timeline_scrollpane.setHvalue(positionSeconds / 60.0);
        });

        // TODO: make a method or something to load a song, either here or in the song class itself (the ladder most likely)

        //initializing nodes (loading images and other stuff)
        open_song_btn.setImage(new Image("/icons/load.png"));
        open_song_btn.getParent().setCursor(Cursor.HAND);
        save_song_btn.setImage(new Image("/icons/save.png"));
        save_song_btn.getParent().setCursor(Cursor.HAND);
        export_song_btn.setImage(new Image("/icons/export.png"));
        export_song_btn.getParent().setCursor(Cursor.HAND);
        close_btn.setImage(new Image("/icons/close.png"));
        close_btn.setCursor(Cursor.HAND);
        minim_btn.setImage(new Image("/icons/inconify.png"));
        minim_btn.getParent().setCursor(Cursor.HAND);
        maxim_btn.setImage(new Image("/icons/minimize.png"));
        maxim_btn.getParent().setCursor(Cursor.HAND);
//        metronome_control.setImage(new Image("/icons/metronome.png"));
//        metronome_control.getParent().setCursor(Cursor.HAND);
        settings_btn.setImage(new Image("/icons/settings.png"));
        settings_btn.getParent().setCursor(Cursor.HAND);
        snap_btn.setImage(new Image("/icons/snap.png"));
        snap_btn.getParent().setCursor(Cursor.HAND);
//        record_control.setImage(new Image("/icons/record.png"));
//        record_control.getParent().setCursor(Cursor.HAND);
        playback_btn.setImage(new Image("/icons/play.png"));
        playback_btn.getParent().setCursor(Cursor.HAND);

        playback_pos.setText(TimeConverter.longToString(0));

        grid_root.setId("grid_root");
        open_song_btn.getParent().setId("open_song_btn_parent");
        song_name.getParent().setId("song_name_parent");
        song_name.setId("song_name");
        save_song_btn.getParent().setId("save_song_btn_parent");
        export_song_btn.getParent().setId("export_song_btn_parent");
        bpm_control.setId("bpm_control");
//        record_control.getParent().setId("record_control_parent");
//        metronome_control.getParent().setId("metronome_control_parent");
        playback_pos.setId("playback_pos");
        audio_vis_top.getParent().setId("audio_vis_top_parent");
        amp_audio_top.getParent().setId("amp_audio_top_parent");
        pc_stats.getParent().setId("pc_stats_parent");
        tab_vbox.setId("tab_vbox");
        snap_btn.getParent().setId("snap_btn_parent");
        playback_btn.getParent().setId("playback_btn_parent");

        snap_btn.getParent().getStyleClass().add("iactive");
//        song_name.setText("New Song");

//        metronome_control.getParent().setOnMousePressed(e -> {
//            if (metronome_control.getParent().getStyleClass().contains("iactive")) {
//                metronome_control.getParent().getStyleClass().remove("iactive");
//            } else {
//                metronome_control.getParent().getStyleClass().add("iactive");
//            }
//        });

        playback_btn.getParent().setOnMousePressed(e -> {
            if (playback_btn.getParent().getStyleClass().contains("iactive")) {
                playback_btn.getParent().getStyleClass().remove("iactive");
                mixer.getRunning().set(false);
            } else {
                playback_btn.getParent().getStyleClass().add("iactive");
                mixer.getRunning().set(true);
            }
//            mixer.getRunning().set(mixer.getRunning().get());
            System.out.println(mixer.getRunning().get());
        });

        snap_btn.getParent().setOnMousePressed(e -> snap.set(!snap.get()));

        playback_pos.textProperty().bind(mixer.getPlayBackPos());

        snap.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                if (!snap_btn.getParent().getStyleClass().contains("iactive")) snap_btn.getParent().getStyleClass().add("iactive");
            } else {
                snap_btn.getParent().getStyleClass().remove("iactive");
            }
        });

//        record_control.getParent().setOnMousePressed(event -> {
//            if (record_control.getParent().getStyleClass().contains("iactive")) {
//                record_control.getParent().getStyleClass().remove("iactive");
//            } else {
//                record_control.getParent().getStyleClass().add("iactive");
//            }
//        });

        info_panel.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            xOffset = rootStage.getX() - event.getScreenX();
            yOffset = rootStage.getY() - event.getScreenY();
        });

        info_panel.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            rootStage.setX(event.getScreenX() + xOffset);
            rootStage.setY(event.getScreenY() + yOffset);
        });

//        bpm_control.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
//            //to resize the playback
//        });

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
                rootStage.setWidth(secondaryWidth + (event.getSceneX() - xResize));
                rootStage.setHeight(secondaryHeight + (event.getSceneY() - yResize));
            }
        });

        grid_root.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (resizing) {
                secondaryWidth = rootStage.getWidth();
                secondaryHeight = rootStage.getHeight();
                setSplitRatio();
                resizing = false;
                rootStage.getScene().setCursor(Cursor.DEFAULT);
            }
        });

        minim_btn.setOnMouseClicked(event -> {
            rootStage.setIconified(true);
        });

        maxim_btn.setOnMouseClicked(event -> {
            if (rootStage.getHeight() != Screen.getPrimary().getVisualBounds().getHeight() || rootStage.getWidth() != Screen.getPrimary().getVisualBounds().getWidth()) {
                rootStage.setX(0);
                rootStage.setY(0);
                rootStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
                rootStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
                maxim_btn.setImage(new Image("/icons/minimize.png"));
            } else {
                rootStage.setX(0);
                rootStage.setY(0);
                secondaryWidth = Screen.getPrimary().getVisualBounds().getWidth() * .75;
                secondaryHeight = Screen.getPrimary().getVisualBounds().getHeight() * .75;
                rootStage.setWidth(secondaryWidth);
                rootStage.setHeight(secondaryHeight);
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

        song_name.getParent().setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                if (song_name.getText() != null && !song_name.getText().trim().isEmpty()) {
                    song_name.getParent().requestFocus();
                }
            }
        });

        tracks_scrollpane.vvalueProperty().bindBidirectional(track_id_scrollpane.vvalueProperty());
        tracks_scrollpane.hvalueProperty().bindBidirectional(timeline_scrollpane.hvalueProperty());

        for (Track track : song.getTracks()) {
            track_vbox.getChildren().addAll(new TrackUI((Song.bpm.get() * 32), track));
            track_id_vbox.getChildren().add(new TrackIDUI(track, this));
            channel_rack.getChildren().add(new ChannelUI(track, track.getLeftAmp(), track.getRightAmp()));
        }

        timeline_canvas.setWidth(Song.bpm.get() * 32);

        channel_rack.getChildren().add(0, new MixerUI(mixer));

        song_name.textProperty().bindBidirectional(song.getSongName());

        search_samples.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAudioSections(newValue);
        });

        FileLoader.init(tab_vbox);



        grid_root.setOnKeyPressed(e -> {
            pressedKeys.add(e.getCode());
            if (pressedKeys.contains(KeyCode.CONTROL) && pressedKeys.contains(KeyCode.A)) {

            }
            if (pressedKeys.contains(KeyCode.CONTROL) && pressedKeys.contains(KeyCode.D)) {

            }
            if (pressedKeys.contains(KeyCode.CONTROL) && pressedKeys.contains(KeyCode.Q)) {
                //get all the children in the track vbox and increase their width to the bpm * n (n being the increment -> default = 1, can increase in fractions or ints)
            }
            if (pressedKeys.contains(KeyCode.CONTROL) && pressedKeys.contains(KeyCode.R)) {
                tab_vbox.getChildren().clear();
                FileLoader.init(tab_vbox);
                tab_vbox.requestFocus();
            }
            if (pressedKeys.contains(KeyCode.SPACE)) {
                if (mixer.getRunning().get()) {
                    playback_btn.getParent().getStyleClass().remove("iactive");
                    mixer.getRunning().set(false);
                } else {
                    playback_btn.getParent().getStyleClass().add("iactive");
                    mixer.getRunning().set(true);
                }
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

    private void setSplitRatio() {
        Platform.runLater(() -> {
            double totalHeight = splitpane.getHeight();
            if (totalHeight <= 0) return;
            double ratio = (totalHeight - 285) / totalHeight;
            splitpane.setDividerPosition(0, Math.max(0.0, Math.min(1.0, ratio)));
        });
    }

    private void filterAudioSections(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            for (Node node : tab_vbox.getChildren()) {
                if (node instanceof SectionUI section) {
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

        for (var node : tab_vbox.getChildren()) {

            if (node instanceof SectionUI section) {
                String sectionName = section.getSectionName().toLowerCase();
                VBox fileSectionList = (VBox) section.getChildren().get(1);

                boolean sectionMatches = sectionName.contains(searchText);
                boolean fileMatches = false;

                for (Node fileNode : fileSectionList.getChildren()) {
                    if (fileNode instanceof FileUI file) {
                        String fileName = file.getFileName().toLowerCase();
                        boolean match = fileName.contains(searchText);

                        if (!specificSearch) {
                            file.setVisible(match);
                            file.setManaged(match);
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
