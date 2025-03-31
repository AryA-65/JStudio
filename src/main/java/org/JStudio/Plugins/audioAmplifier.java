package org.JStudio.Plugins;

import javax.sound.sampled.*;
import java.io.File;

public class audioAmplifier {

    private double[] audioData;
    private File audioFile;

    private void playAudio(double amplitudeFactor) {
        new Thread(() -> {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioStream.getFormat();
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = audioStream.read(buffer)) != -1) {
                    for (int i = 0; i < bytesRead - 1; i += 2) {
                        // Convert bytes to sample
                        short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF));

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
