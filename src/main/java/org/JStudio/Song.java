package org.JStudio;

import java.util.ArrayList;

public class Song {
    private String songName;
    private float bpm;
    private long duration;
    private final byte MIN_TRACKS = 16, MAX_TRACKS = 64; //might reduce max tracks to something like 48 or 32
    private byte numTracks = MIN_TRACKS;
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
        if (numTracks < MAX_TRACKS) {
            tracks.add(track);
            numTracks++;
        }
    }

    public void removeTrack() {
        if (tracks.size() > MIN_TRACKS) {
            tracks.remove(tracks.size() - 1);
            numTracks--;
        }
    }

    public void removeTrack(int index) {
        if (index > 0 && index < numTracks / 2 && tracks.size() > MIN_TRACKS) {
            tracks.remove(index);
            numTracks--;
        }
    }

    public Track getTrack(int index) {
        return tracks.get(index);
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }
}
