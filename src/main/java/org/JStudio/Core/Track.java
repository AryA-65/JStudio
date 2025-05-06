package org.JStudio.Core;

import javafx.beans.property.*;
import javafx.event.Event;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

import org.JStudio.Plugins.Plugin;
import org.JStudio.UI.ClipUI;

public class Track implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    private static short trackCounter = 0;

    private transient final StringProperty name = new SimpleStringProperty(), id = new SimpleStringProperty();
    private transient final DoubleProperty amplitude = new SimpleDoubleProperty(1), pitch = new SimpleDoubleProperty(0), pan = new SimpleDoubleProperty(0);
    public transient final BooleanProperty muted = new SimpleBooleanProperty(false);
    private final ArrayList<Clip> clips = new ArrayList<>();
    private final ArrayList<Plugin> plugins = new ArrayList<>();
    private transient final FloatProperty leftAmp = new SimpleFloatProperty(0), rightAmp = new SimpleFloatProperty(0);

    /**
     * initializing the track
     * @param name track name, set by user
     */
    public Track(String name) {
        this.name.set(name);
        this.id.set(String.valueOf(trackCounter++));
    }

    /**
     * default track
     */
    public Track() {
        this("Empty Track");
    }

    /**
     * @param clip
     */
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
        clips.remove(clip);
    }

    public void removeClip(Event e) {
        System.out.println("Removing clip: " + ((ClipUI) e.getTarget()).getNodeClip().getInfo());
        clips.remove(((ClipUI) e.getTarget()).getNodeClip());
    }

    public ArrayList<Clip> getClips() {
        return clips;
    }

    public float[][] process(float[][] outBuff, int chunkStartSample, int chunkSize, int sampleRate) {
        if (muted.get()) return new float[2][chunkSize];

        float[][] trackBuff = new float[2][chunkSize];

        for (Clip baseClip : clips) {
            if (!(baseClip instanceof AudioClip clip)) continue;

            int clipStartSample = (int) (clip.getPosition() * sampleRate);
            float[][] buffer = clip.getBuffer();
            int clipLength = buffer[0].length;
            int clipEndSample = clipStartSample + clipLength;

            if (clipEndSample <= chunkStartSample || clipStartSample >= chunkStartSample + chunkSize)
                continue;

            int overlapStart = Math.max(clipStartSample, chunkStartSample);
            int overlapEnd = Math.min(clipEndSample, chunkStartSample + chunkSize);
            int length = overlapEnd - overlapStart;

            int clipOffset = overlapStart - clipStartSample;
            int chunkOffset = overlapStart - chunkStartSample;

            double amp = amplitude.get();
            double panning = pan.get();
            double leftGain = panning <= 0 ? 1.0 : 1.0 - panning;
            double rightGain = panning >= 0 ? 1.0 : 1.0 + panning;

            float sumLeft = 0, sumRight = 0;
            for (int i = 0; i < length; i++) {
                float tempL = buffer[0][clipOffset + i] * (float) (amp * leftGain);
                float tempR = 0;
                if (buffer[1] != null) {
                    tempR += buffer[1][clipOffset + i] * (float) (amp * rightGain);
                }
                sumLeft += Math.abs(tempL);
                sumRight += Math.abs(tempR);
                trackBuff[0][chunkOffset + i] += tempL;
                trackBuff[1][chunkOffset + i] += tempR;
            }

            leftAmp.set((float)(sumLeft / chunkSize));
            rightAmp.set((float)(sumRight / chunkSize));
        }

        for (Plugin plugin : plugins) {
            trackBuff = plugin.processStereo(trackBuff);
        }

        for (int i = 0; i < chunkSize; i++) {
            outBuff[0][i] += trackBuff[0][i];
            outBuff[1][i] += trackBuff[1][i];
        }

        return outBuff;
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

    public FloatProperty getLeftAmp() {
        return leftAmp;
    }

    public FloatProperty getRightAmp() {
        return rightAmp;
    }
}

