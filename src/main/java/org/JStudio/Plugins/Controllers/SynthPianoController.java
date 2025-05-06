package org.JStudio.Plugins.Controllers;

import org.JStudio.Plugins.Models.SynthPianoNote;
import org.JStudio.Plugins.Views.SynthPianoNoteView;
import org.JStudio.Plugins.Models.SynthPianoTrack;
import org.JStudio.Plugins.Models.SynthPiano;
import org.JStudio.Plugins.Views.SynthMain_PianoStage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import org.JStudio.Plugins.SynthUtil.Utility;
import org.JStudio.Utils.AlertBox;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.AL;

public class SynthPianoController {
    SynthPiano synthPiano;

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
    
    public SynthPiano getSynthPiano(){
        return synthPiano;
    }

    @FXML
    public void initialize() {
        synthPiano = new SynthPiano();
        
        //creates a new controller
        synthPiano.setSynthController(new SynthController_Piano());
        synthPiano.getSynthController().getSynth().setNotesController(this);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        mainPane.setMaxWidth(0);
        mainPane.setMinWidth(0);
        mainPane.setMaxHeight(0);
        mainPane.setMinHeight(0);

        //gets the playback line starting position
        synthPiano.setPlaybackLineStartPos(playbackLine.getLayoutX());

        //sets the size of the master pane
        mainPane.setPrefWidth(labelVBox.getPrefWidth() + synthPiano.getNewPaneWidth());

        //plays the notes on the tracks when the play button is clicked
        playButton.setOnAction(e -> {
            if (playbackLine.isVisible()) {
            playButton.setDisable(true);
            playNotes();
            } else {
                AlertBox.display("Play Error", "Please add a track first.");
            }
        });

        //opens the track settings/synthesizer so the user can create their own sounds
        addNoteTrack.setOnAction(e -> {
            //stop any audio that was running
            synthPiano.setShouldStopPlayback(true);
            
            //stops the playback line
            if (synthPiano.getMovePlaybackLine() != null) {
                synthPiano.getMovePlaybackLine().stop();
            }

            openTrackOptions();
        });
    }

