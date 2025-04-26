package org.JStudio.Core;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class AudioClip extends Clip {
//    private short[][] buffer;
    private float[][] buffer;

    AudioClip(int position) {
        super(position);
//        this.buffer = ;
    }

    //test init class
    public AudioClip(int position, File f) {
        super(position);
        try {
            this.buffer = readWavFile(f);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBuffertoString() {
        return Arrays.deepToString(buffer);
    }

    public float[][] getBuffer() {return buffer;}

    private float[][] readWavFile(File file) throws UnsupportedAudioFileException, IOException { //Reading wav file (this method repeats a lot, move every other version to this one)
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();

        int sampleSize = format.getSampleSizeInBits();
        int channels = format.getChannels();
        byte[] pcmData = audioInputStream.readAllBytes();
        int bytesPerSample = sampleSize / 8;
        int totalSamples = pcmData.length / (bytesPerSample * channels);

        float[] leftChannel = new float[totalSamples];
        float[] rightChannel = (channels == 2) ? new float[totalSamples] : null;

        for (int i = 0; i < totalSamples; i++) {
            int sampleIndex = i * channels * bytesPerSample;

            // Extract Left Channel
            leftChannel[i] = extractSample(pcmData, sampleIndex, sampleSize);

            // Extract Right Channel (if Stereo)
            if (channels == 2) {
                rightChannel[i] = extractSample(pcmData, sampleIndex + bytesPerSample, sampleSize);
            }
        }

        return new float[][]{leftChannel, rightChannel};
    }

    private float extractSample(byte[] data, int index, int bitDepth) { //Extracting audio (support for 8, 16 and 24 bit audio - 32 bit will also be supported soon)
        int sample = 0;

        //combine this with the edian class that ahmet made (arya)
        if (bitDepth == 8) {
            sample = (data[index] & 0xFF) - 128;
            return sample / 128.0f; //converting unsigned 8-bit to signed
        } else if (bitDepth == 16) {
            sample = ((data[index + 1] << 8) | (data[index] & 0xFF));
            return sample / 32768.0f; //converting 16-bit signed to unsigned
        } else if (bitDepth == 24) {
            sample = ((data[index + 2] << 16) | ((data[index + 1] & 0xFF) << 8) | (data[index] & 0xFF));
            if (sample > 0x7FFFFF) sample -= 0x1000000; //converting 24-bit signed to unsigned
            return sample / 8388608.0f;
        }

        return sample; // Unsupported bit depth
    }
}
