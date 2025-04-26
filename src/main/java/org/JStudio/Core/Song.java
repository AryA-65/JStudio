package org.JStudio.Core;

import java.util.ArrayList;

public class Song {
    private String songName;
    private float bpm = 120;
    private final byte MIN_TRACKS = 16, MAX_TRACKS = 64;
    private byte numTracks = MIN_TRACKS;
    private ArrayList<Track> tracks = new ArrayList<>(MIN_TRACKS);

    public Song(String name) {
        this.songName = name;

    }

    public void setBpm(float bpm) {
        this.bpm = bpm;
    }

    public byte getNumTracks() {
        return numTracks;
    }

    public void addTrack() {

    }

    public void removeTrack() {

    }

    public double getBpm() {
        return bpm;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}
