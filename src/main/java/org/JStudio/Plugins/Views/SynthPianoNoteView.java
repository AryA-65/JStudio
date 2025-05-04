package org.JStudio.Plugins.Views;

import org.JStudio.Plugins.Models.SynthPianoNote;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.JStudio.SettingsController;

public class SynthPianoNoteView extends Rectangle {
    private SynthPianoNote note;

    //creates a rectangle to represent the note on the track
    public SynthPianoNoteView(SynthPianoNote note, double width, double height, double positionX) {
        super(width, height);
        note.setNoteView(this);
        this.note = note;
        setLayoutX(positionX);
        switch (SettingsController.getNoteColor()) {
            case "Blue":
                setFill(Color.BLUE);
                setOnMouseEntered(mouseEvent -> setFill(Color.MEDIUMBLUE));
                setOnMouseExited(mouseEvent -> setFill(Color.BLUE));
                break;
            case "Green":
                setFill(Color.GREEN);
                setOnMouseEntered(mouseEvent -> setFill(Color.SEAGREEN));
                setOnMouseExited(mouseEvent -> setFill(Color.GREEN));
                break;
            case "Red":
                setFill(Color.RED);
                setOnMouseEntered(mouseEvent -> setFill(Color.CRIMSON));
                setOnMouseExited(mouseEvent -> setFill(Color.RED));
                break;
        }
        

        //changes the color of note if the user has mouse over note
        
    }

    //getter
    public SynthPianoNote getNote() {
        return note;
    }
}
