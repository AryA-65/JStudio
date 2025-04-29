package PianoSection.Views;

import PianoSection.Models.Note;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class NoteView extends Rectangle {
    private Note note;

    public NoteView(Note note, double height) {
        super(note.getWidth(), height);
        this.note = note;
        setLayoutX(note.getPositionX());
        setFill(Color.BLACK);

        setOnMouseEntered(mouseEvent -> setFill(Color.GREY));
        setOnMouseExited(mouseEvent -> setFill(Color.BLACK));
    }

    public Note getNote() {
        return note;
    }
}
