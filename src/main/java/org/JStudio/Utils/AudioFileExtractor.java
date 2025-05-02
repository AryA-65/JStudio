package org.JStudio.Utils;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

import javax.sound.sampled.*;
import java.io.*;

public class AudioFileExtractor {
    private static AudioInputStream ais;
    private static AudioFormat format;
    private static AudioFileFormat fileFormat;
    private static boolean file_type;

    public static boolean isMp3() {
        return file_type;
    }

    public static float[][] readFile(File file) throws Exception {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".mp3")) {
            return null;
//            file_type = true;
//            return readMp3(file);
        } else if (name.endsWith(".wav")) {
            file_type = false;
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
        int bytesPerSample = sampleSize / 8;

        byte[] pcmData = ais.readAllBytes();
        int totalSamples = pcmData.length / (bytesPerSample * channels);

        float[] left = new float[totalSamples];
        float[] right = (channels == 2) ? new float[totalSamples] : null;

        for (int i = 0; i < totalSamples; i++) {
            int index = i * bytesPerSample * channels;
            left[i] = SampleExtractor.extractSample(pcmData, index, sampleSize);
            if (channels == 2) {
                right[i] = SampleExtractor.extractSample(pcmData, index + bytesPerSample, sampleSize);
            }
        }

        return new float[][]{left, right};
    }

    public static double getwavLength() {
        return ais != null && format != null
                ? ais.getFrameLength() / format.getSampleRate()
                : -1;
    }

    public static int getSampleRate() {
        return format != null ? (int) format.getSampleRate() : -1;
    }

    public static float[] downsample(float[] samples, int targetSize) {
        float[] result = new float[targetSize];
        int step = samples.length / targetSize;
        for (int i = 0; i < targetSize; i++) {
            result[i] = samples[i * step];
        }
        return result;
    }
    //end of wav section

    //mp3 section
    private static float[][] readMp3(File file) throws Exception {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        Bitstream bitstream = new Bitstream(inputStream);
        Decoder decoder = new Decoder();

        ais = AudioSystem.getAudioInputStream(file);
        fileFormat = AudioSystem.getAudioFileFormat(file);
        format = ais.getFormat();

        float[] left = new float[8_000_000];
        float[] right = new float[8_000_000];
        int index = 0;
        boolean stereo = false;

        Header header;
        while ((header = bitstream.readFrame()) != null) {
            SampleBuffer output = (SampleBuffer) decoder.decodeFrame(header, bitstream);
            stereo = output.getChannelCount() == 2;

            short[] buffer = output.getBuffer();
            int length = output.getBufferLength();

            for (int i = 0; i < length; i++) {
                if (stereo) {
                    if ((i & 1) == 0)
                        left[index] = buffer[i] / 32768f;
                    else
                        right[index] = buffer[i] / 32768f;
                } else {
                    left[index] = buffer[i] / 32768f;
                }
                index++;
            }

            bitstream.closeFrame();
        }

        return new float[][]{
                trimArray(left, index),
                stereo ? trimArray(right, index) : null
        };
    }

    public static double getMP3Length() {
        if (fileFormat != null && fileFormat.properties().containsKey("duration")) {
            long micro = (Long) fileFormat.properties().get("duration");
            return micro / 1_000_000.0;
        }
        return -1;
    }

    public static int getMP3SampleRate() {
        return format != null ? (int) format.getSampleRate() : -1;
    }

    private static float[] trimArray(float[] array, int length) {
        float[] result = new float[length];
        System.arraycopy(array, 0, result, 0, length);
        return result;
    }
    //end of mp3 section
}
