package org.JStudio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

/**
 * Reverb class that plays the audio (will be integrated to UI later)
 * @author Theodore Georgiou
 */
public class Reverb extends Application  {
    private String filePath;
    private ByteArrayInputStream byteArrayInputStream;
    private AudioInputStream audioInputStream;
    
    @Override
    public void start(Stage stage) throws Exception {
        try {
            filePath = "C:\\Users\\theog\\OneDrive\\Desktop\\beep-10.wav"; // Use your own .wav file (44.1 kHz sample rate or more) to run
            Path path = Paths.get(filePath);
            byte[] audioData = Files.readAllBytes(path);
            
            applyReverb(audioData, 0.5, 2, 1);
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
    // Need to add pre delay
    private void applyReverb(byte[] audioData, double amplitudeFactor, int decay, double wetDryFactor) {
        byte[] audioToReverb = new byte[audioData.length-44];
        
        // reverbAudio has same audio data as the original audio for now
        for (int i = 0; i < audioToReverb.length; i++) {
            audioToReverb[i] = audioData[i+44];
        }
        
        // Convert audio data to short type to avoid audio warping
        short[] reverbNums = new short[audioToReverb.length/2];
        for (int i = 0; i < reverbNums.length; i++) {
            reverbNums[i]=ByteBuffer.wrap(audioToReverb,i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort(); // // i*2 since each short is 2 bytes long
        }

        // Decay time is set (how long is the audio decaying for)
        for (int j = 0; j < decay; j++) {
            short[] decayedAudio = amplitudeControlAudio(reverbNums, amplitudeFactor);
        
            // Wet-Dry Mixing
            for (int i = 0; i < decayedAudio.length; i++) {
                short dryAudio = (short) (reverbNums[i]*wetDryFactor);
                short wetAudio = (short) (decayedAudio[i]*(1-wetDryFactor));
                reverbNums[i] = (short) (dryAudio+wetAudio);
            }
           
            
            // Adding previous reverbed points to current data point
            for (int i = 1; i < reverbNums.length; i++) {
                reverbNums[i] += (short) decayedAudio[i-1]*decay/10;
            }
        }
        
        // Revert back to byte array to have playback functionality
        byte[] reverbedAudio = new byte[reverbNums.length*2];
        for (int i = 0; i < reverbNums.length; i++) {
            ByteBuffer.wrap(reverbedAudio, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(reverbNums[i]); // i*2 since each short is 2 bytes long
        }
        
        byte[] finalAudio = new byte[audioData.length];
        System.arraycopy(reverbedAudio, 0, finalAudio, 44, reverbNums.length*2); // Add the audio data
        System.arraycopy(audioData, 0, finalAudio, 0, 44); // Add the header
        
        playAudio(finalAudio);
    }
    
    /**
     * Controls the amplitude of the audio (volume)
     * @param audioData the data from the original file
     * @param amplitudeFactor the amount that the amplitude needs to be adjusted by
     * @param startIndex the index from which amplitude is adjusted
     * @param endIndex the index until which amplitude is adjusted
     * @return the adjusted amplitude audio
     */
    private short[] amplitudeControlAudio(short[] audioData, double amplitudeFactor) {
        short[] volumeControlledAudio = new short[audioData.length];
        for (int i=0; i < volumeControlledAudio.length; i++) {
            volumeControlledAudio[i] = audioData[i];
        }
            
        for (int i=0; i < volumeControlledAudio.length; i++) {
                volumeControlledAudio[i] = (short) (volumeControlledAudio[i]*amplitudeFactor);
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