    //adds a note to the track
    private void addNote(MouseEvent e) {
        synthPiano.setCurrentNoteViews(getNotes());

        //get array list of all currentNoteViews in the pane
        double x = e.getX(); //get mouse position
        synthPiano.setOverlaps(false);

        Rectangle rectangle = new Rectangle(synthPiano.getNoteBaseWidth(), synthPiano.getNoteHeight());
        rectangle.setLayoutX(x - synthPiano.getNoteBaseWidth() / 2);

        if (rectangle.getLayoutX() < 0 || rectangle.getLayoutX() + rectangle.getWidth() > synthPiano.getCurrentTrack().getLayoutX() + synthPiano.getCurrentTrack().getTranslateX() + synthPiano.getCurrentTrack().getPrefWidth()) {
            synthPiano.setOverlaps(true);;
        }

        //if there are already notes in the array list
        if (!synthPiano.getCurrentNoteViews().isEmpty()) {
            for (int i = 0; i < synthPiano.getCurrentNoteViews().size(); i++) {
                //make sure it does not intersect with other notes in the pane
                if (synthPiano.getCurrentNoteViews().get(i).getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                    synthPiano.setOverlaps(true);;
                }
            }
        }

        //if the are not intersection issues then add a note to the pane
        if (!synthPiano.isOverlaps()) {
            SynthPianoNote newNote = new SynthPianoNote(synthPiano.getCurrentTrack());
            SynthPianoNoteView noteView = new SynthPianoNoteView(newNote, synthPiano.getNoteBaseWidth(), synthPiano.getNoteHeight(), x - synthPiano.getNoteBaseWidth() / 2);
            dragNote(noteView); //make the rectangle draggable
            noteView.setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    removeNote(event);
                } else {
                    synthPiano.setOldMousePos(event.getX());
                    synthPiano.setNewMousePos(event.getX());

                    synthPiano.setResizingLeft((synthPiano.getNewMousePos() > 0) && (synthPiano.getNewMousePos() < synthPiano.getResizeBorder()));
                    synthPiano.setResizingRight((synthPiano.getNewMousePos() < noteView.getWidth()) && (synthPiano.getNewMousePos() > noteView.getWidth() - synthPiano.getResizeBorder()));
                }
            });
            synthPiano.getCurrentTrack().getChildren().add(noteView);
            synthPiano.getAllNoteViews().add(noteView);
        }
    }

    //removes a note from the track
    private void removeNote(MouseEvent e) {
        synthPiano.getCurrentTrack().getChildren().remove(e.getSource());
        synthPiano.getAllNoteViews().remove(e.getSource());
    }

    //alows a note to be draggable and resizable
    private void dragNote(SynthPianoNoteView noteView) {

        //add Event Handler on mouse dragged that allows the notes to be dragged along the pane and be resized if the borders are dragged
        noteView.setOnMouseDragged(mouseEvent -> {
            synthPiano.setNewMousePos(mouseEvent.getX());
            synthPiano.setOverlaps(false);;

            double deltaMousePos = synthPiano.getNewMousePos() - synthPiano.getOldMousePos(); //get how much the mouse moved since starting the drag

            //resize left
            if (synthPiano.isResizingLeft()) {
                double nextPosX = noteView.getLayoutX() + deltaMousePos; //get the next position that the note will be in once moved
                double nextWidth = noteView.getWidth() - deltaMousePos; // get the next width that the note will have once resized

                detectOverlap(noteView, nextPosX, nextWidth);

                if (!synthPiano.isOverlaps()) {
                    noteView.setLayoutX(nextPosX);
                    noteView.setWidth(nextWidth);

                    if (noteView.getWidth() < synthPiano.getNoteMinWidth()) { //width is now below the minimum
                        double shrunkenWidth = noteView.getWidth(); //get the width it was reduced to
                        noteView.setWidth(synthPiano.getNoteMinWidth()); //set the width back to the minimum
                        noteView.setLayoutX(noteView.getLayoutX() - (synthPiano.getNoteMinWidth() - shrunkenWidth)); //move the note back by the difference between the minimum width and the width it was reduced to
                    }

                }
                //resize right
            } else if (synthPiano.isResizingRight()) {
                double nextWidth = noteView.getWidth() + deltaMousePos; //get the next position that the note will be in once moved
                synthPiano.setOverlaps(false);

                detectOverlap(noteView, noteView.getLayoutX(), nextWidth);

                if (!synthPiano.isOverlaps()) {
                    noteView.setWidth(nextWidth);

                    if (noteView.getWidth() < synthPiano.getNoteMinWidth()) {
                        noteView.setWidth(synthPiano.getNoteMinWidth());
                    }
                    synthPiano.setOldMousePos(synthPiano.getNewMousePos());
                }

                //move along pane
            } else {
                double nextPosX = noteView.getLayoutX() + deltaMousePos; //get the next position that the note will be in once moved
                synthPiano.setOverlaps(false);

                detectOverlap(noteView, nextPosX, noteView.getWidth());

                //if it doesn't intersect then move the note to that position
                if (!synthPiano.isOverlaps()) {
                    noteView.setLayoutX(nextPosX);
                }
            }
        }
        );

        //reset bools on mouse release
        noteView.setOnMouseReleased(mouseEvent -> {
            synthPiano.setResizingRight(false);
            synthPiano.setResizingLeft(false);
        });

    }

    //detects if a note will overlap with another note
    private void detectOverlap(SynthPianoNoteView noteView, double nextPosX, double nextWidth) {
        //Create a temporary rectangle to detect if it will intersect with any other notes once moved
        Rectangle tempRect = new Rectangle(nextWidth, noteView.getHeight());
        tempRect.setLayoutX(nextPosX);

        //Determine if intersection with the edge of the pane
        if (tempRect.getLayoutX() < 0 || tempRect.getLayoutX() + tempRect.getWidth() > synthPiano.getCurrentTrack().getLayoutX() + synthPiano.getCurrentTrack().getTranslateX() + synthPiano.getCurrentTrack().getPrefWidth()) {
            synthPiano.setOverlaps(true);
            return;
        }

        //Determine if intersecting with other notes
        for (int i = 0; i < synthPiano.getCurrentNoteViews().size(); i++) {
            if (synthPiano.getCurrentNoteViews().get(i) != noteView) {
                if (synthPiano.getCurrentNoteViews().get(i).getBoundsInParent().intersects(tempRect.getBoundsInParent())) {
                    synthPiano.setOverlaps(true);
                    return;
                }
            }
        }
    }

    //opens the track settings/synthesizer for the user to make their own sounds
    public void openTrackOptions() {
        SynthMain_PianoStage synth = new SynthMain_PianoStage();
        synth.setNotesController(this);
        try {
            synth.open();
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    //adds a new track to the piano
    public void addTrack(double frequency, String txt1, String txt2, String txt3, double tone1Value, double tone2Value, double tone3Value, double volume1Value, double volume2Value, double volume3Value) {
        //asks the user for the track name
        PopUpController popUp = new PopUpController();
        String trackName = popUp.showTextInputPopup();

        //setup the name label
        Label label = new Label(trackName);
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(111.0, 27.0);
        label.setFont(new Font(16.0));
        label.setStyle("-fx-background-color: #FFFFFF");
        label.setStyle("-fx-border-color: #000000");
        labelVBox.getChildren().add(label);

        //setup the track itself
        SynthPianoTrack track = new SynthPianoTrack(frequency, txt1, txt2, txt3, tone1Value, tone2Value, tone3Value, volume1Value, volume2Value, volume3Value);
        track.setId("pane" + noteTracks.getChildren().size());
        track.setPrefSize(1820.0, 27.0);
        track.setMaxWidth(synthPiano.getNotesTrackWidth());
        track.setMinWidth(synthPiano.getNotesTrackWidth());
        track.setPrefWidth(synthPiano.getNotesTrackWidth());
        track.setStyle("-fx-background-color: #FFFFFF");
        track.setStyle("-fx-border-color: #000000");
        track.setPrefWidth(synthPiano.getNotesTrackWidth());
        track.setTranslateX((synthPiano.getNotesTrackWidth() - 1820) / 2);
        track.setOnMouseEntered(mouseEvent -> synthPiano.setCurrentTrack(track));

        //adds a note to the track if the track is clicked
        track.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                addNote(e);
            }
        });
        noteTracks.getChildren().add(track);

        //dynamically increase the size of the master pane as tracks get added
        mainPane.setMaxWidth(label.getPrefWidth() + track.getMinWidth());
        mainPane.setPrefWidth(label.getPrefWidth() + track.getMinWidth());
        mainPane.setMinWidth(label.getPrefWidth() + track.getMinWidth());
        mainPane.setMaxHeight(track.getPrefHeight() * noteTracks.getChildren().size());
        mainPane.setMinHeight(track.getPrefHeight() * noteTracks.getChildren().size());
        mainPane.setPrefHeight(track.getPrefHeight() * noteTracks.getChildren().size());

        //enable the playback line
        playbackLine.setVisible(true);
        playbackLine.setEndY(track.getPrefHeight() * noteTracks.getChildren().size());
    }

    //plays the notes that the user placed on the tracks
    private void playNotes() {

        //if there is already a thread running, stop the audio and wait for it to end
        if (synthPiano.getTrackThread() != null && synthPiano.getTrackThread().isAlive()) {
            synthPiano.setShouldStopPlayback(true);
            alSourceStop(synthPiano.getSource());
            try {
                synthPiano.getTrackThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        synthPiano.setShouldStopPlayback(false);

        //creates a new thread that plays the audio
        synthPiano.setTrackThread(new Thread(() -> {

            synthPiano.setDevice(alcOpenDevice((ByteBuffer) null));
            synthPiano.setContext(alcCreateContext(synthPiano.getDevice(), (IntBuffer) null));
            alcMakeContextCurrent(synthPiano.getContext());
            AL.createCapabilities(ALC.createCapabilities(synthPiano.getDevice()));

            int trackLengthSeconds = 30;
            double sampleRate = 44100;

            int totalSamples = (int) (trackLengthSeconds * sampleRate);
            short[] finalTrackSamples = new short[totalSamples];

            int wavePos = 1;

            //calculates each sample of the audio (44100 samples per second)
            for (int i = 0; i < totalSamples && !synthPiano.isShouldStopPlayback(); i++) {
                double currentTime = (double) i / sampleRate; // current time in seconds
                double mixedSample = 0;

                for (SynthPianoNoteView noteView : synthPiano.getAllNoteViews()) { //for all notes placed
                    SynthPianoNote note = noteView.getNote();
                    if (note.isActive(currentTime)) { // Check if this note should be playing
                        //calculate the sample
                        int NORMALIZER = 6;
                        SynthPianoTrack track = note.getTrack();
                        if (track.getTone1Value() != 0) {
                            mixedSample += (generateWaveSample(track.getText1(), Utility.Math.offsetTone(track.getFrequency(), track.getTone1Value()), wavePos) * track.getVolume1Value()) / NORMALIZER;
                        }
                        if (track.getTone2Value() != 0) {
                            mixedSample += (generateWaveSample(track.getText2(), Utility.Math.offsetTone(track.getFrequency(), track.getTone2Value()), wavePos) * track.getVolume2Value()) / NORMALIZER;
                        }
                        if (track.getTone3Value() != 0) {
                            mixedSample += (generateWaveSample(track.getText3(), Utility.Math.offsetTone(track.getFrequency(), track.getTone3Value()), wavePos) * track.getVolume3Value()) / NORMALIZER;
                        }
                    }
                }
                //add the sample to the sample array to be played
                finalTrackSamples[i] = (short) (mixedSample * Short.MAX_VALUE);
                wavePos += 1;
            }

            //setup buffer and play the audio
            synthPiano.setBuffer(alGenBuffers());
            synthPiano.setSource(alGenSources());
            alBufferData(synthPiano.getBuffer(), AL_FORMAT_MONO16, finalTrackSamples, (int) sampleRate);
            alSourcei(synthPiano.getSource(), AL_BUFFER, synthPiano.getBuffer());
            alSourcePlay(synthPiano.getSource());

            //start the playback line movement
            synthPiano.getMovePlaybackLine().play();

            synthPiano.setShouldStopPlayback(false);
            
            //enable the play button
            playButton.setDisable(false);

            //wait until the audio is done playing
            while (!synthPiano.isShouldStopPlayback() && alGetSourcei(synthPiano.getSource(), AL_SOURCE_STATE) == AL_PLAYING) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    break;
                }
            }

            alDeleteSources(synthPiano.getSource());
            alDeleteBuffers(synthPiano.getBuffer());
            alcDestroyContext(synthPiano.getContext());
            alcCloseDevice(synthPiano.getDevice());
        }));

        //create translate transition for the playback line to scroll across the tracks
        synthPiano.setMovePlaybackLine(new TranslateTransition(Duration.seconds(30), playbackLine));
        synthPiano.getMovePlaybackLine().setInterpolator(Interpolator.LINEAR);
        synthPiano.getMovePlaybackLine().setFromX(synthPiano.getPlaybackLineStartPos());
        synthPiano.getMovePlaybackLine().setToX(synthPiano.getCurrentTrack().getWidth());
        synthPiano.getMovePlaybackLine().setCycleCount(1);

        //start the thread
        synthPiano.getTrackThread().start();
    }

    //returns an array list of all notes in the pane
    private ArrayList<SynthPianoNoteView> getNotes() {
        ArrayList<SynthPianoNoteView> currentNoteViews = new ArrayList<>();
        for (Node n : synthPiano.getCurrentTrack().getChildren()) {
            currentNoteViews.add((SynthPianoNoteView) n);
        }
        return currentNoteViews;
    }

    //generates the audio samples to be played depending on the user's set parameters
    public double generateWaveSample(String waveformType, double frequency, double wavePosition) {
        double tDivP = (wavePosition / (double) Utility.AudioInfo.SAMPLE_RATE) / (1d / frequency);

        final double a = 2d * (tDivP - Math.floor(0.5 + tDivP));
        return switch (waveformType) {
            case "Sine" ->
                Math.sin(Utility.Math.frequencyToAngularFrequency(frequency) * wavePosition / Utility.AudioInfo.SAMPLE_RATE);
            case "Square" ->
                Math.signum(Math.sin(Utility.Math.frequencyToAngularFrequency(frequency) * wavePosition / Utility.AudioInfo.SAMPLE_RATE));
            case "Saw" ->
                a;
            case "Triangle" ->
                2d * Math.abs(a) - 1;
            case "Noise" ->
                synthPiano.getRandom().nextDouble() * 2 - 1;
            default ->
                throw new RuntimeException("Oscillator is set to unknown waveform");
        };
    }
}
