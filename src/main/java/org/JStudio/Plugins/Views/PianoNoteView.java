package org.JStudio.Plugins.Views;

import org.JStudio.Plugins.Models.PianoNote;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PianoNoteView extends Rectangle {
    private PianoNote note;

    //creates a rectangle to represent the note on the track
    public PianoNoteView(PianoNote note, double height) {
        super(note.getWidth(), height);
        this.note = note;
        setLayoutX(note.getPositionX());
        setFill(Color.BLACK);

        //changes the color of note if the user has mouse over note
        setOnMouseEntered(mouseEvent -> setFill(Color.GREY));
        setOnMouseExited(mouseEvent -> setFill(Color.BLACK));
    }

    //getter
    public PianoNote getNote() {
        return note;
    }
}
