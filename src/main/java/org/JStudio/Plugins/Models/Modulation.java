package org.JStudio.Plugins.Models;

import java.util.ArrayList;

/**
 * Modulation plugin that takes in audio data and applies a flanging or chorus effect on it
 * @author Theodore Georgiou
 */
public class Modulation extends Plugin{
    private ArrayList<Integer> delays = new ArrayList<>();
    private double frequency;
    private double wetDryFactor;
    private int deviation;

    // Creates a modulator
    public Modulation(double frequency, int deviation, double wetDryFactor) {
        convertAudioFileToByteArray();
        this.frequency = frequency;
        this.deviation = deviation;
        this.wetDryFactor = wetDryFactor;
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
                modulatedAudio[i] = capMaxAmplitude(modulatedAudio[i]);
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
