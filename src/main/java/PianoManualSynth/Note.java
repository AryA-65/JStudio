package PianoManualSynth;

import PianoSection.Models.*;

public class Note {

    private PianoTrack track;
    private NoteView noteView;
    private boolean isPlaying;
    private double startTime;
    private double endTime;

    public Note(PianoTrack track) {
        this.track = track;
    }

    public NoteView getNoteView() {
        return noteView;
    }

    public void setNoteView(NoteView noteView) {
        this.noteView = noteView;
    }

    public PianoTrack getTrack() {
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

    boolean isActive(double currentTime) {
        int trackLengthSeconds = 30;
        startTime = noteView.getLayoutX() / track.getPrefWidth() * trackLengthSeconds;
        endTime = (noteView.getLayoutX() + noteView.getWidth())/ track.getPrefWidth() * trackLengthSeconds;

        return currentTime >= startTime && currentTime <= endTime;
    }
}
