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
        tracks = new ArrayList<>(numTracks);
        for (int i = 0; i < numTracks; i++) {
            tracks.add(new Track(""));
        }
    }

    public void setBpm(float bpm) {
        this.bpm = bpm;
    }

    public byte getNumTracks() {
        return numTracks;
    }

    public void addTrack(Track track) {
        tracks.add(track);
    }

    public Track getTrack(int index) {
        return tracks.get(index);
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }
}
