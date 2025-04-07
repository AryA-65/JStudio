package org.JStudio.Plugins.Models;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    private String fileName;
    private String filePathName;
    private double decay;
    private double wetDryFactor;
    private int preDelay;
    private int diffusion;
    private byte[] originalAudio;
    private ArrayList<short[]> delayLines = new ArrayList<>();

    // Creates a reverb
    public ReverbPlugin(int preDelay, int decay, int diffusion, double wetDryFactor) {
        this.preDelay = preDelay;
        this.decay = decay;
        this.diffusion = diffusion;
        this.wetDryFactor = wetDryFactor;
        convertAudioFileToByteArray();
        delayLines = new ArrayList<>();
        fileName = "\\jumpland.wav"; // Temporary value for now (will have file setting functionality later)
    }

    /**
     * Converts audio data from a wav file to a byte array
     */
    private void convertAudioFileToByteArray() {
        try {
            filePathName = Paths.get(System.getProperty("user.home"), "Downloads") + fileName;
            fileName = "\\laser13.wav"; // Use your own .wav file (44.1 kHz sample rate) to run
            String filePath = Paths.get(System.getProperty("user.home"), "Downloads") + fileName;
            Path path = Paths.get(filePath);
            originalAudio = Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Applies the reverb effect to the audio data array
     */
    private void applyReverbEffect() {
        convertAudioFileToByteArray();
        delayLines = new ArrayList<>();
        byte[] audioToReverb = new byte[originalAudio.length - 44];

        // The audio to reverb has same audio data as the original audio for now (no header)
        for (int i = 0; i < audioToReverb.length; i++) {
            audioToReverb[i] = originalAudio[i + 44];
        }

        // Convert audio data to short type to avoid audio warping
        short[] reverbNums = new short[audioToReverb.length / 2];
        for (int i = 0; i < reverbNums.length; i++) {
            reverbNums[i] = ByteBuffer.wrap(audioToReverb, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort(); // // i*2 since each short is 2 bytes long
        }

        int numOfDelayLines;
        if (decay/10000 >= 5) {
            numOfDelayLines = (int) (decay/20000);
        } else {
            numOfDelayLines = (int) (decay/10000);
        }
        
        double decayNumber;
        double initialDecay = decay/1000;
        // Loop repeats for the number of delay lines
        for (int delayLine = 0; delayLine < numOfDelayLines; delayLine++) {
            decayNumber = initialDecay*Math.pow(Math.E, - (decay/1000));
            short[] decayedAudio = new short[reverbNums.length];
            
            // Decay the audio
            decayedAudio = decayAudio(reverbNums, decayNumber);

            delayLines.add(decayedAudio);
        }
        
        // Add delay lines with diffusion spacing
        short[] delayLineAudio = new short[reverbNums.length+numOfDelayLines*diffusion];
        int delayLineCounter = 0;
        for (int i = 0; i < delayLines.size(); i++) {
            for (int j = 0; j < delayLines.get(i).length; j++) {
                delayLineAudio[j+(delayLineCounter*diffusion)] += delayLines.get(i)[j];
            }
            delayLineCounter++;
        }
        
        // Dry Wet Mixing
        short[] dryAudio = new short[reverbNums.length];
        short[] wetAudio = new short[delayLineAudio.length];
        
        // Setup dry sound
        for (int i = 0; i < dryAudio.length; i++) {
            dryAudio[i] = (short) (reverbNums[i]*wetDryFactor);
        }
        
        // Setup wet sound
        for (int i = 0; i < wetAudio.length; i++) {
            wetAudio[i] = (short) (delayLineAudio[i] * (1-wetDryFactor));
        }
        
        // Mix dry and wet
        short[] mixedAudio = new short[delayLineAudio.length+preDelay];
        int wetPos = 0;
        for (int i = 0; i < mixedAudio.length; i++) {
            if (i<=preDelay) {
                mixedAudio[i] = dryAudio[i];
            } else if (i>preDelay && i<dryAudio.length) {
                mixedAudio[i] = (short) (dryAudio[i] + wetAudio[wetPos]);
                wetPos++;
            } else if (i>dryAudio.length){
                mixedAudio[i] = wetAudio[wetPos];
                wetPos++;
            }
        }
        
        // Revert back to byte array to have playback functionality
        byte[] finalAudio = new byte[(delayLineAudio.length+preDelay) * 2];
        for (int i = 0; i < mixedAudio.length; i++) {
                ByteBuffer.wrap(finalAudio, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(mixedAudio[i]); // i*2 since each short is 2 bytes long
            }
        
        byte[] audioToPlay = new byte[((delayLineAudio.length+preDelay) * 2) + 44];
        System.arraycopy(finalAudio, 0, audioToPlay, 44, (delayLineAudio.length+preDelay) * 2); // Add the audio data
        System.arraycopy(originalAudio, 0, audioToPlay, 0, 44); // Add the header
        playAudio(finalAudio);
    }
    
    /**
     * Decays the audio over its duration
     * @param audioData the audio data to be decayed
     * @param amplitudeFactor the initial factor by which the audio will be decayed
     * @return the decayed audio data
     */
    private short[] decayAudio(short[] audioData, double amplitudeFactor) {
        // Copy array
        short[] volumeControlledAudio = new short[audioData.length];
        for (int i = 0; i < volumeControlledAudio.length; i++) {
            volumeControlledAudio[i] = audioData[i];
        }

        // Apply decay effect
        double initialAmplitude = amplitudeFactor;
        for (int i = 0; i < volumeControlledAudio.length; i++) {
            volumeControlledAudio[i] = (short) (volumeControlledAudio[i] * amplitudeFactor);
            amplitudeFactor = initialAmplitude*Math.pow(Math.E, - (decay/50000) * i); // Based on exponential decay equation

            // To keep the audio audible
            if (amplitudeFactor <= 0.05) {
                amplitudeFactor = 0.05;
            }
        }
        return volumeControlledAudio;
    }

    /**
     * Plays audio data stored in a byte array
     * @param audioData the audio data to be played
     */
    private void playAudio(byte[] audioData) {
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
            delayLines = null;
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.out.println(e);
        }
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
