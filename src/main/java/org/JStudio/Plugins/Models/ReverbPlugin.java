package org.JStudio.Plugins.Models;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import javafx.stage.FileChooser;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Reverb plugin that takes in audio data and applies a reverb effect to it
 * @author Theodore Georgiou
 */
public class ReverbPlugin {
    private String filePathName;
    private double decay;
    private double wetDryFactor;
    private int preDelay;
    private int diffusion;
    private byte[] originalAudio;
    private byte[] finalAudio;
    private ArrayList<short[]> delayLines = new ArrayList<>();
    private SourceDataLine line;

    // Creates a reverb
    public ReverbPlugin(int preDelay, int decay, int diffusion, double wetDryFactor) {
        this.preDelay = preDelay;
        this.decay = decay;
        this.diffusion = diffusion;
        this.wetDryFactor = wetDryFactor;
        convertAudioFileToByteArray();
        delayLines = new ArrayList<>();
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
     * Applies the reverb effect to the audio data array
     */
    private void applyReverbEffect() {
        delayLines = new ArrayList<>();
       
        short[] audioToReverb = convertToShortArray();
        
        int numOfDelayLines =  20;
        
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
        for (int i = 0; i < delayLines.size(); i++) {
            for (int j = 0; j < delayLines.get(i).length; j++) {
                delayLineAudio[j+(delayLineCounter*diffusion)] += delayLines.get(i)[j];
                delayLineAudio[j+(delayLineCounter*diffusion)] = capMaxAmplitude(delayLineAudio[j+(delayLineCounter*diffusion)]);
            }
            delayLineCounter++;
        }
        
        short[] mixedAudio = dryWetMixing(audioToReverb, delayLineAudio, audioToReverb.length, delayLineAudio.length);
        
        convertToByteArray(mixedAudio, (delayLineAudio.length+preDelay) * 2);
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

    /**
     * Caps the amplitude of a sample from exceeding the maximum value of a short
     * @param sample the sample to be capped
     * @return the capped sample
     */
    private short capMaxAmplitude(short sample) {
        if (sample > Short.MAX_VALUE) {
                sample = Short.MAX_VALUE;
        } else if (sample < Short.MIN_VALUE) {
            sample = Short.MIN_VALUE;
        }
        return sample;
    }
    
    /**
     * Converts the original audio data to a short array to allow for modifications
     * @return the short[] audio data array
     */
    private short[] convertToShortArray() {
        byte[] noHeaderByteAudioData = new byte[originalAudio.length - 44];
        // The audio to reverb has same audio data as the original audio for now (no header)
        System.arraycopy(originalAudio, 44, noHeaderByteAudioData, 0, originalAudio.length - 44);
        
        // Convert audio data to short type to avoid audio warping
        short[] audioToReverb = new short[noHeaderByteAudioData.length / 2];
        for (int i = 0; i < audioToReverb.length; i++) {
            audioToReverb[i] = ByteBuffer.wrap(noHeaderByteAudioData, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort(); // // i*2 since each short is 2 bytes long
        }
        
        return audioToReverb;
    }
    
    /**
     * Revert short[] audio data back to byte array to have playback functionality
     * @param audioData the audio data to be converted to a byte array
     */
    private void convertToByteArray(short[] audioData, int sizeOfByteArray) {
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

                int frameSize = line.getFormat().getFrameSize();
                int trimmedLength = (audioData.length / frameSize) * frameSize;
                line.write(audioData, 0, trimmedLength);

                line.drain();
                line.close();
                delayLines = null;
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
