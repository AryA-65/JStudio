package PianoSection;

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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class PianoController {
    Piano piano;
    
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

    @FXML
    public void initialize() {
        
        piano = new Piano();
        
        //get starting position of the playback line
        piano.setPlaybackLineStartPos(playbackLine.getLayoutX());
        
        int addedTrackWidth = 3500;
        int newPaneWidth = 0;

        //All panes (tracks) have ID's from 0-35
        //get each pane
        for (int i = 0; i < 36; i++) {
            Pane pane = (Pane) noteTracks.lookup("#pane" + i);
            if (pane != null) { //if the pane exists, set parameters
                pane.setPrefWidth(pane.getPrefWidth()+addedTrackWidth);
                newPaneWidth = (int) pane.getPrefWidth();
                pane.setTranslateX(addedTrackWidth/2);
                pane.setOnMouseEntered(mouseEvent -> piano.setCurrentPane(pane));
                
                //add note to the pane if the user clicks on it
                pane.setOnMousePressed(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        addNote(e);
                    }
                });
            }
        }
        
        //sets the size of the master pane
        mainPane.setPrefWidth(labelVBox.getPrefWidth() + newPaneWidth);

        //loads the javax synthesizer
        loadChannel();

        //plays the notes when the user clicks the play button
        playButton.setOnAction(e -> playNotes());
    }

    //adds notes to the track when the user clicks on it
    private void addNote(MouseEvent e) {
        piano.setCurrentNoteViews(getNotes());

        //get array list of all currentNoteViews in the pane
        double x = e.getX(); //get mouse position
        int noteNum = piano.getNoteNumStart() + Integer.parseInt(piano.getCurrentPane().getId().replace("pane", ""));
        piano.setOverlaps(false);

        Rectangle rectangle = new Rectangle(piano.getNoteBaseWidth(), piano.getNoteHeight());
        rectangle.setLayoutX(x - piano.getNoteBaseWidth() / 2);

        if (rectangle.getLayoutX() < 0 || rectangle.getLayoutX() + rectangle.getWidth() > piano.getCurrentPane().getLayoutX() + piano.getCurrentPane().getTranslateX() + piano.getCurrentPane().getPrefWidth()) {
            piano.setOverlaps(true);
        }

        //if there are already notes in the array list
        if (!piano.getCurrentNoteViews().isEmpty()) {
            for (int i = 0; i < piano.getCurrentNoteViews().size(); i++) {
                //make sure it does not intersect with other notes in the pane
                if (piano.getCurrentNoteViews().get(i).getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                    piano.setOverlaps(true);
                }
            }
        }
        //if the are not intersection issues then add a note to the pane
        if (!piano.isOverlaps()) {
            Note newNote = new Note(noteNum, piano.getNoteHeight(), x - piano.getNoteBaseWidth() / 2, piano.getNoteBaseWidth());
            NoteView noteView = new NoteView(newNote, piano.getNoteHeight());
            noteView.setLayoutX(x - piano.getNoteBaseWidth() / 2);
            noteView.getNote().setNoteNum(piano.getNoteNumStart() + Integer.parseInt(piano.getCurrentPane().getId().replace("pane", ""))); //set the note number of the note to the corresponding synth note number
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
                    piano.setOldMousePos(event.getX());
                    piano.setNewMousePos(event.getX());

                    piano.setResizingLeft((piano.getNewMousePos() > 0) && (piano.getNewMousePos() < piano.getResizeBorder()));
                    piano.setResizingRight((piano.getNewMousePos() < noteView.getWidth()) && (piano.getNewMousePos() > noteView.getWidth() - piano.getResizeBorder()));
                }
            });
            piano.getCurrentPane().getChildren().add(noteView);
            piano.getAllNoteViews().add(noteView);
        }
    }

    //remove a note from the track
    private void removeNote(MouseEvent e) {
        piano.getCurrentPane().getChildren().remove(e.getSource());
        piano.getAllNoteViews().remove(e.getSource());
    }

    //makes notes draggable and resizable
    private void dragNote(NoteView noteView) {

        //add Event Handler on mouse dragged that allows the notes to be dragged along the pane and be resized if the borders are dragged
        noteView.setOnMouseDragged(mouseEvent -> {
            piano.setNewMousePos(mouseEvent.getX());
            piano.setOverlaps(false);

            double deltaMousePos = piano.getNewMousePos() - piano.getOldMousePos(); //get how much the mouse moved since starting the drag

            //resize left
            if (piano.isResizingLeft()) {
                double nextPosX = noteView.getLayoutX() + deltaMousePos; //get the next position that the note will be in once moved
                double nextWidth = noteView.getWidth() - deltaMousePos; // get the next width that the note will have once resized

                detectOverlap(noteView, nextPosX, nextWidth);

                if (!piano.isOverlaps()) {
                    noteView.setLayoutX(nextPosX);
                    noteView.setWidth(nextWidth);

                    if (noteView.getWidth() < piano.getNoteMinWidth()) { //width is now below the minimum
                        double shrunkenWidth = noteView.getWidth(); //get the width it was reduced to
                        noteView.setWidth(piano.getNoteMinWidth()); //set the width back to the minimum
                        noteView.setLayoutX(noteView.getLayoutX() - (piano.getNoteMinWidth() - shrunkenWidth)); //move the note back by the difference between the minimum width and the width it was reduced to
                    }

                }
                //resize right
            } else if (piano.isResizingRight()) {
                double nextWidth = noteView.getWidth() + deltaMousePos; //get the next position that the note will be in once moved
                piano.setOverlaps(false);

                detectOverlap(noteView, noteView.getLayoutX(), nextWidth);

                if (!piano.isOverlaps()) {
                    noteView.setWidth(nextWidth);

                    if (noteView.getWidth() < piano.getNoteMinWidth()) {
                        noteView.setWidth(piano.getNoteMinWidth());
                    }
                    piano.setOldMousePos(piano.getNewMousePos());
                }

                //move along pane
            } else {
                double nextPosX = noteView.getLayoutX() + deltaMousePos; //get the next position that the note will be in once moved
                piano.setOverlaps(false);

                detectOverlap(noteView, nextPosX, noteView.getWidth());

                //if it doesn't intersect then move the note to that position
                if (!piano.isOverlaps()) {
                    noteView.setLayoutX(nextPosX);
                }
            }
        }
        );

        //reset bools on mouse release
        noteView.setOnMouseReleased(mouseEvent -> {
            piano.setResizingRight(false);
            piano.setResizingLeft(false);
        });

    }

    //detect if a note will overlap with another note
    private void detectOverlap(NoteView noteView, double nextPosX, double nextWidth) {
        //Create a temporary rectangle to detect if it will intersect with any other notes once moved
        Rectangle tempRect = new Rectangle(nextWidth, noteView.getHeight());
        tempRect.setLayoutX(nextPosX);

        //Determine if intersection with the edge of the pane
        if (tempRect.getLayoutX() < 0 || tempRect.getLayoutX() + tempRect.getWidth() > piano.getCurrentPane().getLayoutX() + piano.getCurrentPane().getTranslateX() + piano.getCurrentPane().getPrefWidth()) {
            piano.setOverlaps(true);
            return;
        }

        //Determine if intersecting with other notes
        for (int i = 0; i < piano.getCurrentNoteViews().size(); i++) {
            if (piano.getCurrentNoteViews().get(i) != noteView) {
                if (piano.getCurrentNoteViews().get(i).getBoundsInParent().intersects(tempRect.getBoundsInParent())) {
                    piano.setOverlaps(true);
                    return;
                }
            }
        }
    }

    //plays the notes on each track
    private void playNotes() {
        //create playback line translate transition
        TranslateTransition movePlaybackLine = new TranslateTransition(Duration.seconds(30), playbackLine);
        movePlaybackLine.setInterpolator(Interpolator.LINEAR);
        movePlaybackLine.setFromX(piano.getPlaybackLineStartPos());
        movePlaybackLine.setToX(piano.getCurrentPane().getWidth());
        movePlaybackLine.setCycleCount(1);
        
        //called every frame
        AnimationTimer timer = new AnimationTimer() {
            @Override
            //detect if the playback line is touching each note and play/stop the note
            public void handle(long now) {
                for (NoteView noteView : piano.getAllNoteViews()) {
                    Shape intersection = Shape.intersect(playbackLine, noteView);
                    
                    //if the playback line is touching a note and the note is not playing, then play the note
                    if (intersection.getBoundsInLocal().getWidth() > 0 ||
                    intersection.getBoundsInLocal().getHeight() > 0) {
                        if (!noteView.getNote().isPlaying()) {
                            piano.getChannel().noteOn(noteView.getNote().getNoteNum(), noteView.getNote().getVelocity());
                            noteView.getNote().setPlaying(true);
                        }
                    } else {//if the playback line is not touching a note and the note is playing, then stop playing the note
                        if (noteView.getNote().isPlaying()) {
                            piano.getChannel().noteOff(noteView.getNote().getNoteNum());
                            noteView.getNote().setPlaying(false);
                        }
                    }
                }
            }
        };
        //stop the animation timer when the playback line animation ends
        movePlaybackLine.setOnFinished(e -> {
            timer.stop();
        });
        
        //start the animation and timer
        movePlaybackLine.play();
        timer.start();
    }

    //loads the javax synthesizer
    private void loadChannel() {
        try {
            piano.setSynth(MidiSystem.getSynthesizer());
            piano.getSynth().open();
            piano.getSynth().loadInstrument(piano.getSynth().getDefaultSoundbank().getInstruments()[0]);
            piano.setChannel(piano.getSynth().getChannels()[0]);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    //returns an array list of all notes in the pane
    private ArrayList<NoteView> getNotes() {
        ArrayList<NoteView> currentNoteViews = new ArrayList<>();
        for (Node n : piano.getCurrentPane().getChildren()) {
            currentNoteViews.add((NoteView) n);
        }
        return currentNoteViews;
    }
}
