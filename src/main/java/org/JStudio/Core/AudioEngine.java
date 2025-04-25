package org.JStudio.Core;

//import javazoom.jl.player.AudioDevice;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AudioEngine {
    private final Mixer mixer = new Mixer();
    private final List<Track> tracks = new ArrayList<>();
    private final ExecutorService workerThreads = Executors.newFixedThreadPool(4);
    private final AudioOutputDevice outputDevice;

    private volatile boolean isRunning = false;
    private final int sampleRate = 44100; // Standard audio rate, as used in LMMS
    private final short bufferSize = 1024;  // Common buffer size for real-time audio

    public AudioEngine() {
        this.outputDevice = new AudioOutputDevice(sampleRate, bufferSize);
    }

    public void start() {
        isRunning = true;
        outputDevice.open();
        new Thread(this::renderLoop, "Audio Render Thread").start();
    }

    public void stop() {
        isRunning = false;
        workerThreads.shutdownNow();
        outputDevice.close();
    }

    private void renderLoop() {
        while (isRunning) {
            float[][] buffer = new float[2][bufferSize]; // Stereo buffer (left, right)
            processAudio(buffer);
            outputDevice.write(buffer);
        }
    }

    private void processAudio(float[][] output) {
        // Process tracks in parallel, similar to LMMS's track processing
        List<Future<?>> jobs = new ArrayList<>();
        for (Track track : tracks) {
//            jobs.add(workerThreads.submit(() -> track.process(bufferSize)));
        }

        // Wait for all tracks to complete processing
        for (Future<?> job : jobs) {
            try {
                job.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Mix track outputs into the final buffer, like LMMS's mixer
        mixer.process(tracks, output, bufferSize);
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
}

class AudioOutputDevice {
    private final int sampleRate;
    private final int bufferSize;
    private SourceDataLine line;
    private final AudioFormat format;

    public AudioOutputDevice(int sampleRate, int bufferSize) {
        this.sampleRate = sampleRate;
        this.bufferSize = bufferSize;
        // 16-bit PCM, stereo, 44.1kHz, as commonly used in LMMS
        this.format = new AudioFormat(sampleRate, 16, 2, true, false);
    }

    public void open() {
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, bufferSize * 4); // Buffer size in bytes (2 channels * 2 bytes)
            line.start();
        } catch (LineUnavailableException e) {
            throw new RuntimeException("Failed to open audio line", e);
        }
    }

    public void write(float[][] buffer) {
        // Convert float buffer [-1.0, 1.0] to 16-bit PCM
        byte[] pcmData = new byte[bufferSize * 4]; // 2 channels * 2 bytes per sample
        ByteBuffer byteBuffer = ByteBuffer.wrap(pcmData).order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < bufferSize; i++) {
            // Clip and scale to 16-bit range
            short left = (short) (Math.max(-1.0f, Math.min(1.0f, buffer[0][i])) * 32767);
            short right = (short) (Math.max(-1.0f, Math.min(1.0f, buffer[1][i])) * 32767);
            byteBuffer.putShort(left);
            byteBuffer.putShort(right);
        }

        line.write(pcmData, 0, pcmData.length);
    }

    public void close() {
        if (line != null) {
            line.drain();
            line.close();
        }
    }
}