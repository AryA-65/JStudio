package org.JStudio.Utils;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that extracts audio files
 * Allows MP3 functionality
 */
public class AudioFileExtractor {
    private static AudioInputStream ais;
    private static AudioFormat format;
    private static AudioFileFormat fileFormat;
    private static boolean file_type;

    public static boolean isMp3() {
        return file_type;
    }

    /**
     * Method that reads an audio file
     * @param file The path of the wav file to read
     * @return the read file
     * @throws Exception file not found
     */
    public static float[][] readFile(File file) throws Exception {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".mp3")) {
            file_type = true;
            return readMp3(file);
        } else if (name.endsWith(".wav")) {
            file_type = false;
            return readWavFile(file);
        } else {
            throw new IllegalArgumentException("Unsupported file format");
        }
    }

    /**
     * Method that reads a wav type audio file
     * @param file The path of the wav file to read
     * @return a double float array of the audio file
     * @throws UnsupportedAudioFileException audio file type is not supported
     * @throws IOException file not found
     */
    public static float[][] readWavFile(File file) throws UnsupportedAudioFileException, IOException {
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

    /**
     * Method that gets the wav length
     * @return The length of the wav file in seconds, or -1 if the file is not loaded or the format is invalid.
     */
    public static double getWavLength() {
        return ais != null && format != null
                ? ais.getFrameLength() / format.getSampleRate()
                : -1;
    }

    /**
     * Method that returns the sample rate of an audio file
     * @return The sample rate in Hz, or -1 if the file is not loaded
     */
    public static int getSampleRate() {
        return format != null ? (int) format.getSampleRate() : -1;
    }

    /**
     * Down samples provided audio samples to the specified target size.
     *
     * @param samples    The audio samples to
     * @param targetSize The target size of the array.
     * @return A down sampled array of audio samples.
     */
    public static float[] downSample(float[] samples, int targetSize) {
        float[] result = new float[targetSize];
        int step = samples.length / targetSize;
        for (int i = 0; i < targetSize; i++) {
            result[i] = samples[i * step];
        }
        return result;
    }

    /**
     * Reads an MP3 audio file and extracts stereo audio samples
     *
     * @param file The path of the MP3 file to read
     * @return A 2D float array containing the left and right channel samples
     * @throws Exception If there is an error while reading the file
     */
    private static float[][] readMp3(File file) throws Exception {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        Bitstream bitstream = new Bitstream(inputStream);
        Decoder decoder = new Decoder();

        fileFormat = AudioSystem.getAudioFileFormat(file);
        format = AudioSystem.getAudioInputStream(file).getFormat();

        List<Float> leftList = new ArrayList<>();
        List<Float> rightList = new ArrayList<>();
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
                        leftList.add(buffer[i] / 32768f);
                    else
                        rightList.add(buffer[i] / 32768f);
                } else {
                    leftList.add(buffer[i] / 32768f);
                }
            }

            bitstream.closeFrame();
        }

        float[] left = toFloatArray(leftList);
        float[] right = stereo ? toFloatArray(rightList) : null;

        return new float[][]{left, right};
    }

    /**
     * Returns the duration of the currently loaded MP3 file in seconds
     *
     * @return The length of the MP3 file in seconds, or -1 if the file is not loaded
     */
    public static double getMP3Length() {
        if (fileFormat != null && fileFormat.properties().containsKey("duration")) {
            long micro = (Long) fileFormat.properties().get("duration");
            return micro / 1_000_000.0;
        }
        return -1;
    }

    /**
     * Returns the sample rate of the currently loaded MP3 file
     *
     * @return The sample rate in Hz, or -1 if the file is not loaded
     */
    public static int getMP3SampleRate() {
        return format != null ? (int) format.getSampleRate() : -1;
    }

    /**
     * Converts a list of float objects to a primitive float array
     *
     * @param list The list of float objects to convert.
     * @return A primitive float array containing the values of the list
     */
    private static float[] toFloatArray(List<Float> list) {
        float[] result = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }
}
