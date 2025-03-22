package org.JStudio;

import java.util.ArrayList;

public class Song {
    private String songName;
    private float bpm;
    private long duration;
    private byte numTracks = 16;
    private ArrayList<Track> tracks;

    Song(String name) {
        this.songName = name;
        this.bpm = 120; //default bpm for new songs
    }

    public void setBpm(float bpm) {
        this.bpm = bpm;
    }

}
