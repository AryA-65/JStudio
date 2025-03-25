package org.JStudio.Utils;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;

public class WavData extends LinkedHashMap<WaveSection, byte[]> {
    static int HEADER_SIZE = 44; // There are 44 bits before the data section
    static int DEFAULT_SUBCHUNK1_SIZE = 16;
    static short DEFAULT_AUDIO_FORMAT = 1;
    static short DEFAULT_BLOCK_ALIGN = 4;
    static String DEFAULT_CHUNK_ID = "RIFF";
    static String DEFAULT_FORMAT = "WAVE";
    static String DEFAULT_SUBCHUNK1_ID = "fmt ";
    static String DEFAULT_SUBCHUNK2_ID = "data";

    public WavData(){
        this.put(WaveSection.CHUNK_ID, DEFAULT_CHUNK_ID);
        this.put(WaveSection.FORMAT, DEFAULT_FORMAT);
        this.put(WaveSection.SUBCHUNK1_ID, DEFAULT_SUBCHUNK1_ID);
        this.put(WaveSection.SUBCHUNK1_SIZE, DEFAULT_SUBCHUNK1_SIZE);
        this.put(WaveSection.AUDIO_FORMAT, DEFAULT_AUDIO_FORMAT);
        this.put(WaveSection.BLOCK_ALIGN, DEFAULT_BLOCK_ALIGN);
        this.put(WaveSection.SUB_CHUNK2_ID, DEFAULT_SUBCHUNK2_ID);

        this.put(WaveSection.CHUNK_SIZE, 0);
        this.put(WaveSection.SUB_CHUNK2_SIZE, 0);
        this.put(WaveSection.BYTE_RATE, 0);
    }

    public void put(WaveSection waveSection, String value){
        byte[] bytes = value.getBytes();
        this.put(waveSection, bytes);
    }

    public void put(WaveSection waveSection, int value) {
        byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
        this.put(waveSection, bytes);
    }

    public void put(WaveSection waveSection, short value) {
        byte[] bytes = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array();
        this.put(waveSection, bytes);
    }

    public byte[] getBytes(WaveSection waveSection) {
        return this.get(waveSection);
    }

    public String getString(WaveSection waveSection) {
        byte[] bytes = this.get(waveSection);
        return new String(bytes);
    }

    public int getInt(WaveSection waveSection) {
        byte[] bytes = this.get(waveSection);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public short getShort(WaveSection waveSection) {
        byte[] bytes = this.get(waveSection);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public void printByteInfo() {
        for (WaveSection waveSection : WaveSection.values()) {
            if (waveSection.getNumBytes() == 4
                    && waveSection.getEndian() == ByteOrder.BIG_ENDIAN) {
                System.out.println("SECTION:" + waveSection + ":STRING:"
                        + this.getString(waveSection));
            } else if (waveSection.getNumBytes() == 4
                    && waveSection.getEndian() == ByteOrder.LITTLE_ENDIAN) {
                System.out.println("SECTION:" + waveSection + ":INTEGER:"
                        + this.getInt(waveSection));
            } else if (waveSection.getNumBytes() == 2
                    && waveSection.getEndian() == ByteOrder.LITTLE_ENDIAN) {
                System.out.println("SECTION:" + waveSection + ":SHORT:"
                        + this.getShort(waveSection));
            } else {
                // Data Section
            }
        }
    }

    public void read(String inputPath) throws Exception {
        // Analyze redundant info
        int dataSize = (int) new File(inputPath).length() - HEADER_SIZE;
//        WaveSection.DATA.numBytes  = dataSize; // Can't have two threads using this at the same time

        // Read from File
        DataInputStream inFile = new DataInputStream(new FileInputStream(inputPath));

        for (WaveSection waveSection : WaveSection.values()) {
            byte[] readBytes = new byte[waveSection.getNumBytes()];
            for (int i = 0; i < waveSection.getNumBytes(); i++) {
                readBytes[i] = inFile.readByte();
            }
            this.put(waveSection, readBytes);
        }

        inFile.close();
    }

    public void write(String outputPath) throws Exception {
        // Analyze redundant info
        int dataSize = this.get(WaveSection.DATA).length;
        this.put(WaveSection.CHUNK_SIZE, dataSize+36);
        this.put(WaveSection.SUB_CHUNK2_SIZE, dataSize);

        int byteRate = this.getInt(WaveSection.SAMPLE_RATE)*this.getShort(WaveSection.BLOCK_ALIGN);
        this.put(WaveSection.BYTE_RATE, byteRate);

        // Write to File
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(outputPath));

        for (WaveSection waveSection : WaveSection.values()) {
            dataOutputStream.write(this.getBytes(waveSection));
        }

        dataOutputStream.close();
    }

    public AudioFormat createAudioFormat() {
        boolean audioSignedSamples = true; // Samples are signed
        boolean audioBigEndian = false;
        float sampleRate = (float) this.getInt(WaveSection.SAMPLE_RATE);
        int bitsPerSample = (int) this.getShort(WaveSection.BITS_PER_SAMPLE);
        int numChannels = (int) this.getShort(WaveSection.NUM_CHANNELS);
        return new AudioFormat(sampleRate, bitsPerSample,
                numChannels, audioSignedSamples, audioBigEndian);
    }
}

