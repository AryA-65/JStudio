package org.JStudio.Plugins.Models;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Modulation plugin that takes in audio data and applies a flanging or chorus effect on it
 * @author Theodore Georgiou
 */
public class ModulationPlugin {
    private String filePathName;
    private byte[] originalAudio;
    private byte[] finalAudio;
    private ArrayList<Integer> delays = new ArrayList<>();
    private double frequency;
    private double wetDryFactor;
    private int deviation;
    private SourceDataLine line;

    // Creates a modulator
    public ModulationPlugin(double frequency, int deviation, double wetDryFactor) {
        convertAudioFileToByteArray();
        this.frequency = frequency;
        this.deviation = deviation;
        this.wetDryFactor = wetDryFactor;
    }

    /**
     * Converts audio data from a wav file to a byte array
     */
    private void convertAudioFileToByteArray() {
        try {
            File file = new FileChooser().showOpenDialog(null);
            filePathName = file.getAbsolutePath();
            originalAudio = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    /**
     * Applies modulation effect to audio data
     */
    private void applyModulationEffect() {
        short[] audioToModulate = convertToShortArray();
        // Sets all the delays
        calculateDelayTime(audioToModulate);
        
        short[] modulatedAudio = new short[audioToModulate.length];

        // Original audio
        for (int i = 0; i < modulatedAudio.length; i++) {
            modulatedAudio[i] = (short) (audioToModulate[i] * wetDryFactor);
        }

        // Add delayed audio to original audio
        for (int i = 0; i < delays.size(); i++) {
            if (i+delays.get(i) < audioToModulate.length) {
                modulatedAudio[i] += audioToModulate[i+delays.get(i)]*(1-wetDryFactor);
            }
            if (modulatedAudio[i] > Short.MAX_VALUE) {
                modulatedAudio[i] = Short.MAX_VALUE;
            } else if (modulatedAudio[i] < Short.MIN_VALUE) {
                modulatedAudio[i] = Short.MIN_VALUE;
            }
        }

        convertToByteArray(modulatedAudio, modulatedAudio.length * 2);
    }
    
    /**
     * Calculates all oscillating delays for the audio and stores them in an ArrayList
     * @param audioData the audio data that will be used
     */
    private void calculateDelayTime(short[] audioData) {
        for (int i = 0; i < audioData.length; i++) {
            double delayTrig = Math.sin(2*Math.PI * frequency/500000 * i/44100) ; // Will be changed to match other sample rates
            int delay = (int) (delayTrig  * deviation);
            delays.add(delay);
        }
    }
    
    /**
     * Converts the original audio data to a short array to allow for modifications
     * @return the short[] audio data array
     */
    private short[] convertToShortArray() {
        byte[] noHeaderByteAudioData = new byte[originalAudio.length - 44];

        // The audio to add flanging to has same audio data as the original audio for now (no header)
        for (int i = 0; i < noHeaderByteAudioData.length; i++) {
            noHeaderByteAudioData[i] = originalAudio[i + 44];
        }

        // Convert audio data to short type to avoid audio warping
        short[] audioToModulate = new short[noHeaderByteAudioData.length / 2];
        for (int i = 0; i < audioToModulate.length; i++) {
            audioToModulate[i] = ByteBuffer.wrap(noHeaderByteAudioData, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort(); // // i*2 since each short is 2 bytes long
        }
        
        return audioToModulate;
    }
    
    /**
     * Revert short[] audio data back to byte array to have playback functionality
     * @param audioData the audio data to be converted to a byte array
     */
    private void convertToByteArray(short[] audioData, int sizeOfByteArray) {
        // Revert back to byte array to have playback functionality
        byte[] modifiedAudio = new byte[sizeOfByteArray];
        for (int i = 0; i < audioData.length; i++) {
                ByteBuffer.wrap(modifiedAudio, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(audioData[i]); // i*2 since each short is 2 bytes long
            }
        
        finalAudio = new byte[sizeOfByteArray + 44];
        System.arraycopy(modifiedAudio, 0, finalAudio, 44, sizeOfByteArray); // Add the audio data
        System.arraycopy(originalAudio, 0, finalAudio, 0, 44); // Add the header
        playAudio(finalAudio);
    }
    
    /**
     * Plays audio data stored in a byte array
     * @param audioData the audio data to be played
     */
    private void playAudio(byte[] audioData) {
        new Thread(() -> {
            try {
                File file = new File(filePathName);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat audioFormat = audioInputStream.getFormat();
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(audioFormat);
                line.start();

                line.write(audioData, 0, audioData.length);

                line.drain();
                line.close();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                System.out.println(e);
            }
        }).start();
    }
    
    /**
     * Stops audio playback
     */
    public void stopAudio() {
        line.close();
    }
    
    /**
     * Wrapper class to set modulation effect
     */
    public void setModulationEffect() {
        applyModulationEffect();
    }

    /**
     * Assigns a value for frequency
     * @param frequency the value of frequency to be assigned
     */
    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    /**
    * Assigns a value for frequency
    * @param deviation the value of deviation to be assigned
    */
    public void setDeviation(int deviation) {
        this.deviation = deviation;
    }
    
    /**
    * Assigns a value for frequency
    * @param wetDryFactor the value of wet/dry ratio to be assigned
    */
    public void setWetDryFactor(double wetDryFactor) {
        this.wetDryFactor = wetDryFactor;
    }
}
