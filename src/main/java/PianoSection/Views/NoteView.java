package PianoSection.Views;

import PianoSection.Models.Note;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class NoteView extends Rectangle {
    private Note note;

    //creates a rectangle to represent the note on the track
    public NoteView(Note note, double height) {
        super(note.getWidth(), height);
        this.note = note;
        setLayoutX(note.getPositionX());
        setFill(Color.BLACK);

        //changes the color of note if the user has mouse over note
        setOnMouseEntered(mouseEvent -> setFill(Color.GREY));
        setOnMouseExited(mouseEvent -> setFill(Color.BLACK));
    }

    //getter
    public Note getNote() {
        return note;
    }
}
