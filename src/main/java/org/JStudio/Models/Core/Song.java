package org.JStudio.Models.Core;

import javafx.beans.property.*;
import org.JStudio.Plugins.Models.ClippingDistortion;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Song implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private transient StringProperty songName = new SimpleStringProperty();
    public static FloatProperty bpm = new SimpleFloatProperty(120f);
//    public static FloatProperty duration = new SimpleFloatProperty();
    private final byte MIN_TRACKS = 16, MAX_TRACKS = 64;
    private byte numTracks = MIN_TRACKS;
    private final ArrayList<Track> tracks = new ArrayList<>(numTracks);

    /**
     * initializing the son
     * @param name name of the song
     */
    public Song(String name) {
        this.songName.set(name);
        for (int i = 0; i < numTracks; i++) { //for testing
            Track track = new Track();
            track.addPlugin(new ClippingDistortion(1f,1f, 0.1f, ClippingDistortion.TYPE.HARD));
//            track.addPlugin(new HarmonicDistortion(1f,0f));
//            track.addPlugin(new BitcrusherPlugin(4));
//            track.addPlugin(new Reverb(4000,10000,1000,.8));
//            track.addPlugin(new AliasingDistortion(1f,.1f));
            tracks.add(track);
        }
    }

    public byte getNumTracks() {
        return numTracks;
    }

    public void addTrack() {
        //migrate from ui controller class to this class
    }

    public void removeTrack() {
        //same as addTrack()
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public StringProperty getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName.set(songName);
    }
}
