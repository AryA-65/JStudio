package org.JStudio.Plugins.Models;

import javax.sound.sampled.*;
import java.io.File;

public class audioAmplifier { //todo implement gui

    private void amplifyAudio(double amplitudeFactor, String source) {
        new Thread(() -> {
            try {
                File audioFile = new File(source);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioStream.getFormat();
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = audioStream.read(buffer)) != -1) { // read raw audio data into the buffer
                    for (int i = 0; i < bytesRead - 1; i += 2) { // loop through the buffer 2 bytes at a time
                        short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF)); // combines low and high byte(+1)

                        // Apply amplitude factor
                        sample = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, sample * amplitudeFactor));

                        // Convert back to bytes
                        buffer[i] = (byte) (sample & 0xFF);
                        buffer[i + 1] = (byte) ((sample >> 8) & 0xFF);
                    }

                    line.write(buffer, 0, bytesRead);
                }

                line.drain();
                line.close();
                audioStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
