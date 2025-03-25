package org.JStudio.Utils;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class WavAudioPlayer {
    WavData waveData = new WavData();

    public WavAudioPlayer(WavData waveData) {
        this.waveData = waveData;
    }

    public void playAudio() throws Exception {
        byte[] data = waveData.getBytes(WaveSection.DATA);

        // Create an audio input stream from byte array
        AudioFormat audioFormat = waveData.createAudioFormat();
        InputStream byteArrayInputStream = new ByteArrayInputStream(data);
        AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream,
                audioFormat, data.length / audioFormat.getFrameSize());

        // Write audio input stream to speaker source data line
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,
                audioFormat);
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();

        // Loop through input stream to write to source data line
        byte[] tempBuffer = new byte[10000];
        int cnt;
        while ((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
            sourceDataLine.write(tempBuffer, 0, cnt);
        }

        // Cleanup
        sourceDataLine.drain();
        sourceDataLine.close();
        byteArrayInputStream.close();
    }
}
