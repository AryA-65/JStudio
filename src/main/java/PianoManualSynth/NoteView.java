package PianoManualSynth;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class NoteView extends Rectangle {
    private Note note;

    public NoteView(Note note, double width, double height, double positionX) {
        super(width, height);
        note.setNoteView(this);
        this.note = note;
        setLayoutX(positionX);
        setFill(Color.BLACK);

        setOnMouseEntered(mouseEvent -> setFill(Color.GREY));
        setOnMouseExited(mouseEvent -> setFill(Color.BLACK));
    }

    public Note getNote() {
        return note;
    }
}
