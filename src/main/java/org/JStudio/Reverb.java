package org.JStudio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Reverb extends Application  {
    private String filePath;
    private ByteArrayInputStream byteArrayInputStream;
    private AudioInputStream audioInputStream;
    
    @Override
    public void start(Stage stage) throws Exception {
        try {
            filePath = "C:\\Users\\theog\\OneDrive\\Desktop\\30-06_load.wav"; // Use your own .wav file (44.1 kHz sample rate) to run
            Path path = Paths.get(filePath);
            byte[] audioData = Files.readAllBytes(path);
            
            //0.1 - 0.25 -> range for amplitude
            applyReverb(audioData, 0.25, 3, 0.8);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    /**
     * Applies reverb effects to audio data from a .wav file
     * @param audioData the original data contained in the .wav file
     * @param amplitudeFactor the factor by which the amplitude is changed (0.1 - 0.25)
     * @param decay the amount the audio decaying for
     * @param wetDryFactor the mix of original audio and reverbed audio
     */
    // Need to add pre delay (3 points later)
    private void applyReverb(byte[] audioData, double amplitudeFactor, int decay, double wetDryFactor) {
        byte[] reverbAudio = new byte[audioData.length];
        
        // reverbAudio has same audio data as the original audio for now
        for (int i = 0; i < audioData.length; i++) {
            reverbAudio[i] = audioData[i];
        }
        
        // Decay time is set (how long is the audio decaying for)
        for (int j = 0; j < decay; j++) {
            byte [] softenedAudio = amplitudeControlAudio(reverbAudio, amplitudeFactor);
        
            // Wet-Dry Mixing
            for (int i = 44; i < softenedAudio.length; i++) {
                reverbAudio[i] *= wetDryFactor; // Reverb hasn't been applied yet, therfore this is dry data
                softenedAudio[i] *= (1-wetDryFactor); // Wet data
            }
            
            // Adding previous reverbed points to current data point
            for (int i = 44; i < reverbAudio.length; i++) {
                reverbAudio[i] += softenedAudio[i-1]*decay/10;
            }
        }
        
        playAudio(reverbAudio);
    }
    
    /**
     * Controls the amplitude of the audio (volume)
     * @param audioData the data from the original file
     * @param amplitudeFactor the amount that the amplitude needs to be adjusted by
     * @param startIndex the index from which amplitude is adjusted
     * @param endIndex the index until which amplitude is adjusted
     * @return the adjusted amplitude audio
     */
    private byte[] amplitudeControlAudio(byte[] audioData, double amplitudeFactor) {
        byte[] volumeControlledAudio = new byte[audioData.length];
        for (int i=44; i < volumeControlledAudio.length; i++) {
            volumeControlledAudio[i] = audioData[i];
        }
            
        for (int i=44; i < volumeControlledAudio.length; i++) {
                volumeControlledAudio[i] = (byte) (volumeControlledAudio[i]*amplitudeFactor);
            }
        return volumeControlledAudio;
    }
    
    /**
     * Plays the audio from a byteArray containing audio data in the .wav format
     * @param audioData the audio that needs to be played
     */
    private void playAudio(byte[] audioData) {
        try {
            byteArrayInputStream = new ByteArrayInputStream(audioData);
            audioInputStream = AudioSystem.getAudioInputStream(byteArrayInputStream);
            
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println(e);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
