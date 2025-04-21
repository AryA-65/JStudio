package org.JStudio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Buffer that stores audio clips to allow for sequenced playback
 * @author Theodore Georgiou
 */
public class AudioPlaybackBuffer {
    private ArrayList<byte[]> clips = new ArrayList<>();
    private ArrayList<String> filePathNames = new ArrayList<>();
    private SourceDataLine line;

    public AudioPlaybackBuffer() {
    }
    
    /**
     * Adds a clip to the playback list
     * @param clip the clip to add
     * @param filePathName the name of the file
     */
    public void addClip(byte[] clip, String filePathName) {
        clips.add(clip);
        filePathNames.add(filePathName);
    }
    
    /**
     * Plays the sequence of audio clips
     */
    public void playAudio() {
        new Thread(() -> {
            try {
                for (int i = 0; i < clips.size(); i++) {
                File file = new File(filePathNames.get(i));
                byte[] audioData = clips.get(i);
                
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat audioFormat = audioInputStream.getFormat();
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(audioFormat);
                line.start();

                line.write(audioData, 0, audioData.length);

                line.drain();
                line.close();
                }
                
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                System.out.println(e);
            }
        }).start();
    }
}
