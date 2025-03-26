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
    
    @Override
    public void start(Stage stage) throws Exception {
        try {
            filePath = "C:\\Users\\theog\\OneDrive\\Desktop\\Misc Lasers.wav"; // Use your own .wav file to run (use one of sizeable length for now)
            Path path = Paths.get(filePath);
            byte[] audioData = Files.readAllBytes(path);

            
//            // Test where the audio lowers in volum towards the end (Ignore this)
//            byte[] mainAudio =  amplitudeControlAudio(audioData, 0, audioData.length/2, audioData.length);
//            
//            // Note: Decay Time would be how quick the amplitude Factor would decrease (to be implemented later)
//            byte[] reverbedAudio = amplitudeControlAudio(audioData, 0, 100, audioData.length/2);
//            reverbedAudio = amplitudeControlAudio(audioData, 0.3, audioData.length/2, audioData.length/2 + audioData.length/4);
//            reverbedAudio = amplitudeControlAudio(audioData, 0.1, audioData.length/2 + audioData.length/2, audioData.length);
//            playAudio(mainAudio);
//            playAudio(reverbedAudio);
            
            byte[] mainAudio = decayAudio(audioData, 0.1);
            playAudio(mainAudio);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    /**
     * Adjusts the audio progressively
     * @param audioData the audio data to lower
     * @param decayFactor the factor with which the audio is adjusted
     * @return the adjusted audio
     */
    private byte[] decayAudio(byte[] audioData, double decayFactor) {
        byte[] decayedAudio = new byte[audioData.length];
        for (int i = 0; i < decayedAudio.length; i++) {
            decayedAudio[i] = audioData[i];
        }
        
        int currentAmplitude = 1;
        int startPosition = 100;
        int endPosition = decayedAudio.length;
        for (int i = 0; i < 20; i++) {
            decayedAudio = amplitudeControlAudio(audioData, currentAmplitude*decayFactor, startPosition, endPosition);
            startPosition+=20000;
        }
        
        return decayedAudio;
    }
    
    /**
     * Trims the beginning of the audio file (Keeps the header)
     * @param audioData the data from the original file
     * @param trimFactor the amount the file needs to be trimmed
     * @return the trimmed audio data
     */
    private byte[] trimAudio(byte[] audioData, double trimFactor) {
        // Cutting off a part of the audio (beginning)
        // *Note: Still plays silence in the beginning, needs to skip to first sound found, to be implemented later
        int cutAudioSize = (int) (audioData.length - audioData.length*trimFactor);
        byte[] trimmedAudio = new byte[audioData.length];

        for (int i = 0; i < audioData.length; i++) {
            if (i <= 100) {
                trimmedAudio[i] = audioData[i];
            } else if (i >= cutAudioSize) {
                trimmedAudio[i] = audioData[i];
            }
        }
        return trimmedAudio;
    }
    
    /**
     * Controls the amplitude of the audio (volume)
     * @param audioData the data from the original file
     * @param amplitudeFactor the amount that the amplitude needs to be adjusted by
     * @param startIndex the index from which amplitude is adjusted
     * @param endIndex the index until which amplitude is adjusted
     * @return the adjusted amplitude audio
     */
    private byte[] amplitudeControlAudio(byte[] audioData, double amplitudeFactor, int startIndex, int endIndex) {
        byte[] volumeControlledAudio = new byte[audioData.length];
            for (int i = 0; i < volumeControlledAudio.length; i++) {
                volumeControlledAudio[i] = audioData[i];
            }
        for (int i=startIndex; i < endIndex; i++) {
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
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(byteArrayInputStream);

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
