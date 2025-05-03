package org.JStudio.Plugins.Models;

import org.JStudio.Plugins.Views.SynthPianoNoteView;

public class SynthPianoNote {

    private SynthPianoTrack track;
    private SynthPianoNoteView noteView;
    private boolean isPlaying;
    private double startTime;
    private double endTime;

    //sets the track that the note is on
    public SynthPianoNote(SynthPianoTrack track) {
        this.track = track;
    }

    //getters and setters
    public SynthPianoNoteView getNoteView() {
        return noteView;
    }

    public void setNoteView(SynthPianoNoteView noteView) {
        this.noteView = noteView;
    }

    public SynthPianoTrack getTrack() {
        return track;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
    
    public double getStartTime(){
        return startTime;
    }

    //returns whether a note should be playing at a specific moment in time
    public boolean isActive(double currentTime) {
        int trackLengthSeconds = 30;
        startTime = noteView.getLayoutX() / track.getPrefWidth() * trackLengthSeconds;
        endTime = (noteView.getLayoutX() + noteView.getWidth())/ track.getPrefWidth() * trackLengthSeconds;

        return currentTime >= startTime && currentTime <= endTime;
    }
}
