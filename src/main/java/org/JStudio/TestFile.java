package org.JStudio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class TestFile {
    File file;
    String name;
//    LinkedList<Float> data;

    TestFile(File file) throws Exception {
        this.file = file;
//        data(file);
//        System.out.println(data);
    }

//    public void data(File file) throws Exception {
//        try {
//            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
//            AudioFormat format = audioInputStream.getFormat();
//            byte[] audioBytes = audioInputStream.readAllBytes();
//
//            // Number of bytes per sample
//            int bytesPerSample = format.getSampleSizeInBits() / 8;
//            int frameSize = format.getFrameSize();
//
//            for (int i = 0; i < audioBytes.length; i += frameSize) {
//                // Extract sample data (considering stereo or mono)
//                int sample = 0;
//                for (int byteIndex = 0; byteIndex < bytesPerSample; byteIndex++) {
//                    sample |= (audioBytes[i + byteIndex] & 0xFF) << (byteIndex * 8);
//                }
//
//                // Normalize the sample data and add it to the linked list
//                float normalizedSample = sample / (float) Math.pow(2, format.getSampleSizeInBits() - 1);
//                System.out.println(normalizedSample);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
