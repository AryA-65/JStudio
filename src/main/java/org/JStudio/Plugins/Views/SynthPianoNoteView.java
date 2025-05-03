package org.JStudio.Plugins.Views;

import org.JStudio.Plugins.Models.SynthPianoNote;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SynthPianoNoteView extends Rectangle {
    private SynthPianoNote note;

    //creates a rectangle to represent the note on the track
    public SynthPianoNoteView(SynthPianoNote note, double width, double height, double positionX) {
        super(width, height);
        note.setNoteView(this);
        this.note = note;
        setLayoutX(positionX);
        setFill(Color.BLACK);

        //changes the color of note if the user has mouse over note
        setOnMouseEntered(mouseEvent -> setFill(Color.GREY));
        setOnMouseExited(mouseEvent -> setFill(Color.BLACK));
    }

    //getter
    public SynthPianoNote getNote() {
        return note;
    }
}
