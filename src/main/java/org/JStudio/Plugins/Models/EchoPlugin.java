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
 * Echo plugin that takes in audio data and applies an echo effect to it
 * @author Theodore Georgiou
 */
public class EchoPlugin {
    private String filePathName;
    private double decay;
    private double wetDryFactor;
    private int preDelay;
    private int echoNum;
    private int diffusion;
    private byte[] originalAudio;
    private byte[] finalAudio;
    private ArrayList<short[]> echos = new ArrayList<>();
    private SourceDataLine line;

    // Creates an echo
    public EchoPlugin(int preDelay, double decay, int diffusion, int echoNum, double wetDryFactor) {
        this.preDelay = preDelay;
        this.decay = decay;
        this.diffusion = diffusion;
        this.echoNum = echoNum;
        this.wetDryFactor = wetDryFactor;
        convertAudioFileToByteArray();
    }

    /**
     * Converts audio data from a wav file to a byte array
     */
    private void convertAudioFileToByteArray() {
        try {
            FileChooser fl = new FileChooser();
            File selectedFile = fl.showOpenDialog(null);
            filePathName = selectedFile.getAbsolutePath();
            originalAudio = Files.readAllBytes(selectedFile.toPath());
        } catch (IOException e) {
            System.out.println(e);
        }
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
        short[] audioToEcho = new short[noHeaderByteAudioData.length / 2];
        for (int i = 0; i < audioToEcho.length; i++) {
            audioToEcho[i] = ByteBuffer.wrap(noHeaderByteAudioData, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort(); // // i*2 since each short is 2 bytes long
        }
        
        return audioToEcho;
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
                echos = null;
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

    // Wrapper class to set echo effect
    public void setEchoEffect() {
        applyEchoEffect();
    }
    
    public void setDecay(double decay) {
        this.decay = decay;
    }

    public void setPreDelay(int preDelay) {
        this.preDelay = preDelay;
    }

    public void setEchoNum(int echoNum) {
        this.echoNum = echoNum;
    }

    public void setDiffusion(int diffusion) {
        this.diffusion = diffusion;
    }

    public void setWetDryFactor(double wetDryFactor) {
        this.wetDryFactor = wetDryFactor;
    }
}
