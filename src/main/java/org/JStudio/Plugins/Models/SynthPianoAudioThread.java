package org.JStudio.Plugins.Models;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import java.util.function.Supplier;
import org.JStudio.Plugins.Synthesizer.OpenALException;
import org.JStudio.Plugins.Synthesizer.Utility;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

public class SynthPianoAudioThread extends Thread {
    static final int BUFFER_SIZE = 512; // how many samples each buffer will contain. common usage in DAWs
    static final int BUFFER_COUNT = 8; // how many buffers will be in queue
    private final int[] buffers = new int[BUFFER_COUNT];
    private final int source;
    private int bufferIndex = 0;

    private final long device = alcOpenDevice(alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER));
    private final long context = alcCreateContext(device, new int[1]); // context of the device

    private boolean closed;
    private boolean running;

    private final Supplier<short[]> bufferSupplier;
    
    public static int getBufferSize(){
        return BUFFER_SIZE;
    }
    
    public boolean isClosed(){
        return closed;
    }

    //adds samples to a buffer, queues the buffer, and starts playing the audio
    public SynthPianoAudioThread(Supplier<short[]> bufferSupplier) {
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
        while (!closed) { //loop until stopped
            while (!running) {
                Utility.handleProcedure(this::wait, false); // more efficient than using a condition
                // while not running, sleep for 1 sec -> has to continuously loop
            }
            //gets all samples and plays them
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

    //breaks out of the loop and "runs" the thread
    public synchronized void triggerPlayback() {
        running = true;
        notify();
    }
    
    //makes the thread enter the loop again to wait
    public void pause(){
        running = false;
    }

    //breaks out of the loop and closes the thread
    public synchronized void close() {
        closed = true;
        running = true;
        notify();
    }

    //Queues samples in a buffer
    private void bufferSamples(short[] samples) {
        int buf = buffers[bufferIndex++];
        alBufferData(buf, AL_FORMAT_MONO16, samples, Utility.AudioInfo.SAMPLE_RATE);
        alSourceQueueBuffers(source, buf);
        bufferIndex %= BUFFER_COUNT; // Reset the bufferIndex
    }

    //cathes any errors with the OpenAL library
    private void catchInternalException() {
        int err = alcGetError(device);
        if (err != ALC_NO_ERROR) {
            throw new OpenALException(err);
        }
    }
}


