package org.JStudio.Plugins.Models;

import org.JStudio.Plugins.Views.SynthPianoNoteView;
import org.JStudio.Plugins.Controllers.SynthController_Piano;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SynthPiano {
    private final double noteBaseWidth = 50;
    private final double noteHeight = 27;
    private final double resizeBorder = 10;
    private final double noteMinWidth = 50;
    private double oldMousePos;
    private double newMousePos;
    private double playbackLineStartPos;

    private SynthPianoTrack currentTrack;
    private ArrayList<SynthPianoNoteView> allNoteViews = new ArrayList<>();
    private List<SynthPianoNoteView> currentNoteViews = new ArrayList<>();

    private SynthController_Piano synthController;

    private long device;
    private long context;

    private boolean overlaps;
    private boolean resizingRight = false;
    private boolean resizingLeft = false;
    private volatile boolean shouldStopPlayback = false;

    private int notesTrackWidth = 5320;
    private int newPaneWidth = 0;
    private int buffer;
    private int source;

    private TranslateTransition movePlaybackLine;

    private Thread trackThread;

    private Random random = new Random();
    
    private StringProperty name = new SimpleStringProperty("Synth Piano");
    
    //getters and setters
    public ArrayList<SynthPianoNoteView> getAllNoteViews() {
        return allNoteViews;
    }

    public List<SynthPianoNoteView> getCurrentNoteViews() {
        return currentNoteViews;
    }

    public SynthPianoTrack getCurrentTrack() {
        return currentTrack;
    }

    public double getNoteBaseWidth() {
        return noteBaseWidth;
    }

    public double getNoteHeight() {
        return noteHeight;
    }

    public double getNoteMinWidth() {
        return noteMinWidth;
    }

    public StringProperty getName() {
        return name;
    }

    public double getNewMousePos() {
        return newMousePos;
    }

    public double getOldMousePos() {
        return oldMousePos;
    }

    public double getPlaybackLineStartPos() {
        return playbackLineStartPos;
    }

    public double getResizeBorder() {
        return resizeBorder;
    }

    public boolean isResizingLeft() {
        return resizingLeft;
    }

    public boolean isResizingRight() {
        return resizingRight;
    }

    public boolean isOverlaps() {
        return overlaps;
    }

    public int getBuffer() {
        return buffer;
    }

    public long getContext() {
        return context;
    }

    public long getDevice() {
        return device;
    }

    public TranslateTransition getMovePlaybackLine() {
        return movePlaybackLine;
    }

    public int getNewPaneWidth() {
        return newPaneWidth;
    }

    public int getNotesTrackWidth() {
        return notesTrackWidth;
    }

    public Random getRandom() {
        return random;
    }

    public int getSource() {
        return source;
    }

    public SynthController_Piano getSynthController() {
        return synthController;
    }

    public Thread getTrackThread() {
        return trackThread;
    }

    public boolean isShouldStopPlayback() {
        return shouldStopPlayback;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    public void setContext(long context) {
        this.context = context;
    }

    public void setDevice(long device) {
        this.device = device;
    }

    public void setMovePlaybackLine(TranslateTransition movePlaybackLine) {
        this.movePlaybackLine = movePlaybackLine;
    }

    public void setNewPaneWidth(int newPaneWidth) {
        this.newPaneWidth = newPaneWidth;
    }

    public void setNotesTrackWidth(int notesTrackWidth) {
        this.notesTrackWidth = notesTrackWidth;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setShouldStopPlayback(boolean shouldStopPlayback) {
        this.shouldStopPlayback = shouldStopPlayback;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public void setSynthController(SynthController_Piano synthController) {
        this.synthController = synthController;
    }

    public void setTrackThread(Thread trackThread) {
        this.trackThread = trackThread;
    }

    public void setAllNoteViews(ArrayList<SynthPianoNoteView> allNoteViews) {
        this.allNoteViews = allNoteViews;
    }

    public void setCurrentNoteViews(List<SynthPianoNoteView> currentNoteViews) {
        this.currentNoteViews = currentNoteViews;
    }

    public void setCurrentTrack(SynthPianoTrack currentTrack) {
        this.currentTrack = currentTrack;
    }

    public void setResizingLeft(boolean resizingLeft) {
        this.resizingLeft = resizingLeft;
    }

    public void setResizingRight(boolean resizingRight) {
        this.resizingRight = resizingRight;
    }

    public void setName(StringProperty name) {
        this.name = name;
    }

    public void setNewMousePos(double newMousePos) {
        this.newMousePos = newMousePos;
    }

    public void setOldMousePos(double oldMousePos) {
        this.oldMousePos = oldMousePos;
    }

    public void setOverlaps(boolean overlaps) {
        this.overlaps = overlaps;
    }

    public void setPlaybackLineStartPos(double playbackLineStartPos) {
        this.playbackLineStartPos = playbackLineStartPos;
    }
}
