package org.JStudio.Plugins.Models;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Echo plugin that takes in audio data and applies an echo effect to it
 * @author Theodore Georgiou
 */
public class Echo extends Plugin {
    private double decay;
    private double wetDryFactor;
    private int preDelay;
    private int echoNum;
    private int diffusion;
    private ArrayList<short[]> echos = new ArrayList<>();
    private StringProperty name = new SimpleStringProperty("Echo");

    // Creates an echo
    public Echo(int preDelay, double decay, int diffusion, int echoNum, double wetDryFactor) {
        this.preDelay = preDelay;
        this.decay = decay;
        this.diffusion = diffusion;
        this.echoNum = echoNum;
        this.wetDryFactor = wetDryFactor;
        convertAudioFileToByteArray();
    }
    
    /**
     * Applies the echo effect to the audio
     */
    private void applyEchoEffect() {
        echos = new ArrayList<>();
        short[] audioToEcho = convertToShortArray();
        
        int numOfEchos = echoNum;
        double decayValue = decay;
        // Loop repeats for the number of echos
        for (int echo = 0; echo < numOfEchos; echo++) {
            short[] loweredAudio = new short[audioToEcho.length];
            
            // lower the audio
            loweredAudio = lowerAudio(audioToEcho, decayValue);

            echos.add(loweredAudio);
            decayValue *= decay/2;
        }
        
        // Add echos with diffusion spacing
        short[] echoAudio = new short[audioToEcho.length+numOfEchos*diffusion];
        int echoCounter = 0;
        for (int i = 0; i < echos.size(); i++) {
            for (int j = 0; j < echos.get(i).length; j++) {
                echoAudio[j+(echoCounter*diffusion)] += echos.get(i)[j];
                echoAudio[j+(echoCounter*diffusion)] = capMaxAmplitude(echoAudio[j+(echoCounter*diffusion)]);
            }
            echoCounter++;
        }
        
        short[] mixedAudio = dryWetMixing(audioToEcho, echoAudio, audioToEcho.length, echoAudio.length);
        
        convertToByteArray(mixedAudio,((echoAudio.length+preDelay) * 2) + 44);
    }
    
    /**
     * Lowers the amplitude the audio over its duration
     * @param audioData the audio data to be lowered
     * @param amplitudeFactor the initial factor by which the audio will be lowered
     * @return the lowered audio data
     */
    private short[] lowerAudio(short[] audioData, double amplitudeFactor) {
        short[] loweredAudio = new short[audioData.length];
        System.arraycopy(audioData, 0, loweredAudio, 0, audioData.length);

        // Apply decay effect
        for (int i = 0; i < loweredAudio.length; i++) {
            loweredAudio[i] = (short) (loweredAudio[i] * amplitudeFactor);
            loweredAudio[i] = capMaxAmplitude(loweredAudio[i]);
        }
        return loweredAudio;
    }
    
    /**
     * Mixes the dry and wet audio data
     * @param dryAudio the original audio
     * @param wetAudio the modified audio
     * @param dryAudioSize the size of the original audio
     * @param wetAudioSize the size of the modified audio
     * @return the mix of original and modified audio
     */
    private short[] dryWetMixing(short[] dryAudio, short[] wetAudio, int dryAudioSize, int wetAudioSize) {
        // Dry Wet Mixing
        short[] dryAudioToMix = new short[dryAudioSize];
        short[] wetAudioToMix = new short[wetAudioSize];
        
        // Setup dry sound
        for (int i = 0; i < dryAudioToMix.length; i++) {
            dryAudioToMix[i] = (short) (dryAudio[i]*wetDryFactor);
        }
        
        // Setup wet sound
        for (int i = 0; i < wetAudio.length; i++) {
            wetAudioToMix[i] = (short) (wetAudio[i] * (1-wetDryFactor));
        }
        
        // Mix dry and wet
        short[] mixedAudio = new short[wetAudioToMix.length+preDelay];
        int wetPos = 0;
        for (int i = 0; i < mixedAudio.length; i++) {
            if (i<=preDelay) {
                mixedAudio[i] = dryAudioToMix[i];
            } else if (i>preDelay && i<dryAudioToMix.length) {
                mixedAudio[i] = (short) (dryAudioToMix[i] + wetAudioToMix[wetPos]);
                mixedAudio[i] = capMaxAmplitude(mixedAudio[i]);
                wetPos++;
            } else if (i>dryAudio.length){
                mixedAudio[i] = wetAudioToMix[wetPos];
                wetPos++;
            }
        }
        
        return mixedAudio;
    }

    // Wrapper class to set echo effect
    public void setEchoEffect() {
        applyEchoEffect();
    }
    
    /**
     * Assigns a value for decay
     * @param decay the value of decay to be assigned
     */
    public void setDecay(double decay) {
        this.decay = decay;
    }

    /**
     * Assigns a value for pre delay
     * @param preDelay the value of pre delay to be assigned
     */
    public void setPreDelay(int preDelay) {
        this.preDelay = preDelay;
    }

    /**
     * Assigns a value for the number of echos
     * @param echoNum the value of the number of echos to be assigned
     */
    public void setEchoNum(int echoNum) {
        this.echoNum = echoNum;
    }

    /**
     * Assigns a value for diffusion
     * @param diffusion the value of diffusion to be assigned
     */
    public void setDiffusion(int diffusion) {
        this.diffusion = diffusion;
    }

    /**
     * Assigns a value for the wet/dry audio ratio
     * @param wetDryFactor the ratio of wet/dry to be assigned
     */
    public void setWetDryFactor(double wetDryFactor) {
        this.wetDryFactor = wetDryFactor;
    }
}
