package org.JStudio.Plugins.Models;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import javafx.stage.FileChooser;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Phaser plugin that takes in audio data and applies a phasing effect on it
 * @author Theodore Georgiou
 */
public class PhaserPlugin {
    private String fileName;
    private String filePathName;
    private byte[] originalAudio;
    private byte[] finalAudio;
    private double frequency;
    private double wetDryFactor;
    private int deviation;

    // Creates a phaser
    public PhaserPlugin(double frequency, int deviation, double wetDryFactor) {
        convertAudioFileToByteArray();
        this.frequency = frequency;
        this.deviation = deviation;
        this.wetDryFactor = wetDryFactor;
        fileName = "\\jumpland.wav"; // Temporary value for now (will have file setting functionality later)
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
     * Applies phaser effect to audio data
     */
    private void applyPhaserEffect() {
        short[] audioToPhase = convertToShortArray();
        short[] phasedAudio = new short[audioToPhase.length];

        // Original audio
        for (int i = 0; i < phasedAudio.length; i++) {
            phasedAudio[i] = (short) (audioToPhase[i] * wetDryFactor);
        }

        short[] filteredAudio1 = allPassFilter(phasedAudio);
        short[] filteredAudio2 = allPassFilter(filteredAudio1);
        short[] filteredAudio3 = allPassFilter(filteredAudio2);
        short[] filteredAudio4 = allPassFilter(filteredAudio3);
        
        short[] filteredAudio = new short[filteredAudio4.length];
        // Mix all pass filters
        for (int i = 0; i < filteredAudio.length; i++) {
            filteredAudio[i] = (short) ((filteredAudio1[i] + filteredAudio2[i] + filteredAudio3[i] + filteredAudio4[i]) * (1-wetDryFactor));
        }
       
        phasedAudio = dryWetMixing(phasedAudio, filteredAudio);
        convertToByteArray(phasedAudio, phasedAudio.length * 2);
    }
    
    /**
     * All pass filtering of the audio  data
     * @param audioData the audio data to be filtered
     * @return the filtered audio data
     */
    private short[] allPassFilter(short[] audioData) {
        short[] filteredArray = new short[audioData.length];
        filteredArray[0]  = audioData[0];
        for (int i = 1; i < filteredArray.length; i++) {
            double phaseOscillation = Math.sin(2*Math.PI * frequency/200 * i/44100); // Will be changed to match other sample rates
            int phaseShift = (int) (phaseOscillation*deviation/5);
            filteredArray[i] = (short) (phaseShift*audioData[i] + audioData[i-1] - phaseShift*filteredArray[i-1]);
        }
        return filteredArray;
    }
    
    /**
     * Converts the original audio data to a short array to allow for modifications
     * @return the short[] audio data array
     */
    private short[] convertToShortArray() {
         byte[] noHeaderByteAudioData = new byte[originalAudio.length - 44];

        // The audio to add phasing to has same audio data as the original audio for now (no header)
        for (int i = 0; i < noHeaderByteAudioData.length; i++) {
            noHeaderByteAudioData[i] = originalAudio[i + 44];
        }

        // Convert audio data to short type to avoid audio warping
        short[] audioToPhase = new short[noHeaderByteAudioData.length / 2];
        for (int i = 0; i < audioToPhase.length; i++) {
            audioToPhase[i] = ByteBuffer.wrap(noHeaderByteAudioData, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort(); // // i*2 since each short is 2 bytes long
        }
        return audioToPhase;
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
     * Mixes the dry and wet audio data
     * @param dryAudio the original audio
     * @param wetAudio the modified audio
     * @return the mix of original and modified audio
     */
    private short[] dryWetMixing(short[] dryAudio, short[] wetAudio) {
        // Mix phase shifted audio and original audio
        for (int i = 0; i < dryAudio.length; i++) {
            dryAudio[i] += wetAudio[i]*(1-wetDryFactor);
            if (dryAudio[i] > Short.MAX_VALUE) {
                dryAudio[i] = Short.MAX_VALUE;
            } else if (dryAudio[i] < Short.MIN_VALUE) {
                dryAudio[i] = Short.MIN_VALUE;
            }
        }
        return dryAudio;
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
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
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
     * Wrapper class to set flanger effect
     */
    public void setPhaserEffect() {
        applyPhaserEffect();
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
