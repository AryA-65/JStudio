package PianoSection.Controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;
import PianoSection.Models.Note;
import PianoSection.Views.NotesView;

public class NotesController {

    private final double RECTANGLE_BASE_WIDTH = 50;
    private final double RECTANGLE_HEIGHT = 100;
    private final double RESIZE_BORDER = 10;
    private final double RECT_MIN_WIDTH = 50;

    private Pane currentPane;
    private ArrayList<NotesView> allNoteViews = new ArrayList<>();
    private List<Note> notes = new ArrayList<>();

    private MidiChannel channel;
    private int noteNumStart = 37;

    @FXML
    private VBox noteTracks;
    @FXML
    private Button playButton;
    @FXML
    private Line playbackLine;

    private double playbackLineStartPos;

    @FXML
    public void initialize() {
        playbackLineStartPos = playbackLine.getLayoutX();

        for (int i = 0; i < 36; i++) {
            Pane pane = (Pane) noteTracks.lookup("#pane" + i);
            if (pane != null) {
                pane.setOnMouseEntered(mouseEvent -> currentPane = pane);
                pane.setOnMousePressed(this::addNote);
            }
        }

        loadChannel();

        playButton.setOnAction(e -> playNotes());
    }

    private void addNote(MouseEvent e) {
        double x = e.getX();
        int noteNum = noteNumStart + Integer.parseInt(currentPane.getId().replace("pane", ""));
        Note newNote = new Note(noteNum, RECTANGLE_HEIGHT, x - RECTANGLE_BASE_WIDTH / 2, RECTANGLE_BASE_WIDTH);
        notes.add(newNote);

        NotesView noteView = new NotesView(newNote, RECTANGLE_HEIGHT);
        currentPane.getChildren().add(noteView);
        allNoteViews.add(noteView);
    }

    private void playNotes() {
        TranslateTransition movePlaybackLine = new TranslateTransition(Duration.seconds(7.5), playbackLine);
        movePlaybackLine.setInterpolator(Interpolator.LINEAR);
        movePlaybackLine.setFromX(playbackLineStartPos);
        movePlaybackLine.setToX(1920);
        movePlaybackLine.setCycleCount(1);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (NotesView noteView : allNoteViews) {
                    if (playbackLine.getBoundsInParent().intersects(noteView.getBoundsInParent())) {
                        if (!noteView.getNote().isPlaying()) {
                            channel.noteOn(noteView.getNote().getNoteNum(), 90);
                            noteView.getNote().setPlaying(true);
                        }
                    } else {
                        if (noteView.getNote().isPlaying()) {
                            channel.noteOff(noteView.getNote().getNoteNum());
                            noteView.getNote().setPlaying(false);
                        }
                    }
                }
            }
        };

        movePlaybackLine.setOnFinished(e -> timer.stop());
        movePlaybackLine.play();
        timer.start();
    }

    private void loadChannel() {
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            synthesizer.loadInstrument(synthesizer.getDefaultSoundbank().getInstruments()[0]);
            channel = synthesizer.getChannels()[0];
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }
}