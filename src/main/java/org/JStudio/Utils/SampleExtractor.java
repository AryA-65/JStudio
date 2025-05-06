package org.JStudio.Utils;

/**
 * Class to extract normalized audio samples from a byte array
 */
public class SampleExtractor {
    /**
     * Extracts a single normalized audio sample from a byte array based on the given bit depth.
     *
     * @param data The byte array containing PCM audio data.
     * @param index The starting index of the sample in the byte array.
     * @param bitDepth The bit depth of the sample (supported: 8, 16, or 24).
     * @return A float value between -1.0 and 1.0 representing the audio sample.
     */
    public static float extractSample(byte[] data, int index, int bitDepth) {
        int sample = 0;

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
        return sample;
    }
}
