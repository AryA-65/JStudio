package org.JStudio.Plugins.Models;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import javafx.stage.FileChooser;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Abstract plugin class that handles basic use (playing, stopping, array
 * conversions)
 *
 * @author Theo, Ahmet, Alex
 */
public abstract class Plugin {
    protected String filePathName;
    protected String fileName;
    protected byte[] originalAudio;
    protected byte[] finalAudio;
    protected float[] audioFloatInput;
    protected byte[] audioByteInput;
    protected float[] floatOutput;
    protected double outputGain;
    protected SourceDataLine line;
    private Thread playingThread;
    private int numOfChannels;
    private AudioFormat audioFormat;
    private boolean fileSelected = false;
    
    //getters and setters
    public float[] getAudioFloatInput(){
        return audioFloatInput;
    }

    public String getName() {
        return "";
    }

    public SourceDataLine getAudioLine() {
        return line;
    }
    public byte[] getOriginalAudio(){
        return originalAudio;
    }
    
    public void setOriginalAudio(byte[] originalAudio){
        this.originalAudio = originalAudio;
    }
    
    public byte[] getFinalAudio() {
        return finalAudio;
    }
    
    public String getFileName() {
            return fileName;
    }
    
    public String getFilePathName() {
        return filePathName;
    }
    
    public byte[] getAudioByteInput(){
        return audioByteInput;
    }
    
    public boolean isFileSelected(){
        return fileSelected;
    }
    
    public void setIsFileSelected(boolean isFileSelected){
        fileSelected = isFileSelected;
    }
    
    public void setFinalAudio(byte[] finalAudio) {
        this.finalAudio = finalAudio;
    }

    public void setOutputGain(double outputGain) {
        this.outputGain = outputGain;
    }

    public void setFilePathName(String filePathName) {
        this.filePathName = filePathName;
    }
    
    public Thread getPlayingThread(){
        return playingThread;
    }

    public void setFloatOutput(float[] floatOutput) {
        this.floatOutput = floatOutput;
    }
    
    public void setAudioByteInput(byte[] audioByteInput){
        this.audioByteInput = audioByteInput;
    }

    public void clearFinalAudio() {
        finalAudio = null;
    }
    
    //constructor initializes outputGain
    public Plugin() {
        outputGain = 1;
    }
    
    //exports plugin modifications as a wav file
    public void export(String pluginName){
        try {            
            //create AudioInputStream from the byte array
            ByteArrayInputStream bais = new ByteArrayInputStream(finalAudio);
            AudioInputStream audioInputStream = new AudioInputStream(bais, audioFormat, finalAudio.length / audioFormat.getFrameSize());
            
            //make sure the file path exists
            File dir = new File(System.getProperty("user.home") + File.separator + "Music" + File.separator + "JStudio" + File.separator + "audio_Files" + File.separator + "Plugins");
            if (!dir.exists()) {
                Files.createDirectories(dir.toPath());
            }
            
            File wavFile;
            
            //save to wav file
            if (new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName +".wav").exists()) {
                int i = 1;
                while(new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName + i +".wav").exists()){
                    i++;
                }
                wavFile = new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName + i +".wav");
            } else {
                wavFile = new File(dir.toPath() + File.separator + fileName.replace(".wav", "") + "_" + pluginName +".wav");
            }
            
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    /**
     * Method that works without fields to allow for JUnit testing
     * @param filePathName the file path of the wav file
     * @return the byte array of a wav file
     */
    protected byte[] convertFileToByteTestingMethod(String filePathName) {
        byte[] byteOutput = null;
        try {
            byteOutput = Files.readAllBytes(Paths.get(filePathName)); //get array of all bytes from the stream
        } catch (IOException e) {
            System.out.println(e);
        }
        return byteOutput;
    }
    
    /**
     * Applies output gain to audio data
     * @param audioData the audio to apply output gain to
     * @return the audio data with output gain
     */
    public short[] outputGainAudio(short[] audioData) {
        short[] outputGainedAudio = new short[audioData.length];
        System.arraycopy(audioData, 0, outputGainedAudio, 0, audioData.length);
        
        for (int i = 0; i < outputGainedAudio.length; i++) {
           outputGainedAudio[i] = (short) (outputGainedAudio[i] * outputGain);
        }
        return outputGainedAudio;
    }
    
    /**
     * Converts audio data from a wav file to a byte array
     */
    protected void convertAudioFileToByteArray() {
        fileSelected = false;
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("WAV Files", "*.wav"),
                    new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"),
                    new FileChooser.ExtensionFilter("All Audio Files", "*.wav", "*.mp3"));
            File file = fileChooser.showOpenDialog(null);
            
            if(file == null){
                return;
            }
            fileSelected = true;

            filePathName = file.getAbsolutePath();
            fileName = file.getName();
            originalAudio = Files.readAllBytes(file.toPath());
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            audioFormat = audioInputStream.getFormat();
            numOfChannels = audioFormat.getChannels();

            audioFloatInput = convertByteToFloatArray(originalAudio);
            audioByteInput = convertFloatToByteArray(audioFloatInput);

