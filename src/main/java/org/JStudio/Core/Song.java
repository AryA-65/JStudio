package org.JStudio.Core;

import javafx.beans.property.*;
import org.JStudio.Plugins.AliasingDistortion;
import org.JStudio.Plugins.Distortion;
import org.JStudio.Plugins.HarmonicDistortion;

import java.io.Serializable;
import java.util.ArrayList;

public class Song implements Serializable {
    private StringProperty songName = new SimpleStringProperty();
    public static FloatProperty bpm = new SimpleFloatProperty(120f);
    private final byte MIN_TRACKS = 16, MAX_TRACKS = 64;
    private byte numTracks = MIN_TRACKS;
    private ArrayList<Track> tracks = new ArrayList<>(numTracks);

    public Song(String name) {
        this.songName.set(name);
        System.out.println();
        for (int i = 0; i < numTracks; i++) { //for testing
            Track track = new Track();
            track.addPlugin(new AliasingDistortion(1f,1f));
            track.addPlugin(new HarmonicDistortion(1f,1f));
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
