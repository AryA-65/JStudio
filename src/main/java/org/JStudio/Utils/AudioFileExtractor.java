package org.JStudio.Utils;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

public class AudioFileExtractor {
    private static AudioInputStream ais;
    private static AudioFormat format;

    public static float[][] readFile(File file) throws Exception {
        if (file.getName().toLowerCase().endsWith(".mp3")) {
            return readMp3(file);
        } else if (file.getName().toLowerCase().endsWith(".wav")) {
            return readWavFile(file);
        } else {
            throw new IllegalArgumentException("Unsupported file format");
        }
    }

    //wav section
    private static float[][] readWavFile(File file) throws UnsupportedAudioFileException, IOException {
        ais = AudioSystem.getAudioInputStream(file);
        format = ais.getFormat();

        int sampleSize = format.getSampleSizeInBits();
        int channels = format.getChannels();
        byte[] pcmData = ais.readAllBytes();
        int bytesPerSample = sampleSize / 8;
        int totalSamples = pcmData.length / (bytesPerSample * channels);

        float[] leftChannel = new float[totalSamples];
        float[] rightChannel = (channels == 2) ? new float[totalSamples] : null;

        for (int i = 0; i < totalSamples; i++) {
            int sampleIndex = i * channels * bytesPerSample;

            leftChannel[i] = SampleExtractor.extractSample(pcmData, sampleIndex, sampleSize);

            if (channels == 2) {
                rightChannel[i] = SampleExtractor.extractSample(pcmData, sampleIndex + bytesPerSample, sampleSize);
            }
        }

        return new float[][]{leftChannel, rightChannel};
    }

    public static double getLength() {
        return ais.getFrameLength() / format.getSampleRate();
    }

    public static int getSampleRate() {
        return (int) format.getSampleRate();
    }

    public static float[] downsample(float[] samples, int targetSize) {
        float[] downsampled = new float[targetSize];
        int step = samples.length / targetSize;

        for (int i = 0; i < targetSize; i++) {
            downsampled[i] = samples[i * step];
        }

        return downsampled;
    }
    //end of wav section

    //mp3 section
    private static float[][] readMp3(File file) throws Exception {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        Bitstream bitstream = new Bitstream(inputStream);
        Decoder decoder = new Decoder();
        float[] leftChannel = new float[10000000];
        float[] rightChannel = new float[10000000];

        int index = 0;
        boolean isStereo = false;

        while (true) {
            Header frameHeader = bitstream.readFrame();
            if (frameHeader == null) break;

            SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
            isStereo = output.getChannelCount() == 2;

            for (int i = 0; i < output.getBufferLength(); i++) {
                if (isStereo) {
                    if (i % 2 == 0) leftChannel[index] = output.getBuffer()[i] / 32768.0f;
                    else rightChannel[index] = output.getBuffer()[i] / 32768.0f;
                } else {
                    leftChannel[index] = output.getBuffer()[i] / 32768.0f;
                }
                index++;
            }

            bitstream.closeFrame();
        }

        return new float[][]{trimArray(leftChannel, index), isStereo ? trimArray(rightChannel, index) : null};
    }

    private static float[] trimArray(float[] array, int size) {
        float[] trimmed = new float[size];
        System.arraycopy(array, 0, trimmed, 0, size);
        return trimmed;
    }
    //end of mp3 section
}
