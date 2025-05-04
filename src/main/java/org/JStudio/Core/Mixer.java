package org.JStudio.Core;

import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class Mixer { //calls all the process methods for all active channels and plugins

    private final FloatProperty masterGain = new SimpleFloatProperty(1), pitch = new SimpleFloatProperty(0f), pan = new SimpleFloatProperty(0f);
    private final BooleanProperty muted = new SimpleBooleanProperty(false);

    private final List<Track> tracks = new ArrayList<Track>();

    public Mixer() {

    }

    public void addTrack(Track track) {
        synchronized (tracks) {
            tracks.add(track);
        }
    }

    public void removeTrack(Track track) {
        synchronized (tracks) {
            tracks.remove(track);
        }
    }

    //needs to be fixed
    public void process(List<Track> tracks , float[][] buffer, short buffSize) {
        for (int i = 0; i < buffSize; i++) {
            buffer[0][i] = 0.0f;
            buffer[1][i] = 0.0f;
        }

        synchronized (tracks) {
            for (Track track : tracks) {
                float[][] trackBuff = track.process(buffer.clone());
                for (int i = 0; i < buffSize; i++) {
                    buffer[0][i] += trackBuff[0][i];
                    buffer[1][i] += trackBuff[1][i];
                }
            }
        }

        for (int i = 0; i < buffSize; i++) {
            buffer[0][i] = (float) Math.max(-1.0f, Math.min(1.0f, buffer[0][i] * masterGain.get()));
            buffer[1][i] = (float) Math.max(-1.0f, Math.min(1.0f, buffer[1][i] * masterGain.get()));
        }
    }

    public BooleanProperty getMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted.set(muted);
    }

    public FloatProperty getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch.set(pitch);
    }

    public FloatProperty getPan() {
        return pan;
    }

    public void setMasterGain(float masterGain) {
        this.masterGain.set(masterGain);
    }

    public FloatProperty getMasterGain() {
        return masterGain;
    }
}
