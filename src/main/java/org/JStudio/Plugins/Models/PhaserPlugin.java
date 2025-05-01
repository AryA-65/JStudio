package org.JStudio.Plugins.Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Phaser plugin that takes in audio data and applies a phasing effect on it
 * @author Theodore Georgiou
 */
public class PhaserPlugin extends Plugin {
    private double frequency;
    private double wetDryFactor;
    private int deviation;
    private StringProperty name = new SimpleStringProperty("Phaser");

    // Creates a phaser
    public PhaserPlugin(double frequency, int deviation, double wetDryFactor) {
        convertAudioFileToByteArray();
        this.frequency = frequency;
        this.deviation = deviation;
        this.wetDryFactor = wetDryFactor;
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
            filteredAudio[i] = capMaxAmplitude(filteredAudio[i]);
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
            filteredArray[i] = capMaxAmplitude(filteredArray[i]);
        }
        return filteredArray;
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
            dryAudio[i] = capMaxAmplitude(dryAudio[i]);
        }
        return dryAudio;
    }
    
    /**
     * Wrapper class to set phaser effect
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
