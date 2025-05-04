package org.JStudio.Plugins.Views;

import org.JStudio.Plugins.Models.PianoNote;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.JStudio.SettingsController;

public class PianoNoteView extends Rectangle {
    private PianoNote note;

    //creates a rectangle to represent the note on the track
    public PianoNoteView(PianoNote note, double height) {
        super(note.getWidth(), height);
        this.note = note;
        setLayoutX(note.getPositionX());
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
    public PianoNote getNote() {
        return note;
    }
}
