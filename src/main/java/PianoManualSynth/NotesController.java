package PianoManualSynth;

import java.io.IOException;
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
import java.util.Optional;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;

public class NotesController {

    private final double NOTE_BASE_WIDTH = 50;
    private final double NOTE_HEIGHT = 27;
    private final double RESIZE_BORDER = 10;
    private final double NOTE_MIN_WIDTH = 50;

    private NotesTrack currentTrack;
    private ArrayList<NotesView> allNoteViews = new ArrayList<>();
    private List<NotesView> currentNoteViews = new ArrayList<>();

    private double oldMousePos;
    private double newMousePos;

    private Controller synthController;

    private int notesTrackWidth = 5320;
    private int newPaneWidth = 0;

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
    private Button addNoteTrack;
    @FXML
    private Line playbackLine;
    @FXML
    private ScrollPane scrollPane;

    private double playbackLineStartPos;

    public Controller getSynth() {
        return synthController;
    }

    @FXML
    public void initialize() {
        synthController = new Controller();
        synthController.setNotesController(this);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        mainPane.setMaxWidth(0);
        mainPane.setMinWidth(0);
        mainPane.setMaxHeight(0);
        mainPane.setMinHeight(0);

        playbackLineStartPos = playbackLine.getLayoutX();

        mainPane.setPrefWidth(labelVBox.getPrefWidth() + newPaneWidth);

        playButton.setOnAction(e -> playNotes());

        addNoteTrack.setOnAction(e -> openTrackOptions());
    }

    private void addNote(MouseEvent e) {
        currentNoteViews = getNotes();

        //get array list of all currentNoteViews in the pane
        double x = e.getX(); //get mouse position
        overlaps = false;

        Rectangle rectangle = new Rectangle(NOTE_BASE_WIDTH, NOTE_HEIGHT);
        rectangle.setLayoutX(x - NOTE_BASE_WIDTH / 2);

        if (rectangle.getLayoutX() < 0 || rectangle.getLayoutX() + rectangle.getWidth() > currentTrack.getLayoutX() + currentTrack.getTranslateX() + currentTrack.getPrefWidth()) {
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
            Note newNote = new Note(currentTrack);
            NotesView noteView = new NotesView(newNote, NOTE_BASE_WIDTH, NOTE_HEIGHT, x - NOTE_BASE_WIDTH / 2);
            dragNote(noteView); //make the rectangle draggable
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
            currentTrack.getChildren().add(noteView);
            allNoteViews.add(noteView);
        }
    }

    private void removeNote(MouseEvent e) {
        currentTrack.getChildren().remove(e.getSource());
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
        if (tempRect.getLayoutX() < 0 || tempRect.getLayoutX() + tempRect.getWidth() > currentTrack.getLayoutX() + currentTrack.getTranslateX() + currentTrack.getPrefWidth()) {
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

    public void openTrackOptions() {
        SynthMain synth = new SynthMain();
        synth.setNotesController(this);
        try {
            synth.open();
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void addTrack(AudioThread auTh, double frequency, String txt1, String txt2, String txt3, double tone1Value, double tone2Value, double tone3Value, double volume1Value, double volume2Value, double volume3Value) {
        String trackName = showTextInputPopup();

        Label label = new Label(trackName);
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(111.0, 27.0);
        label.setFont(new Font(16.0));
        label.setStyle("-fx-background-color: #FFFFFF");
        label.setStyle("-fx-border-color: #000000");
        labelVBox.getChildren().add(label);

        NotesTrack track = new NotesTrack(auTh, frequency, txt1, txt2, txt3, tone1Value, tone2Value, tone3Value, volume1Value, volume2Value, volume3Value);
        track.setId("pane" + noteTracks.getChildren().size());
        track.setPrefSize(1820.0, 27.0);
        track.setMaxWidth(notesTrackWidth);
        track.setMinWidth(notesTrackWidth);
        track.setPrefWidth(notesTrackWidth);
        track.setStyle("-fx-background-color: #FFFFFF");
        track.setStyle("-fx-border-color: #000000");
        track.setPrefWidth(notesTrackWidth);
        track.setTranslateX((notesTrackWidth - 1820) / 2);
        track.setOnMouseEntered(mouseEvent -> currentTrack = track);
        track.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                addNote(e);
            }
        });
        noteTracks.getChildren().add(track);

        mainPane.setMaxWidth(label.getPrefWidth() + track.getMinWidth());
        mainPane.setPrefWidth(label.getPrefWidth() + track.getMinWidth());
        mainPane.setMinWidth(label.getPrefWidth() + track.getMinWidth());
        mainPane.setMaxHeight(track.getPrefHeight() * noteTracks.getChildren().size());
        mainPane.setMinHeight(track.getPrefHeight() * noteTracks.getChildren().size());
        mainPane.setPrefHeight(track.getPrefHeight() * noteTracks.getChildren().size());

        playbackLine.setVisible(true);
        playbackLine.setEndY(track.getPrefHeight() * noteTracks.getChildren().size());
    }

    private void playNotes() {
        TranslateTransition movePlaybackLine = new TranslateTransition(Duration.seconds(30), playbackLine);
        movePlaybackLine.setInterpolator(Interpolator.LINEAR);
        movePlaybackLine.setFromX(playbackLineStartPos);
        movePlaybackLine.setToX(currentTrack.getWidth());
        movePlaybackLine.setCycleCount(1);
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (NotesView noteView : allNoteViews) {
                    Shape intersection = Shape.intersect(playbackLine, noteView);
                    NotesTrack track = noteView.getNote().getTrack();

                    if (intersection.getBoundsInLocal().getWidth() > 0
                            || intersection.getBoundsInLocal().getHeight() > 0) {
                        if (!noteView.getNote().isPlaying()) {
                            synthController.playAudio(track.getAudioThread(), track.getFrequency(), track.getText1(), track.getText2(), track.getText3(), track.getTone1Value(), track.getTone2Value(), track.getTone3Value(), track.getVolume1Value(), track.getVolume2Value(), track.getVolume3Value());
                            noteView.getNote().setPlaying(true);
                        }
                    } else {
                        if (noteView.getNote().isPlaying()) {
                            synthController.stopAudio(track.getAudioThread());
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

    //returns an array list of all notes in the pane
    private ArrayList<NotesView> getNotes() {
        //create 
        ArrayList<NotesView> currentNoteViews = new ArrayList<>();
        for (Node n : currentTrack.getChildren()) {
            currentNoteViews.add((NotesView) n);
        }
        return currentNoteViews;
    }

    public String showTextInputPopup() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Track");
        dialog.setHeaderText(null);

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        TextField inputNameField = new TextField();
        inputNameField.setPromptText("Name");

        VBox content = new VBox(inputNameField);
        content.setSpacing(10);
        dialog.getDialogPane().setContent(content);

        // Convert result to String when "Add" is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {

                return inputNameField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
}
