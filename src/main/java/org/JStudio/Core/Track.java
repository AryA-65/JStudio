package org.JStudio.Core;

import javafx.beans.property.*;
import javafx.event.Event;

import java.io.Serializable;
import java.util.ArrayList;

import org.JStudio.Plugins.Plugin;

public class Track implements Serializable {
    private static short trackCounter = 0;

    private final StringProperty name = new SimpleStringProperty(), id = new SimpleStringProperty();
    private final DoubleProperty amplitude = new SimpleDoubleProperty(100), pitch = new SimpleDoubleProperty(0), pan = new SimpleDoubleProperty(0);
    public final BooleanProperty muted = new SimpleBooleanProperty(false);
    private final ArrayList<Clip> clips = new ArrayList<>();
    private final ArrayList<Plugin> plugins = new ArrayList<>();

    public Track(String name) {
        this.name.set(name);
        this.id.set(String.valueOf(trackCounter++));
    }

    public Track() {
        this("Empty Track");
    }

    public void addClip(Clip clip) {
        clips.add(clip);
    }

    public void removeClip(int position) {
        for (Clip clip : clips) {
            if (position >= clip.getPosition() && position < clip.getPosition() + clip.getLength()) {
                clips.remove(clip);
                break;
            }
        }
    }

    public void removeClip(Clip clip) {
        System.out.println("Removing clip");
        clips.remove(clip);
        System.out.println(clips.size());
    }

    private void removeClip(Event e) {
        clips.removeIf(clip -> e.getTarget() == clip);
    }

    public float[][] process(float[][] buff) { //1024 chucks
        float[][] output = null;


        return output;
    }

    public StringProperty getName() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty getId() {
        return id;
    }

    public void setId(byte id) {
        this.id.set(String.valueOf(id));
    }

    public DoubleProperty getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude.set(amplitude);
    }

    public DoubleProperty getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch.set(pitch);
    }

    public DoubleProperty getPan() {
        return pan;
    }

    public void setPan(double pan) {
        this.pan.set(pan);
    }

    public BooleanProperty getMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted.set(muted);
    }

    public ArrayList<Plugin> getPlugins() {
        return plugins;
    }

    public void addPlugin(Plugin plugin) {
        plugins.add(plugin);
    }

    public void removePlugin(Plugin plugin) {
        plugins.remove(plugin);
    }
}

