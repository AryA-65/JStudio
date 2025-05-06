package org.JStudio.Utils;

public class Converter {
    public static byte[] floatToByteArray(float[][] buff, int buff_size) {
        byte[] output = new byte[buff_size * 4];
        int index = 0;

        for (int i = 0; i < buff_size; i++) {
            for (int j = 0; j < 2; j++) {
                int sample = (int) (Math.max(-1f, Math.min(1f, buff[j][i])) * 32767);
                output[index++] = (byte) (sample & 0xff);
                output[index++] = (byte) ((sample >> 8) & 0xff);
            }
        }

        return output;
    }
}
