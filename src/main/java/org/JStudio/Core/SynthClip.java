package org.JStudio.Core;

import java.util.ArrayList;

public class SynthClip extends Clip {
    private ArrayList<Note> notes;

    SynthClip(int position, short sample_rate) {
        super(position, sample_rate);
        notes = new ArrayList<>();
    }

    public void setNote(short note, int position, short sample_rate) {
        notes.add(new Note(sample_rate, note, position));
    }

    public void removeNote(int note, int position) {
        notes.removeIf(n -> n.getNote() == note && n.getPosition() == position);
    }

    public Note getNote(int note, int position) {
        return notes.get(note).getPosition() == position ? notes.get(note) : null;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }
}
