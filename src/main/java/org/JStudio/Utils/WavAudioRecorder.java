package org.JStudio.Utils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;

public class WavAudioRecorder implements Runnable {
    WavData waveData = new WavData();
    boolean recording = true;
    Thread runningThread;
    ByteArrayOutputStream byteArrayOutputStream;

    public WavAudioRecorder(WavData waveData) {
        this.waveData = waveData;
    }

    public void startRecording() {
        this.recording = true;
        this.runningThread = new Thread(this);
        runningThread.start();
    }

    public WavData stopRecording() throws Exception {
        this.recording = false;
        runningThread.stop();

        waveData.put(WaveSection.DATA, byteArrayOutputStream.toByteArray());

        return waveData;
    }

    public void run() {
        try {
            // Create an audio output stream for byte array
            byteArrayOutputStream = new ByteArrayOutputStream();

            // Write audio input stream to speaker source data line
            AudioFormat audioFormat = waveData.createAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            // Loop through target data line to write to output stream
            int numBytesRead;
            byte[] data = new byte[targetDataLine.getBufferSize() / 5];
            while (recording) {
                numBytesRead = targetDataLine.read(data, 0, data.length);
                byteArrayOutputStream.write(data, 0, numBytesRead);
            }

            // Cleanup
            targetDataLine.stop();
            targetDataLine.close();
            byteArrayOutputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
