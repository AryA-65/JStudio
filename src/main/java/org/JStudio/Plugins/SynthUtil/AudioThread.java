package org.JStudio.Plugins.SynthUtil;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;

import java.util.function.Supplier;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

/**
 * Audio thread
 */
public class AudioThread extends Thread {
    public static final int SAMPLE_RATE = 44100; // or whatever sample rate you use
    public static final int BUFFER_SIZE = 512; // how many samples each buffer will contain. common usage in DAWs
    static final int BUFFER_COUNT = 8; // how many buffers will be in queue
    private final int[] buffers = new int[BUFFER_COUNT];
    private final long device = alcOpenDevice(alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER));
    private final long context = alcCreateContext(device, new int[1]); // context of the device
    private final int source;

    private int bufferIndex = 0;

    private boolean closed;
    private boolean running;

    private final Supplier<short[]> bufferSupplier;

    public boolean isRunning() {
        return running;
    }

    public AudioThread(Supplier<short[]> bufferSupplier) {
        this.bufferSupplier = bufferSupplier;
        alcMakeContextCurrent(context);
        AL.createCapabilities(ALC.createCapabilities(device));
        source = alGenSources();
        for (int i = 0; i < BUFFER_COUNT; i++) {
            bufferSamples(new short[0]);
        }
        alSourcePlay(source);
        catchInternalException();
        start();
    }

    @Override
    public synchronized void run() {
        while (!closed) {
            while (!running) {

                Utility.handleProcedure(this::wait, false); // more efficient than using a condition
                // while not running, sleep for 1 sec -> has to continuously loop
            }
            int processedBuffs = alGetSourcei(source, AL_BUFFERS_PROCESSED);
            for (int i = 0; i < processedBuffs; i++) {
                short[] samples = bufferSupplier.get();
                if (samples == null) {
                    running = false;
                    break;
                }
                alDeleteBuffers(alSourceUnqueueBuffers(source));
                buffers[bufferIndex] = alGenBuffers();
                bufferSamples(samples);
            }
            if (alGetSourcei(source, AL_SOURCE_STATE) != AL_PLAYING) {
                alSourcePlay(source);
            }
            catchInternalException();
        }
        alDeleteSources(source);
        alDeleteBuffers(buffers);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }

    /**
     * Starts playback
     */
    public synchronized void triggerPlayback() {
        running = true;
        notify();
    }

    /**
     * Closing actions
     */
    public void close() {
        closed = true; // break out of the loop
        triggerPlayback();

    }

    private void bufferSamples(short[] samples) {
        int buf = buffers[bufferIndex++];
        alBufferData(buf, AL_FORMAT_MONO16, samples, Utility.AudioInfo.SAMPLE_RATE);
        alSourceQueueBuffers(source, buf);
        bufferIndex %= BUFFER_COUNT; // Reset the bufferIndex
    }

    /**
     * Exception handler
     */
    private void catchInternalException() {
        int err = alcGetError(device);
        if (err != ALC_NO_ERROR) {
            throw new OpenALException(err);
        }
    }
}


