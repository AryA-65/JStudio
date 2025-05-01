package SynthPiano;

public class Note {

    private PianoTrack track;
    private NoteView noteView;
    private boolean isPlaying;
    private double startTime;
    private double endTime;

    //sets the track that the note is on
    public Note(PianoTrack track) {
        this.track = track;
    }

    //getters and setters
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

    //returns whether a note should be playing at a specific moment in time
    boolean isActive(double currentTime) {
        int trackLengthSeconds = 30;
        startTime = noteView.getLayoutX() / track.getPrefWidth() * trackLengthSeconds;
        endTime = (noteView.getLayoutX() + noteView.getWidth())/ track.getPrefWidth() * trackLengthSeconds;

        return currentTime >= startTime && currentTime <= endTime;
    }
}
