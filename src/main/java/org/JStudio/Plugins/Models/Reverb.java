package org.JStudio.Plugins.Models;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Reverb plugin that takes in audio data and applies a reverb effect to it
 * @author Theodore Georgiou
 */
public class Reverb extends Plugin {
    private double decay;
    private double wetDryFactor;
    private int preDelay;
    private int diffusion;
    private ArrayList<short[]> delayLines = new ArrayList<>();
    private StringProperty name = new SimpleStringProperty("Reverb");

    // Creates a reverb
    public Reverb(int preDelay, int decay, int diffusion, double wetDryFactor) {
        this.preDelay = preDelay;
        this.decay = decay;
        this.diffusion = diffusion;
        this.wetDryFactor = wetDryFactor;
        convertAudioFileToByteArray();
        delayLines = new ArrayList<>();
    }

    /**
     * Applies the reverb effect to the audio data array
     */
    private void applyReverbEffect() {
        byte[][] byteReverb = new byte[2][audioByteInput2D[0].length*4];
        for (int i = 0; i < audioByteInput2D.length; i++) {
            delayLines = new ArrayList<>();
       
            short[] audioToReverb = convertToShortArray(audioByteInput2D[i]);
            int numOfDelayLines = 0;
            if (audioByteInput2D[i].length < 200000) {
                numOfDelayLines =  5;
            } else {
                numOfDelayLines =  20;
            }


            double decayNumber;
            double initialDecay = decay/35000;
            // Loop repeats for the number of delay lines
            for (int delayLine = 0; delayLine < numOfDelayLines; delayLine++) {
                decayNumber = initialDecay*Math.pow(Math.E, - (decay/100000));
                initialDecay = decayNumber;

                // Decay the audio
                short[] decayedAudio = decayAudio(audioToReverb, decayNumber);

                delayLines.add(decayedAudio);
            }

            // Add delay lines with diffusion spacing
            short[] delayLineAudio = new short[audioToReverb.length+numOfDelayLines*diffusion];
            int delayLineCounter = 0;
            for (int k = 0; k < delayLines.size(); k++) {
                for (int j = 0; j < delayLines.get(k).length; j++) {
                    delayLineAudio[j+(delayLineCounter*diffusion)] += delayLines.get(k)[j];
                    delayLineAudio[j+(delayLineCounter*diffusion)] = capMaxAmplitude(delayLineAudio[j+(delayLineCounter*diffusion)]);
                }
                delayLineCounter++;
            }

            short[] mixedAudio = dryWetMixing(audioToReverb, delayLineAudio, audioToReverb.length, delayLineAudio.length);
            byte[] byteData = convertToByteArray(mixedAudio, (delayLineAudio.length+preDelay) * 2);
            byteReverb[i] = byteData;
        }
        
        audioOutput2D = convert2DByteTo2DFloat(byteReverb);
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
    
    /**
     * Decays the audio over its duration
     * @param audioData the audio data to be decayed
     * @param amplitudeFactor the initial factor by which the audio will be decayed
     * @return the decayed audio data
     */
    private short[] decayAudio(short[] audioData, double amplitudeFactor) {
        short[] volumeControlledAudio = new short[audioData.length];
        System.arraycopy(audioData, 0, volumeControlledAudio, 0, audioData.length);

        // Apply decay effect
        for (int i = 0; i < volumeControlledAudio.length; i++) {
            volumeControlledAudio[i] = (short) (volumeControlledAudio[i] * amplitudeFactor);
            volumeControlledAudio[i] = capMaxAmplitude(volumeControlledAudio[i]);
        }
        return volumeControlledAudio;
    }
    
    // Wrapper class to set reverb effect
    public void setReverbEffect() {
        applyReverbEffect();
    }
    
    /**
     * Assigns a value for decay
     * @param decay the value of decay to be assigned
     */
    public void setDecay(double decay) {
        this.decay = decay;
    }

    /**
     * Assigns a value for the wet/dry audio ratio
     * @param wetDryFactor the ratio of wet/dry to be assigned
     */
    public void setWetDryFactor(double wetDryFactor) {
        this.wetDryFactor = wetDryFactor;
    }

    /**
     * Assigns a value for pre delay
     * @param preDelay the value of pre delay to be assigned
     */
    public void setPreDelay(int preDelay) {
        this.preDelay = preDelay;
    }

    /**
     * Assigns a value for diffusion
     * @param diffusion the value of diffusion to be assigned
     */
    public void setDiffusion(int diffusion) {
        this.diffusion = diffusion;
    }
}