//            float[][] floatArray = {{1, 2, 3, 11, 75}, {4, 5, 6, 17, 90}};
//            convert2DByteTo2DFloat(convert2DFloatTo2DByte(floatArray));
//            filePathName = file.getAbsolutePath();
//            originalAudio = Files.readAllBytes(file.toPath());
        } catch (IOException | UnsupportedAudioFileException e) {
            System.out.println(e);
        }
    }
    
    
    public byte[] convertFloatToByteArray(float[] audioData) {
        byte[] byteArray = new byte[audioData.length * 4];
            ByteBuffer buffer = ByteBuffer.allocate(audioData.length * 4);
            for (float floatData : audioData) {
                buffer.putFloat(floatData);
            }
            byteArray = buffer.array();

//        for (int i = 0; i < byteArray.length; i++) {
//            for (int j = 0; j < byteArray[i].length; j++) {
//                System.out.print(byteArray[i][j] + ",");
//            }
//            System.out.println();
//        }
        return byteArray;
    }

    public float[] convertByteToFloatArray(byte[] audioData) {
        float[] floatArray = new float[audioData.length / 4];
        try {

                ByteArrayInputStream bos = new ByteArrayInputStream(audioData);
                DataInputStream dis = new DataInputStream(bos);

                for (int i = 0; i < floatArray.length; i++) {
                    floatArray[i] = dis.readFloat();
                }

//        for (int i = 0; i < floatArray.length; i++) {
//            for (int j = 0; j < floatArray[i].length; j++) {
//                System.out.print(floatArray[i][j] + ",");
//            }
//            System.out.println();
//        }
        } catch (Exception e) {
            System.out.println(e);
        }

        return floatArray;
    }

    /**
     * Wrapper class for playing method
     */
    public void play() {
        playAudio(finalAudio);
    }

    /**
     * Plays audio data stored in a byte array
     * @param audioData the audio data to be played
     */
    protected void playAudio(byte[] audioData) {
        // Stops any previous audio playing
        if (line != null && playingThread != null) {
            playingThread.interrupt();
            line.close();
        }

        playingThread = new Thread(() -> {
            try {
                AudioFormat audioFormat = new AudioFormat(44100, 16, numOfChannels, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(audioFormat);
                line.start();

                int frameSize = audioFormat.getFrameSize();
                int lengthToWrite = audioData.length - (audioData.length % frameSize);

                line.write(audioData, 0, lengthToWrite);

                line.drain();
                line.close();
            } catch (LineUnavailableException e) {
                System.out.println(e);
            }
        });

        playingThread.start();
    }

    /**
     * Stops audio playback
     */
    public void stopAudio() {
        if (line != null && line.isOpen()) {
            line.flush(); // discard non called data
            line.stop(); // stop
            line.close(); // close resources
        }
    }

    /**
     * Converts the original audio data to a short array to allow for
     * modifications
     * @return the short[] audio data array
     */
    protected short[] convertToShortArray() {
        byte[] noHeaderByteAudioData = new byte[originalAudio.length - 44];
        // The audio to add flanging to has same audio data as the original audio for now (no header)
        System.arraycopy(originalAudio, 44, noHeaderByteAudioData, 0, originalAudio.length - 44);

        // Convert audio data to short type to avoid audio warping
        short[] shortArrayData = new short[noHeaderByteAudioData.length / 2];
        for (int i = 0; i < shortArrayData.length; i++) {
            shortArrayData[i] = ByteBuffer.wrap(noHeaderByteAudioData, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort(); // // i*2 since each short is 2 bytes long
        }

        return shortArrayData;
    }
    
    protected short[] convertToShortArray(byte[] originalAudio) {
        byte[] noHeaderByteAudioData = new byte[originalAudio.length - 44];
        // The audio to add flanging to has same audio data as the original audio for now (no header)
        System.arraycopy(originalAudio, 44, noHeaderByteAudioData, 0, originalAudio.length - 44);

        // Convert audio data to short type to avoid audio warping
        short[] shortArrayData = new short[noHeaderByteAudioData.length / 2];
        for (int i = 0; i < shortArrayData.length; i++) {
            shortArrayData[i] = ByteBuffer.wrap(noHeaderByteAudioData, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort(); // // i*2 since each short is 2 bytes long
        }

        return shortArrayData;
    }
    

    /**
     * Revert short[] audio data back to byte array to have playback
     * functionality
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
    }

    /**
     * Caps the amplitude of a sample from exceeding the maximum value of a
     * short
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


    public short[] bytesToShorts(byte[] bytes) {
        short[] shorts = new short[bytes.length / 2];
        for (int i = 0; i < shorts.length; i++) {
            shorts[i] = (short) ((bytes[2 * i + 1] << 8) | (bytes[2 * i] & 0xFF));
        }
        return shorts;
    }

    public byte[] shortsToBytes(short[] shorts) {
        byte[] bytes = new byte[shorts.length * 2];
        for (int i = 0; i < shorts.length; i++) {
            bytes[2 * i] = (byte) (shorts[i] & 0xFF);
            bytes[2 * i + 1] = (byte) ((shorts[i] >> 8) & 0xFF);
        }
        return bytes;
    }

    public float[] shortsToFloats(short[] shorts) {
        float[] floats = new float[shorts.length];
        for (int i = 0; i < shorts.length; i++) {
            floats[i] = shorts[i] / 32768f;
        }
        return floats;
    }
}
