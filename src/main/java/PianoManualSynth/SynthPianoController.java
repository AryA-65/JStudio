package PianoManualSynth;

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
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.JStudio.Plugins.Controllers.PopUpController;
import java.util.*;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import org.JStudio.Plugins.Synthesizer.Utility;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.AL;

public class SynthPianoController {

    private final double NOTE_BASE_WIDTH = 50;
    private final double NOTE_HEIGHT = 27;
    private final double RESIZE_BORDER = 10;
    private final double NOTE_MIN_WIDTH = 50;
    private double oldMousePos;
    private double newMousePos;
    private double playbackLineStartPos;

    private PianoTrack currentTrack;
    private ArrayList<NoteView> allNoteViews = new ArrayList<>();
    private List<NoteView> currentNoteViews = new ArrayList<>();

    private SynthController_Piano synthController;

    private long device;
    private long context;

    private boolean overlaps;
    private boolean isResizingRight = false;
    private boolean isResizingLeft = false;
    volatile boolean shouldStopPlayback = false;

    private int notesTrackWidth = 5320;
    private int newPaneWidth = 0;
    private int buffer;
    private int source;

    private TranslateTransition movePlaybackLine;

    private Thread trackThread;

    private Random random = new Random();

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

    //getter
    public SynthController_Piano getSynth() {
        return synthController;
    }
    
    public void setShouldStopPlayback(boolean shouldStop){
        shouldStopPlayback = shouldStop;
    }

    @FXML
    public void initialize() {
        //creates a new controller
        synthController = new SynthController_Piano();
        synthController.setNotesController(this);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        mainPane.setMaxWidth(0);
        mainPane.setMinWidth(0);
        mainPane.setMaxHeight(0);
        mainPane.setMinHeight(0);

        //gets the playback line starting position
        playbackLineStartPos = playbackLine.getLayoutX();

        //sets the size of the master pane
        mainPane.setPrefWidth(labelVBox.getPrefWidth() + newPaneWidth);

        //plays the notes on the tracks when the play button is clicked
        playButton.setOnAction(e -> {
            playButton.setDisable(true);
            playNotes();
        });

        //opens the track settings/synthesizer so the user can create their own sounds
        addNoteTrack.setOnAction(e -> {
            //stop any audio that was running
            shouldStopPlayback = true;
            
            //stops the playback line
            if (movePlaybackLine != null) {
                movePlaybackLine.stop();
            }

            openTrackOptions();
        });
    }

    //adds a note to the track
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
            NoteView noteView = new NoteView(newNote, NOTE_BASE_WIDTH, NOTE_HEIGHT, x - NOTE_BASE_WIDTH / 2);
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

    //removes a note from the track
    private void removeNote(MouseEvent e) {
        currentTrack.getChildren().remove(e.getSource());
        allNoteViews.remove(e.getSource());
    }

    //alows a note to be draggable and resizable
    private void dragNote(NoteView noteView) {

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

        //reset bools on mouse release
        noteView.setOnMouseReleased(mouseEvent -> {
            isResizingRight = false;
            isResizingLeft = false;
        });

    }

    //detects if a note will overlap with another note
    private void detectOverlap(NoteView noteView, double nextPosX, double nextWidth) {
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

    //opens the track settings/synthesizer for the user to make their own sounds
    public void openTrackOptions() {
        SynthMain_Piano synth = new SynthMain_Piano();
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
        PianoTrack track = new PianoTrack(frequency, txt1, txt2, txt3, tone1Value, tone2Value, tone3Value, volume1Value, volume2Value, volume3Value);
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
        if (trackThread != null && trackThread.isAlive()) {
            shouldStopPlayback = true;
            alSourceStop(source);
            try {
                trackThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        shouldStopPlayback = false;

        //creates a new thread that plays the audio
        trackThread = new Thread(() -> {

            device = alcOpenDevice((ByteBuffer) null);
            context = alcCreateContext(device, (IntBuffer) null);
            alcMakeContextCurrent(context);
            AL.createCapabilities(ALC.createCapabilities(device));

            int trackLengthSeconds = 30;
            double sampleRate = 44100;

            int totalSamples = (int) (trackLengthSeconds * sampleRate);
            short[] finalTrackSamples = new short[totalSamples];

            int wavePos = 1;

            //calculates each sample of the audio (44100 samples per second)
            for (int i = 0; i < totalSamples && !shouldStopPlayback; i++) {
                double currentTime = (double) i / sampleRate; // current time in seconds
                double mixedSample = 0;

                for (NoteView noteView : allNoteViews) { //for all notes placed
                    Note note = noteView.getNote();
                    if (note.isActive(currentTime)) { // Check if this note should be playing
                        //calculate the sample
                        int NORMALIZER = 6;
                        PianoTrack track = note.getTrack();
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
            buffer = alGenBuffers();
            source = alGenSources();
            alBufferData(buffer, AL_FORMAT_MONO16, finalTrackSamples, (int) sampleRate);
            alSourcei(source, AL_BUFFER, buffer);
            alSourcePlay(source);

            //start the playback line movement
            movePlaybackLine.play();

            shouldStopPlayback = false;
            
            //enable the play button
            playButton.setDisable(false);

            //wait until the audio is done playing
            while (!shouldStopPlayback && alGetSourcei(source, AL_SOURCE_STATE) == AL_PLAYING) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    break;
                }
            }

            alDeleteSources(source);
            alDeleteBuffers(buffer);
            alcDestroyContext(context);
            alcCloseDevice(device);
        });

        //create translate transition for the playback line to scroll across the tracks
        movePlaybackLine = new TranslateTransition(Duration.seconds(30), playbackLine);
        movePlaybackLine.setInterpolator(Interpolator.LINEAR);
        movePlaybackLine.setFromX(playbackLineStartPos);
        movePlaybackLine.setToX(currentTrack.getWidth());
        movePlaybackLine.setCycleCount(1);

        //start the thread
        trackThread.start();
    }

    //returns an array list of all notes in the pane
    private ArrayList<NoteView> getNotes() {
        ArrayList<NoteView> currentNoteViews = new ArrayList<>();
        for (Node n : currentTrack.getChildren()) {
            currentNoteViews.add((NoteView) n);
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
                random.nextDouble() * 2 - 1;
            default ->
                throw new RuntimeException("Oscillator is set to unknown waveform");
        };
    }
}
