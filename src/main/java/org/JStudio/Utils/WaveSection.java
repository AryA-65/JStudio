package org.JStudio.Utils;

import java.nio.ByteOrder;

public enum WaveSection {
    // https://stackoverflow.com/questions/10397272/wav-file-convert-to-byte-array-in-java
    // 12 Bytes
    CHUNK_ID(4, ByteOrder.BIG_ENDIAN),
    CHUNK_SIZE(4, ByteOrder.LITTLE_ENDIAN),
    FORMAT(4, ByteOrder.BIG_ENDIAN),

    // 24 Bytes
    SUBCHUNK1_ID(4, ByteOrder.BIG_ENDIAN),
    SUBCHUNK1_SIZE(4, ByteOrder.LITTLE_ENDIAN),
    AUDIO_FORMAT(2, ByteOrder.LITTLE_ENDIAN),
    NUM_CHANNELS(2, ByteOrder.LITTLE_ENDIAN),
    SAMPLE_RATE(4, ByteOrder.LITTLE_ENDIAN),
    BYTE_RATE(4, ByteOrder.LITTLE_ENDIAN),
    BLOCK_ALIGN(2, ByteOrder.LITTLE_ENDIAN),
    BITS_PER_SAMPLE(2, ByteOrder.LITTLE_ENDIAN),

    // 8 Bytes
    SUB_CHUNK2_ID(4, ByteOrder.BIG_ENDIAN),
    SUB_CHUNK2_SIZE(4, ByteOrder.LITTLE_ENDIAN),
    DATA(0, ByteOrder.LITTLE_ENDIAN);

    private Integer numBytes;
    private ByteOrder endian;
    WaveSection(Integer numBytes, ByteOrder endian){
        this.numBytes = numBytes;
        this.endian = endian;
    }

    public Integer getNumBytes() {
        return numBytes;
    }

    public void setNumBytes(Integer numBytes) {
        this.numBytes = numBytes;
    }

    public ByteOrder getEndian() {
        return endian;
    }

    public void setEndian(ByteOrder endian) {
        this.endian = endian;
    }
}
