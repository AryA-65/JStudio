package org.JStudio.Plugins;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Reverb plugin that takes in audio data and applies a reverb effect to it
 * @author Theodore Georgiou
 */
public class ReverbPlugin {
    private String fileName;
    private double decay;
    private double wetDryFactor;
    private int preDelay;
    private double diffusion;
    private byte[] originalAudio;
    private byte[] firstDelayLine;
    private byte[] secondDelayLine;
    private byte[] thirdDelayLine;
    private byte[] fourthDelayLine;

    // Creates a reverb
    public ReverbPlugin() {
        fileName = "\\jumpland.wav"; // Temporary value for now (will have file setting functionality later)
    }

    /**
     * Converts audio data from a wav file to a byte array
     */
    private void convertAudioFileToByteArray() {
        try {
            fileName = "\\lowlife.wav"; // Use your own .wav file (44.1 kHz sample rate) to run
//            String filePath = Paths.get(System.getProperty("user.home"), "Downloads") + fileName;
            String filePath = "C:\\Users\\The Workstation\\Downloads\\beep-10.wav";
            Path path = Paths.get(filePath);
//            originalAudio = Files.readAllBytes(path);
            originalAudio = Files.readAllBytes(Paths.get("C:\\Users\\The Workstation\\Downloads\\beep-10.wav"));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Applies the reverb effect to the audio data array
     */
    private void applyReverbEffect() {
        convertAudioFileToByteArray();
        byte[] audioToReverb = new byte[originalAudio.length - 44];

        // the audio to reverb has same audio data as the original audio for now (no header)
        System.arraycopy(originalAudio, 44, audioToReverb, 0, audioToReverb.length);

        // Convert audio data to short type to avoid audio warping
        short[] reverbNums = new short[audioToReverb.length / 2];
        for (int i = 0; i < reverbNums.length; i++) {
            reverbNums[i] = ByteBuffer.wrap(audioToReverb, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort(); // // i*2 since each short is 2 bytes long
        }

        double decayNumber = 0;
        // Loop repeats 4 times for 4 delay lines
        for (int delayLine = 0; delayLine < 4; delayLine++) {
            switch (delayLine) {
                case 0 -> decayNumber = decay/1000;
                case 1 -> decayNumber = decay/1000 - 0.2;
                case 2 ->  decayNumber = decay/1000 - 0.45;
                case 3 -> decayNumber = decay/1000 - 0.6;
            }
            
            short[] decayedAudio = new short[reverbNums.length];
            short[] dryAudio = new short[reverbNums.length];
            short[] wetAudio = new short[reverbNums.length];
            short[] dryWetMixedAudio = new short[reverbNums.length];
            
            // Decay the audio
            decayedAudio = decayAudio(reverbNums, decayNumber);

            for (int i = 0; i < decayedAudio.length; i++) {
                dryAudio[i] = (short) (reverbNums[i] * wetDryFactor);
                wetAudio[i] = (short) (decayedAudio[i] * (1-wetDryFactor));
            }

            // Mix dry and wet audio
            for (int i = 0; i < dryWetMixedAudio.length; i++) {
                dryWetMixedAudio[i] = (short) (dryAudio[i] + wetAudio[i]);
            }

            // Adding previous reverbed points to current data point
            for (int i = 1; i < dryWetMixedAudio.length; i++) {
                dryWetMixedAudio[i] += (short) (dryWetMixedAudio[i-1] * decayNumber);
            }

            // Revert back to byte array to have playback functionality
            byte[] finalAudio = new byte[dryWetMixedAudio.length * 2];
            for (int i = 0; i < dryWetMixedAudio.length; i++) {
                ByteBuffer.wrap(finalAudio, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(dryWetMixedAudio[i]); // i*2 since each short is 2 bytes long
            }
            
            switch (delayLine) {
                case 0:
                    firstDelayLine = new byte[originalAudio.length];
                    System.arraycopy(finalAudio, 0, firstDelayLine, 44, reverbNums.length * 2); // Add the audio data
                    System.arraycopy(originalAudio, 0, firstDelayLine, 0, 44); // Add the header
                    break;
                case 1:
                    secondDelayLine = new byte[originalAudio.length];
                    System.arraycopy(finalAudio, 0, secondDelayLine, 44, reverbNums.length * 2); // Add the audio data
                    System.arraycopy(originalAudio, 0, secondDelayLine, 0, 44); // Add the header
                    break;
                case 2:
                    thirdDelayLine = new byte[originalAudio.length];
                    System.arraycopy(finalAudio, 0, thirdDelayLine, 44, reverbNums.length * 2); // Add the audio data
                    System.arraycopy(originalAudio, 0, thirdDelayLine, 0, 44); // Add the header
                    break;
                case 3:
                    fourthDelayLine = new byte[originalAudio.length];
                    System.arraycopy(finalAudio, 0, fourthDelayLine, 44, reverbNums.length * 2); // Add the audio data
                    System.arraycopy(originalAudio, 0, fourthDelayLine, 0, 44); // Add the header
                    break;
            }
        }
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
        System.arraycopy(audioData, 0, volumeControlledAudio, 0, volumeControlledAudio.length);

        // Apply decay effect
        double initialAmplitude = amplitudeFactor;
        for (int i = 0; i < volumeControlledAudio.length; i++) {
            volumeControlledAudio[i] = (short) (volumeControlledAudio[i] * amplitudeFactor);
            amplitudeFactor = initialAmplitude*Math.pow(Math.E, - (decay/50000) * i); // Based on exponential decay equation
            
            // To keep the audio audible
            if (amplitudeFactor <= 0.01) {
                amplitudeFactor = 0.01;
            }
        }
        return volumeControlledAudio;
    }

    /**
     * Sets up the audio data to be played
     * @param audioData the audio data to be played
     * @return the clip that will be played
     */
    private Clip setUpAudio(byte[] audioData) {
        Clip clip = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(byteArrayInputStream);

            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println(e);
        }
        return clip;
    }

    /**
     * Sets up the delay line effect and plays the audio
     */
    private void setDelayLines() {
        applyReverbEffect();
        
        Clip originalAudioClip = setUpAudio(originalAudio);
        Clip firstDelayLineClip = setUpAudio(firstDelayLine);
        Clip secondDelayLineClip = setUpAudio(secondDelayLine);
        Clip thirdDelayLineClip = setUpAudio(thirdDelayLine);
        Clip fourthDelayLineClip = setUpAudio(fourthDelayLine);

        long totalClipDuration = originalAudioClip.getMicrosecondLength();
        long preDelayAmount = preDelay * 2L;
        
        firstDelayLineClip.setMicrosecondPosition(totalClipDuration/preDelayAmount);
        secondDelayLineClip.setMicrosecondPosition((long) (totalClipDuration/(preDelayAmount+(totalClipDuration*0.0005 + diffusion))));
        thirdDelayLineClip.setMicrosecondPosition((long) (totalClipDuration/(preDelayAmount+(preDelayAmount+(totalClipDuration*0.0008 + 2*diffusion)))));
        fourthDelayLineClip.setMicrosecondPosition((long) (totalClipDuration/(preDelayAmount+(preDelayAmount+(totalClipDuration*0.001 + 3*diffusion)))));
        
        originalAudioClip.start();
        firstDelayLineClip.start();
        secondDelayLineClip.start();
        thirdDelayLineClip.start();
        fourthDelayLineClip.start();
    }

    // Wrapper class to set effect and play it
    public void setReverbEffect() {
        setDelayLines();
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
    public void setDiffusion(double diffusion) {
        this.diffusion = diffusion;
    }
}
