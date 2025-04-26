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

public abstract class Plugin {
    protected String filePathName;
    protected byte[] originalAudio;
    protected byte[] finalAudio;
    protected SourceDataLine line;
    
    /**
     * Converts audio data from a wav file to a byte array
     */
    protected void convertAudioFileToByteArray() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("WAV Files", "*.wav"),
                new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"),
                new FileChooser.ExtensionFilter("All Audio Files", "*.wav", "*.mp3"));
            File file = fileChooser.showOpenDialog(null);
            filePathName = file.getAbsolutePath();
            originalAudio = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    /**
     * Plays audio data stored in a byte array
     * @param audioData the audio data to be played
     */
    protected void playAudio(byte[] audioData) {
        // Stops any previous audio playing
        if (line!=null) {
            line.close();
        }
        
        new Thread(() -> {
            try {
                File file = new File(filePathName);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat audioFormat = audioInputStream.getFormat();
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                line = (SourceDataLine) AudioSystem.getLine(info);
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
     * Stops audio playback
     */
    public void stopAudio() {
        line.close();
    }
    
    /**
     * Converts the original audio data to a short array to allow for modifications
     * @return the short[] audio data array
     */
    protected short[] convertToShortArray() {
        byte[] noHeaderByteAudioData = new byte[originalAudio.length - 44];
        // The audio to add flanging to has same audio data as the original audio for now (no header)
        System.arraycopy(originalAudio, 44, noHeaderByteAudioData, 0, originalAudio.length - 44);

        // Convert audio data to short type to avoid audio warping
        short[] audioToModulate = new short[noHeaderByteAudioData.length / 2];
        for (int i = 0; i < audioToModulate.length; i++) {
            audioToModulate[i] = ByteBuffer.wrap(noHeaderByteAudioData, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort(); // // i*2 since each short is 2 bytes long
        }
        
        return audioToModulate;
    }
    
    /**
     * Revert short[] audio data back to byte array to have playback functionality
     * @param audioData the audio data to be converted to a byte array
     * @param sizeOfByteArray the size of the array to save
     */
    protected void convertToByteArray(short[] audioData, int sizeOfByteArray) {
        // Revert back to byte array to have playback functionality
        byte[] modifiedAudio = new byte[sizeOfByteArray];
        for (int i = 0; i < audioData.length; i++) {
            ByteBuffer.wrap(modifiedAudio, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(audioData[i]); // i*2 since each short is 2 bytes long
        }
        
        finalAudio = new byte[sizeOfByteArray];
        System.arraycopy(modifiedAudio, 0, finalAudio, 0, sizeOfByteArray); // Add the audio data
        playAudio(finalAudio);
    }
    
    /**
     * Caps the amplitude of a sample from exceeding the maximum value of a short
     * @param sample the sample to be capped
     * @return the capped sample
     */
    protected short capMaxAmplitude(short sample) {
        if (sample > Short.MAX_VALUE) {
                sample = Short.MAX_VALUE;
        } else if (sample < Short.MIN_VALUE) {
            sample = Short.MIN_VALUE;
        }
        return sample;
    }

    /**
     * Method to transform a double array into a byte array
     * @param doubleArray the array to be transformed
     * @return the byte array
     */
    public static byte[] doubleArrayToByteArray(double[] doubleArray) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(doubleArray.length * Double.BYTES);
        for (double d : doubleArray) {
            byteBuffer.putDouble(d);
        }
        return byteBuffer.array();
    }

    public byte[] shortToByte(short [] input)
    {
        int index;
        int iterations = input.length;

        ByteBuffer bb = ByteBuffer.allocate(input.length * 2);

        for(index = 0; index != iterations; ++index)
        {
            bb.putShort(input[index]);
        }

        return bb.array();
    }

    public static double[] shortToDouble(short[] shortArray) {
        if (shortArray == null) {
            return null;
        }
        double[] doubleArray = new double[shortArray.length];
        for (int i = 0; i < shortArray.length; i++) {
            doubleArray[i] = (double) shortArray[i];
        }
        return doubleArray;
    }

    public byte[] getFinalAudio() {
        return finalAudio;
    }
}
