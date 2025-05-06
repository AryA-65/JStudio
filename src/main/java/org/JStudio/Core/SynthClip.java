package org.JStudio.Core;

import java.util.ArrayList;

/**
 * Clip from a synth
 */
public class SynthClip extends Clip {
    private ArrayList<Note> notes;

    SynthClip(int position, short sample_rate) {
        super(position, sample_rate);
        notes = new ArrayList<>();
    }

    /**
     * Sets the note at a position
     * @param note the note
     * @param position the position
     * @param sample_rate the sample rate of the note
     */
    public void setNote(short note, int position, short sample_rate) {
        notes.add(new Note(sample_rate, note, position));
    }

    /**
     * Removes a note at a position
     * @param note the note
     * @param position the position
     */
    public void removeNote(int note, int position) {
        notes.removeIf(n -> n.getNote() == note && n.getPosition() == position);
    }

    /**
     * Retrieves the note at a position
     * @param note the note
     * @param position the position
     * @return the note at the desired position
     */
    public Note getNote(int note, int position) {
        return notes.get(note).getPosition() == position ? notes.get(note) : null;
    }

    /**
     * Gets all notes
     * @return all notes
     */
    public ArrayList<Note> getNotes() {
        return notes;
    }
}
