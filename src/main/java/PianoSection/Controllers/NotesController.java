package PianoSection.Controllers;


import PianoSection.Models.Note;
import PianoSection.Views.NotesView;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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

    private MidiChannel channel;
    private int noteNumStart = 37;
    
    private boolean overlaps;
    private boolean isResizingRight = false;
    private boolean isResizingLeft = false;

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
                pane.setOnMousePressed(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        addNote(e);
                    }
                    if (e.getButton() == MouseButton.SECONDARY) {
                        removeNote(e);
                    }
                });
            }
        }

        loadChannel();

        playButton.setOnAction(e -> playNotes());
    }

    private void addNote(MouseEvent e) {

        currentNoteViews = getNotes();
        
        //get array list of all currentNoteViews in the pane
        double x = e.getX(); //get mouse position
        int noteNum = noteNumStart + Integer.parseInt(currentPane.getId().replace("pane", ""));
        overlaps = false;

        //if there are already currentNoteViews in the array list
        if (!currentNoteViews.isEmpty()) {
            for (int i = 0; i < currentNoteViews.size(); i++) {
                Rectangle rectangle = new Rectangle(NOTE_BASE_WIDTH, NOTE_HEIGHT);
                rectangle.setLayoutX(x - NOTE_BASE_WIDTH / 2);
                //make sure it does not intersect with other notes in the pane
                if (currentNoteViews.get(i).getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                    overlaps = true;
                }
            }
        }
        //if the are not intersection issues then add a rectangle to the pane
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
            currentPane.getChildren().add(noteView);
            allNoteViews.add(noteView);
        }
    }

    private void removeNote(MouseEvent e) {
        Rectangle rectangle = null;
        for (Node node : currentPane.getChildren()) {
            rectangle = (Rectangle) node;
            if (e.getX() > rectangle.getLayoutX() && e.getX() < (rectangle.getLayoutX() + rectangle.getWidth()) && e.getY() > rectangle.getLayoutY() && e.getY() < (rectangle.getLayoutY() + rectangle.getHeight())) {
                int pos = currentPane.getChildren().indexOf(node);
                allNoteViews.remove(pos);
//                currentNoteViews.remove(pos);
                currentPane.getChildren().remove(node);

            }

        }
    }
    
    private void dragNote(NotesView noteView) {
        //add Event Hnadler on mouse pressed that saves the starting position of the note
        noteView.setOnMousePressed(mouseEvent -> {
            oldMousePos = mouseEvent.getX();
            newMousePos = mouseEvent.getX();

            isResizingLeft = (newMousePos > 0) && (newMousePos < RESIZE_BORDER);
            isResizingRight = (newMousePos < noteView.getWidth()) && (newMousePos > noteView.getWidth() - RESIZE_BORDER);
        });

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
        if (tempRect.getLayoutX() < 0 || tempRect.getLayoutX() + tempRect.getWidth() > currentPane.getWidth()) {
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