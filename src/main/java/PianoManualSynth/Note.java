package PianoManualSynth;

import PianoSection.Models.*;

public class Note {
    private NotesTrack track;
    private boolean isPlaying;
    
    public Note(NotesTrack track) {
        this.track = track;
    }
    
    public NotesTrack getTrack(){
        return track;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
}
