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
                setFill(Color.web("#118AB2"));
                setOnMouseEntered(mouseEvent -> setFill(Color.web("#0F749B")));
                setOnMouseExited(mouseEvent -> setFill(Color.web("#118AB2")));
                break;
            case "Green":
                setFill(Color.web("#06D6A0"));
                setOnMouseEntered(mouseEvent -> setFill(Color.web("#05BD8E")));
                setOnMouseExited(mouseEvent -> setFill(Color.web("#06D6A0")));
                break;
            case "Red":
                setFill(Color.web("#FF6B6B"));
                setOnMouseEntered(mouseEvent -> setFill(Color.web("#E05555")));
                setOnMouseExited(mouseEvent -> setFill(Color.web("#FF6B6B")));
                break;
        }        
    }

    //getter
    public SynthPianoNote getNote() {
        return note;
    }
}
