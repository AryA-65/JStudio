package PianoSection.Controllers;

import PianoSection.Models.Note;
import PianoSection.Views.NotesView;
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
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class NotesController {

    private final double NOTE_BASE_WIDTH = 50;
    private final double NOTE_HEIGHT = 100;
    private final double RESIZE_BORDER = 10;
    private final double NOTE_MIN_WIDTH = 50;

    private Pane currentPane;
    private ArrayList<NotesView> allNoteViews = new ArrayList<>();
    private List<NotesView> currentNoteViews = new ArrayList<>();

    private double oldMousePos;
    private double newMousePos;

    private Synthesizer synth;
    private MidiChannel channel;
    private int noteNumStart = 37;

    private boolean overlaps;
    private boolean isResizingRight = false;
    private boolean isResizingLeft = false;

    @FXML
    private Pane mainPane;
    @FXML
    private VBox labelVBox;
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
        
        int addedTrackWidth = 3500;
        int newPaneWidth = 0;

        for (int i = 0; i < 36; i++) {
            Pane pane = (Pane) noteTracks.lookup("#pane" + i);
            if (pane != null) {
                pane.setPrefWidth(pane.getPrefWidth()+addedTrackWidth);
                newPaneWidth = (int) pane.getPrefWidth();
                pane.setTranslateX(addedTrackWidth/2);
                pane.setOnMouseEntered(mouseEvent -> currentPane = pane);
                pane.setOnMousePressed(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        addNote(e);
                    }
                });
            }
        }
        
        mainPane.setPrefWidth(labelVBox.getPrefWidth() + newPaneWidth);

        loadChannel();

        playButton.setOnAction(e -> playNotes());
    }

    private void addNote(MouseEvent e) {
        currentNoteViews = getNotes();

        //get array list of all currentNoteViews in the pane
        double x = e.getX(); //get mouse position
        int noteNum = noteNumStart + Integer.parseInt(currentPane.getId().replace("pane", ""));
        overlaps = false;

        Rectangle rectangle = new Rectangle(NOTE_BASE_WIDTH, NOTE_HEIGHT);
        rectangle.setLayoutX(x - NOTE_BASE_WIDTH / 2);

        if (rectangle.getLayoutX() < 0 || rectangle.getLayoutX() + rectangle.getWidth() > currentPane.getLayoutX() + currentPane.getTranslateX() + currentPane.getPrefWidth()) {
            overlaps = true;
        }

        //if there are already notes in the array list
        if (!currentNoteViews.isEmpty()) {
            for (int i = 0; i < currentNoteViews.size(); i++) {
                //make sure it does not intersect with other notes in the pane
                if (currentNoteViews.get(i).getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                    overlaps = true;
                }
            }
        }
        //if the are not intersection issues then add a note to the pane
        if (!overlaps) {
            Note newNote = new Note(noteNum, NOTE_HEIGHT, x - NOTE_BASE_WIDTH / 2, NOTE_BASE_WIDTH);
            NotesView noteView = new NotesView(newNote, NOTE_HEIGHT);
            noteView.setLayoutX(x - NOTE_BASE_WIDTH / 2);
            noteView.getNote().setNoteNum(noteNumStart + Integer.parseInt(currentPane.getId().replace("pane", ""))); //set the note number of the note to the corresponding synth note number
            dragNote(noteView); //make the rectangle draggable
            noteView.setOnMouseEntered(mouseEvent -> {
                noteView.setFill(Color.GREY);
            });
            noteView.setOnMouseExited(mouseEvent -> {
                noteView.setFill(Color.BLACK);
            });
            noteView.setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    removeNote(event);
                } else {
                    oldMousePos = event.getX();
                    newMousePos = event.getX();

                    isResizingLeft = (newMousePos > 0) && (newMousePos < RESIZE_BORDER);
                    isResizingRight = (newMousePos < noteView.getWidth()) && (newMousePos > noteView.getWidth() - RESIZE_BORDER);
                }
            });
            currentPane.getChildren().add(noteView);
            allNoteViews.add(noteView);
        }
    }

    private void removeNote(MouseEvent e) {
        currentPane.getChildren().remove(e.getSource());
        allNoteViews.remove(e.getSource());
    }

    private void dragNote(NotesView noteView) {

        //add Event Handler on mouse dragged that allows the notes to be dragged along the pane and be resized if the borders are dragged
        noteView.setOnMouseDragged(mouseEvent -> {
            newMousePos = mouseEvent.getX();
            overlaps = false;

            double deltaMousePos = newMousePos - oldMousePos; //get how much the mouse moved since starting the drag

            //resize left
            if (isResizingLeft) {
                double nextPosX = noteView.getLayoutX() + deltaMousePos; //get the next position that the note will be in once moved
                double nextWidth = noteView.getWidth() - deltaMousePos; // get the next width that the note will have once resized

                detectOverlap(noteView, nextPosX, nextWidth);

                if (!overlaps) {
                    noteView.setLayoutX(nextPosX);
                    noteView.setWidth(nextWidth);

                    if (noteView.getWidth() < NOTE_MIN_WIDTH) { //width is now below the minimum
                        double shrunkenWidth = noteView.getWidth(); //get the width it was reduced to
                        noteView.setWidth(NOTE_MIN_WIDTH); //set the width back to the minimum
                        noteView.setLayoutX(noteView.getLayoutX() - (NOTE_MIN_WIDTH - shrunkenWidth)); //move the note back by the difference between the minimum width and the width it was reduced to
                    }

                }
                //resize right
            } else if (isResizingRight) {
                double nextWidth = noteView.getWidth() + deltaMousePos; //get the next position that the note will be in once moved
                overlaps = false;

                detectOverlap(noteView, noteView.getLayoutX(), nextWidth);

                if (!overlaps) {
                    noteView.setWidth(nextWidth);

                    if (noteView.getWidth() < NOTE_MIN_WIDTH) {
                        noteView.setWidth(NOTE_MIN_WIDTH);
                    }
                    oldMousePos = newMousePos;
                }

                //move along pane
            } else {
                double nextPosX = noteView.getLayoutX() + deltaMousePos; //get the next position that the note will be in once moved
                overlaps = false;

                detectOverlap(noteView, nextPosX, noteView.getWidth());

                //if it doesn't intersect then move the note to that position
                if (!overlaps) {
                    noteView.setLayoutX(nextPosX);
                }
            }
        }
        );

        noteView.setOnMouseReleased(mouseEvent -> {
            isResizingRight = false;
            isResizingLeft = false;
        });

    }

    private void detectOverlap(NotesView noteView, double nextPosX, double nextWidth) {
        //Create a temporary rectangle to detect if it will intersect with any other notes once moved
        Rectangle tempRect = new Rectangle(nextWidth, noteView.getHeight());
        tempRect.setLayoutX(nextPosX);

        //Determine if intersection with the edge of the pane
        if (tempRect.getLayoutX() < 0 || tempRect.getLayoutX() + tempRect.getWidth() > currentPane.getLayoutX() + currentPane.getTranslateX() + currentPane.getPrefWidth()) {
            overlaps = true;
            return;
        }

        //Determine if intersecting with other notes
        for (int i = 0; i < currentNoteViews.size(); i++) {
            if (currentNoteViews.get(i) != noteView) {
                if (currentNoteViews.get(i).getBoundsInParent().intersects(tempRect.getBoundsInParent())) {
                    overlaps = true;
                    return;
                }
            }
        }
    }

    private void playNotes() {
        TranslateTransition movePlaybackLine = new TranslateTransition(Duration.seconds(30), playbackLine);
        movePlaybackLine.setInterpolator(Interpolator.LINEAR);
        movePlaybackLine.setFromX(playbackLineStartPos);
        movePlaybackLine.setToX(currentPane.getWidth());
        movePlaybackLine.setCycleCount(1);
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (NotesView noteView : allNoteViews) {
                    Shape intersection = Shape.intersect(playbackLine, noteView);
                    
                    if (intersection.getBoundsInLocal().getWidth() > 0 ||
                    intersection.getBoundsInLocal().getHeight() > 0) {
                        if (!noteView.getNote().isPlaying()) {
                            channel.noteOn(noteView.getNote().getNoteNum(), noteView.getNote().getVelocity());
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
        movePlaybackLine.setOnFinished(e -> {
            timer.stop();
        });
        movePlaybackLine.play();
        timer.start();
    }

    private void loadChannel() {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            synth.loadInstrument(synth.getDefaultSoundbank().getInstruments()[0]);
            channel = synth.getChannels()[0];
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    //returns an array list of all notes in the pane
    private ArrayList<NotesView> getNotes() {
        //create 
        ArrayList<NotesView> currentNoteViews = new ArrayList<>();
        for (Node n : currentPane.getChildren()) {
            currentNoteViews.add((NotesView) n);
        }
        return currentNoteViews;
    }
}
