package org.JStudio.PianoSection;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

/**
 *
 * @author alexa
 */
public class NotesUIController {

    private final double rectangleBaseWidth = 50;
    private final double rectangleHeight = 100;
    private final double resizeBorder = 10;
    private final double rectMinWidth = 50;

    private Pane currentPane;

    private double oldMousePos;
    private double newMousePos;

    private boolean isResizingRight = false;
    private boolean isResizingLeft = false;

    private ArrayList<Note> notes;
    private ArrayList<Note> allNotes = new ArrayList<Note>();
    private boolean overlaps;

    private MidiChannel channel;
    private int noteNumStart = 37; //number for the synth corresponding to the first note in the program (in this case B)

    @FXML
    private VBox noteTracks; // The parent container
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
                pane.setOnMouseEntered(mouseEvent -> {
                    currentPane = pane;
                });
                pane.setOnMousePressed(this::addNote);
            }
        }

        loadChannel(); //load the synthesizer

        playButton.setOnAction(e -> {
            playNotes();
        });
    }

    //Makes a rectangle draggable
    private void dragNote(Note note) {
        //add Event Hnadler on mouse pressed that saves the starting position of the note
        note.setOnMousePressed(mouseEvent -> {
            oldMousePos = mouseEvent.getX();
            newMousePos = mouseEvent.getX();

            isResizingLeft = (newMousePos > 0) && (newMousePos < resizeBorder);
            isResizingRight = (newMousePos < note.getWidth()) && (newMousePos > note.getWidth() - resizeBorder);
        });

        //add Event Handler on mouse dragged that allows the notes to be dragged along the pane and be resized if the borders are dragged
        note.setOnMouseDragged(mouseEvent -> {
            newMousePos = mouseEvent.getX();
            notes = getNotes();
            overlaps = false;

            double deltaMousePos = newMousePos - oldMousePos; //get how much the mouse moved since starting the drag

            //resize left
            if (isResizingLeft) {
                double nextPosX = note.getLayoutX() + deltaMousePos; //get the next position that the note will be in once moved
                double nextWidth = note.getWidth() - deltaMousePos; // get the next width that the note will have once resized

                detectOverlap(note, nextPosX, nextWidth);

                if (!overlaps) {
                    note.setLayoutX(nextPosX);
                    note.setWidth(nextWidth);

                    if (note.getWidth() < rectMinWidth) { //width is now below the minimum
                        double shrunkenWidth = note.getWidth(); //get the width it was reduced to
                        note.setWidth(rectMinWidth); //set the width back to the minimum
                        note.setLayoutX(note.getLayoutX() - (rectMinWidth - shrunkenWidth)); //move the note back by the difference between the minimum width and the width it was reduced to
                    }

                }
                //resize right
            } else if (isResizingRight) {
                double nextWidth = note.getWidth() + deltaMousePos; //get the next position that the note will be in once moved
                overlaps = false;

                detectOverlap(note, note.getLayoutX(), nextWidth);

                if (!overlaps) {
                    note.setWidth(nextWidth);

                    if (note.getWidth() < rectMinWidth) {
                        note.setWidth(rectMinWidth);
                    }
                    oldMousePos = newMousePos;
                }

                //move along pane
            } else {
                double nextPosX = note.getLayoutX() + deltaMousePos; //get the next position that the note will be in once moved
                overlaps = false;

                detectOverlap(note, nextPosX, note.getWidth());

                //if it doesn't intersect then move the note to that position
                if (!overlaps) {
                    note.setLayoutX(nextPosX);
                }
            }
        }
        );

        note.setOnMouseReleased(mouseEvent -> {
            isResizingRight = false;
            isResizingLeft = false;
        });

    }

    //Adds a note to the pane centered at the current mouse position
    private void addNote(MouseEvent e) {
        //get array list of all notes in the pane
        notes = getNotes();
        double x = e.getX(); //get mouse position
        overlaps = false;

        //if there are already notes in the array list
        if (!notes.isEmpty()) {
            for (int i = 0; i < notes.size(); i++) {
                Rectangle rectangle = new Rectangle(rectangleBaseWidth, rectangleHeight);
                rectangle.setLayoutX(x - rectangleBaseWidth / 2);
                //make sure it does not intersect with other notes in the pane
                if (notes.get(i).getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                    overlaps = true;
                }
            }
        }
        //if the are not intersection issues then add a rectangle to the pane
        if (!overlaps) {
            Note note = new Note(rectangleBaseWidth, rectangleHeight);
            note.setLayoutX(x - rectangleBaseWidth / 2);
            note.setNoteNum(noteNumStart + Integer.parseInt(currentPane.getId().replace("pane", ""))); //set the note number of the note to the corresponding synth note number
            dragNote(note); //make the rectangle draggable
            note.setOnMouseEntered(mouseEvent -> {
                note.setFill(Color.GREY);
            });
            note.setOnMouseExited(mouseEvent -> {
                note.setFill(Color.BLACK);
            });
            currentPane.getChildren().add(note);
            allNotes.add(note);
        }
    }

    private void detectOverlap(Note note, double nextPosX, double nextWidth) {
        //Create a temporary rectangle to detect if it will intersect with any other notes once moved
        Rectangle tempRect = new Rectangle(nextWidth, note.getHeight());
        tempRect.setLayoutX(nextPosX);

        //Determine if intersection with the edge of the pane
        if (tempRect.getLayoutX() < 0 || tempRect.getLayoutX() + tempRect.getWidth() > currentPane.getWidth()) {
            overlaps = true;
            return;
        }

        //Determine if intersecting with other notes
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i) != note) {
                if (notes.get(i).getBoundsInParent().intersects(tempRect.getBoundsInParent())) {
                    overlaps = true;
                    return;
                }
            }
        }
    }

    //returns an array list of all notes in the pane
    private ArrayList<Note> getNotes() {
        //create 
        notes = new ArrayList<>();
        for (Node n : currentPane.getChildren()) {
            notes.add((Note) n);
        }
        return notes;
    }

    private void loadChannel() {
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            synthesizer.loadInstrument(synthesizer.getDefaultSoundbank().getInstruments()[0]);
            channel = synthesizer.getChannels()[0];

        } catch (MidiUnavailableException e) {
            System.out.println("Error getting synth.");
            e.printStackTrace();
        }
    }

    private void playNotes() {
        TranslateTransition movePlaybackLine = new TranslateTransition(Duration.seconds(7.5), playbackLine);
        movePlaybackLine.setInterpolator(Interpolator.LINEAR);
        movePlaybackLine.setFromX(playbackLineStartPos);
        movePlaybackLine.setToX(1920);
        movePlaybackLine.setCycleCount(1);
        // Collision detection using AnimationTimer
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (int i = 0; i < allNotes.size(); i++) {
                    if (playbackLine.getBoundsInParent().intersects(allNotes.get(i).getBoundsInParent())) {
                        if (!allNotes.get(i).getIsPlaying()) {
                            channel.noteOn(allNotes.get(i).getNoteNum(), 90);
                            System.out.println(allNotes.get(i).getNoteNum());
                            allNotes.get(i).setIsPlaying(true);
                        }
                    } else {
                        if (allNotes.get(i).getIsPlaying()) {
                            channel.noteOff(allNotes.get(i).getNoteNum());
                            allNotes.get(i).setIsPlaying(false);
                        }
                    }
                }
            }
        };

        movePlaybackLine.setOnFinished(e -> timer.stop()); // Stop timer when animation ends
        movePlaybackLine.play();
        timer.start(); // Start checking for collisions
    }
}
