package org.JStudio.Plugins.Models;

import org.JStudio.Plugins.Views.PianoNoteView;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Pane;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Synthesizer;

public class Piano {
    private final double noteBaseWidth = 50;
    private final double noteHeight = 27;
    private final double resizeBorder = 10;
    private final double noteMinWidth = 50;

    private Pane currentPane;
    private ArrayList<PianoNoteView> allNoteViews = new ArrayList<>();
    private List<PianoNoteView> currentNoteViews = new ArrayList<>();

    private double oldMousePos;
    private double newMousePos;

    private Synthesizer synth;
    private MidiChannel channel;
    private int noteNumStart = 37;

    private boolean overlaps;
    private boolean resizingRight = false;
    private boolean resizingLeft = false;
    
    private StringProperty name = new SimpleStringProperty("Piano");

    private double playbackLineStartPos;

    //getters and setters
    public ArrayList<PianoNoteView> getAllNoteViews() {
        return allNoteViews;
    }

    public MidiChannel getChannel() {
        return channel;
    }

    public List<PianoNoteView> getCurrentNoteViews() {
        return currentNoteViews;
    }

    public Pane getCurrentPane() {
        return currentPane;
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

    public int getNoteNumStart() {
        return noteNumStart;
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

    public Synthesizer getSynth() {
        return synth;
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

    public void setAllNoteViews(ArrayList<PianoNoteView> allNoteViews) {
        this.allNoteViews = allNoteViews;
    }

    public void setChannel(MidiChannel channel) {
        this.channel = channel;
    }

    public void setCurrentNoteViews(List<PianoNoteView> currentNoteViews) {
        this.currentNoteViews = currentNoteViews;
    }

    public void setCurrentPane(Pane currentPane) {
        this.currentPane = currentPane;
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

    public void setNoteNumStart(int noteNumStart) {
        this.noteNumStart = noteNumStart;
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

    public void setSynth(Synthesizer synth) {
        this.synth = synth;
    }
    
}
