package org.JStudio.Core;

import javafx.application.Platform;
import javafx.beans.property.*;
import org.JStudio.Utils.TimeConverter;

import javax.sound.sampled.*;

public class Mixer {
    private final Song song;
    private final int sampleRate = 44100, chunkSize = 1024;
    private int currentSample = 0;
    private final byte bitDepth = 16;
    private final SourceDataLine line;
    private Thread processThread;

    private volatile BooleanProperty running = new SimpleBooleanProperty(false);

    private final FloatProperty masterGain = new SimpleFloatProperty(1), pitch = new SimpleFloatProperty(0f), pan = new SimpleFloatProperty(0f);
    private final BooleanProperty muted = new SimpleBooleanProperty(false);

    private volatile StringProperty playBackPos = new SimpleStringProperty(TimeConverter.doubleToString((double) currentSample / sampleRate));

    public Mixer(Song song) {
        this.song = song;
        this.line = setupAudioLine();

        running.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                start();
                System.out.println("Started");
            }
            else
                stop();
        });
    }

    private SourceDataLine setupAudioLine() {
        try {
            AudioFormat format = new AudioFormat(sampleRate, bitDepth, 2, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine newLine = (SourceDataLine) AudioSystem.getLine(info);
            newLine.open(format);
            newLine.start();
            return newLine;
        } catch (LineUnavailableException e) {
            throw new RuntimeException("Failed to initialize audio line", e);
        }
    }

    public void start() {
        processThread = new Thread(this::process);
        processThread.setDaemon(true);
        processThread.start();
    }

    public void stop() {
        if (processThread != null) {
            try { processThread.join(); } catch (InterruptedException ignored) {}
        }
//        line.stop();
        line.flush();
        System.gc();
        currentSample = 0;
    }

    //needs to be fixed
    private void process() {
        float[][] floatBuffer = new float[2][chunkSize];
        byte[] byteBuffer = new byte[chunkSize * 4];
        long lastUIUpdateTime = System.nanoTime();

        while (running.get()) {
            for (int ch = 0; ch < 2; ch++) {
                for (int i = 0; i < chunkSize; i++) floatBuffer[ch][i] = 0;
            }

            for (Track track : song.getTracks()) {
                track.process(floatBuffer, currentSample, chunkSize, sampleRate);
//                System.out.println(track.getId());
            }

            for (int i = 0; i < chunkSize; i++) {
                float left = floatBuffer[0][i] * masterGain.get() * (1 - pan.get());
                float right = floatBuffer[1][i] * masterGain.get() * (1 + pan.get());

                int sampleL = (int)(left * 32767.0);
                int sampleR = (int)(right * 32767.0);

                sampleL = Math.max(-32768, Math.min(32767, sampleL));
                sampleR = Math.max(-32768, Math.min(32767, sampleR));

                int index = i * 4;
                byteBuffer[index] = (byte)(sampleL & 0xFF);
                byteBuffer[index + 1] = (byte)((sampleL >> 8) & 0xFF);
                byteBuffer[index + 2] = (byte)(sampleR & 0xFF);
                byteBuffer[index + 3] = (byte)((sampleR >> 8) & 0xFF);
            }

            line.write(byteBuffer, 0, byteBuffer.length);

            if (currentSample <= (Song.bpm.get() / (Song.bpm.get() / 60) * sampleRate)) {
                currentSample += chunkSize;
            } else running.set(false);

            long now = System.nanoTime();
            if ((now - lastUIUpdateTime) > 100_000_000L) { //about every 100 milliseconds
                Platform.runLater(() -> playBackPos.set(TimeConverter.doubleToString((double) currentSample / sampleRate)));
                lastUIUpdateTime = now;
            }
        }
    }

    public int getCurrentSample() {
        return currentSample;
    }

    public void setPlaybackSample(int sample) {
        this.currentSample = Math.max(0, sample);
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

    public BooleanProperty getRunning() {
        return running;
    }

    public StringProperty getPlayBackPos() {
        return playBackPos;
    }
}
