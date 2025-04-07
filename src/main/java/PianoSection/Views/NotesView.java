package PianoSection.Views;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import PianoSection.Models.Note;

public class NotesView extends Rectangle {
    private Note note;

    //create a rectangle that will be added to the pane to display a note
    public NotesView(Note note, double height) {
        super(note.getWidth(), height);
        this.note = note;
        setLayoutX(note.getPositionX());
        setFill(Color.BLACK);

        setOnMouseEntered(mouseEvent -> setFill(Color.GREY));
        setOnMouseExited(mouseEvent -> setFill(Color.BLACK));
    }

    //getter for the note
    public Note getNote() {
        return note;
    }
}
