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
    private String fileName;
    private String filePathName;
    private double decay;
    private double wetDryFactor;
    private int preDelay;
    private int echoNum;
    private int diffusion;
    private byte[] originalAudio;
    private ArrayList<short[]> echos = new ArrayList<>();

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
        byte[] audioToEcho = new byte[originalAudio.length - 44];

        // The audio to echo has same audio data as the original audio for now (no header)
        for (int i = 0; i < audioToEcho.length; i++) {
            audioToEcho[i] = originalAudio[i + 44];
        }

        // Convert audio data to short type to avoid audio warping
        short[] echoNums = new short[audioToEcho.length / 2];
        for (int i = 0; i < echoNums.length; i++) {
            echoNums[i] = ByteBuffer.wrap(audioToEcho, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort(); // // i*2 since each short is 2 bytes long
        }

        int numOfEchos = echoNum;
        double decayValue = decay;
        // Loop repeats for the number of echos
        for (int echo = 0; echo < numOfEchos; echo++) {
            short[] loweredAudio = new short[echoNums.length];
            
            // lower the audio
            loweredAudio = lowerAudio(echoNums, decayValue);

            echos.add(loweredAudio);
            decayValue *= decay;
        }
        
        // Add echos with diffusion spacing
        short[] echoAudio = new short[echoNums.length+numOfEchos*diffusion];
        int echoCounter = 0;
        for (int i = 0; i < echos.size(); i++) {
            for (int j = 0; j < echos.get(i).length; j++) {
                echoAudio[j+(echoCounter*diffusion)] += echos.get(i)[j];
                
                if (echoAudio[j+(echoCounter*diffusion)] > Short.MAX_VALUE) {
                echoAudio[j+(echoCounter*diffusion)] = Short.MAX_VALUE;
                } else if (echoAudio[j+(echoCounter*diffusion)] < Short.MIN_VALUE) {
                    echoAudio[j+(echoCounter*diffusion)] = Short.MIN_VALUE;
                }
            }
            echoCounter++;
        }
        
        // Dry Wet Mixing
        short[] dryAudio = new short[echoNums.length];
        short[] wetAudio = new short[echoAudio.length];
        
        // Setup dry sound
        for (int i = 0; i < dryAudio.length; i++) {
            dryAudio[i] = (short) (echoNums[i]*wetDryFactor);
        }
        
        // Setup wet sound
        for (int i = 0; i < wetAudio.length; i++) {
            wetAudio[i] = (short) (echoAudio[i] * (1-wetDryFactor));
        }
        
        // Mix dry and wet
        short[] mixedAudio = new short[echoAudio.length+preDelay];
        int wetPos = 0;
        for (int i = 0; i < mixedAudio.length; i++) {
            if (i<=preDelay) {
                mixedAudio[i] = dryAudio[i];
            } else if (i>preDelay && i<dryAudio.length) {
                mixedAudio[i] = (short) (dryAudio[i] + wetAudio[wetPos]);
                
                if (mixedAudio[i] > Short.MAX_VALUE) {
                mixedAudio[i] = Short.MAX_VALUE;
                } else if (mixedAudio[i] < Short.MIN_VALUE) {
                    mixedAudio[i] = Short.MIN_VALUE;
                }
                wetPos++;
            } else if (i>dryAudio.length){
                mixedAudio[i] = wetAudio[wetPos];
                wetPos++;
            }
        }
        
        // Revert back to byte array to have playback functionality
        byte[] finalAudio = new byte[(echoAudio.length+preDelay) * 2];
        for (int i = 0; i < mixedAudio.length; i++) {
                ByteBuffer.wrap(finalAudio, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(mixedAudio[i]); // i*2 since each short is 2 bytes long
            }
        
        byte[] audioToPlay = new byte[((echoAudio.length+preDelay) * 2) + 44];
        System.arraycopy(finalAudio, 0, audioToPlay, 44, (echoAudio.length+preDelay) * 2); // Add the audio data
        System.arraycopy(originalAudio, 0, audioToPlay, 0, 44); // Add the header
        playAudio(finalAudio);
    }
    
    /**
     * Lowers the amplitude the audio over its duration
     * @param audioData the audio data to be lowered
     * @param amplitudeFactor the initial factor by which the audio will be lowered
     * @return the lowered audio data
     */
    private short[] lowerAudio(short[] audioData, double amplitudeFactor) {
        // Copy array
        short[] loweredAudio = new short[audioData.length];
        for (int i = 0; i < loweredAudio.length; i++) {
            loweredAudio[i] = audioData[i];
        }

        // Apply decay effect
        for (int i = 0; i < loweredAudio.length; i++) {
            loweredAudio[i] = (short) (loweredAudio[i] * amplitudeFactor);
            if (loweredAudio[i] > Short.MAX_VALUE) {
                loweredAudio[i] = Short.MAX_VALUE;
            } else if (loweredAudio[i] < Short.MIN_VALUE) {
                loweredAudio[i] = Short.MIN_VALUE;
            }
        }
        return loweredAudio;
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

            int frameSize = line.getFormat().getFrameSize();
            int trimmedLength = (audioData.length / frameSize) * frameSize;
            line.write(audioData, 0, trimmedLength);

            line.drain();
            line.close();
            echos = null;
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.out.println(e);
        }
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
