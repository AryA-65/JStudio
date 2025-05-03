package org.JStudio.Plugins.Controllers;

import org.JStudio.Plugins.Models.EqualizerBand;
import org.jtransforms.fft.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.JStudio.Plugins.Models.Equalizer;
import org.JStudio.Plugins.Models.Plugin;

public class EqualizerController extends Plugin {

    Equalizer equalizer = new Equalizer();

    public Equalizer getEqualizer() {
        return equalizer;
    }

    public EqualizerController() {
        convertAudioFileToByteArray();
    }

    public void processAudio(float[] float2DInput) {

//        try {

            //turn file into audio input stream
            //AudioInputStream ais = AudioSystem.getAudioInputStream(equalizer.getFile());
            //equalizer.setAudioFormat(ais.getFormat()); //get format of audio to ensure proper conversion later
            //equalizer.setSampleRate(equalizer.getAudioFormat().getSampleRate()); //get the sample rate
            //equalizer.setAudioBytes(ais.readAllBytes()); //get array of all bytes from the stream
            //ais.close();
            //wav files use 16 bit audio format so each data point takes 2 bytes
            //convert the byte array into a short array since a short is 16 bits
            //now every element in the short array is the amplitude of a data point

                equalizer.setSamples(new short[audioByteInput.length / 2]);
                ByteBuffer.wrap(audioByteInput).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(equalizer.getSamples());
                //equalizer.setSamples(new short[equalizer.getAudioBytes().length / 2]);
                //ByteBuffer.wrap(equalizer.getAudioBytes()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(equalizer.getSamples());

                //FFT
                // Convert short array to double array (for FFT)
                equalizer.setFftSize(equalizer.getSamples().length);
                equalizer.setFftData(new double[equalizer.getFftSize() * 2]);
                for (int i = 0; i < equalizer.getFftSize(); i++) {
                    equalizer.getFftData()[2 * i] = equalizer.getSamples()[i];  // Real part
                    equalizer.getFftData()[2 * i + 1] = 0;       // Imaginary part (zero)
                }

                // Apply FFT to transform into frequency domain
                equalizer.setFft(new DoubleFFT_1D(equalizer.getFftSize()));
                equalizer.getFft().realForward(equalizer.getFftData());

                //get equalizer bands
                EqualizerBand[] eqBands = equalizer.getEqView().getEqBands();

                // Manipulate magnitude of frequencies
                for (int i = 0; i < equalizer.getFftSize(); i++) {
                    double frequency = (i * equalizer.getSampleRate()) / equalizer.getFftSize();
                    for (int j = 0; j < eqBands.length; j++) {
                        //check if the frequency of each sample is in range of each band
                        if (frequency >= eqBands[j].getLowFrequency() && frequency <= eqBands[j].getHighFrequency()) {
                            // Manipulate the magnitude by Manipulating the real and imaginary parts
                            equalizer.getFftData()[2 * i] *= eqBands[j].getValue(); // real part
                            equalizer.getFftData()[2 * i + 1] *= eqBands[j].getValue(); // imaginary part
                            break;
                        }
                    }
                }

                // Inverse FFT to get back to time domain
                equalizer.getFft().realInverse(equalizer.getFftData(), true);

                //convert double array back to short array
                for (int i = 0; i < equalizer.getSamples().length; i++) {
                    // Ensure it's within the valid range for short
                    equalizer.getSamples()[i] = (short) Math.min(Math.max(equalizer.getFftData()[2 * i], Short.MIN_VALUE), Short.MAX_VALUE);
                }

                //convert short array back to a byte array
                ByteBuffer buffer = ByteBuffer.allocate(equalizer.getSamples().length * 2);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                for (short sample : equalizer.getSamples()) {
                    buffer.putShort(sample);
                }
                equalizer.setProcessedBytes(buffer.array());

            setFinalAudio(equalizer.getProcessedBytes());

            play();

//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
    }

//    public void playAudio(){
//        equalizer.setAudioThread(new Thread(() -> {
//            try {
//                //use source data line to play the byte array
//                DataLine.Info info = new DataLine.Info(SourceDataLine.class, equalizer.getAudioFormat());
//                equalizer.setLine((SourceDataLine) AudioSystem.getLine(info));
//                equalizer.getLine().open(equalizer.getAudioFormat());
//                equalizer.getLine().start();
//
//                // write the audio bytes to play
//                equalizer.getLine().write(equalizer.getProcessedBytes(), 0, equalizer.getProcessedBytes().length);
//
//                // close after finishing playing
//                equalizer.getLine().drain();
//                equalizer.getLine().close();
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//        }));
//        
//        equalizer.getAudioThread().start();
//    }
//    //close the line that is playing the audio
//    public void stopPlaying() {
//        if (equalizer.getLine() != null) {
//            equalizer.getLine().close();
//        }
//        equalizer.getAudioThread().interrupt(); //interrupt the thread
//    }
//    public void export() {
//        String userHome = System.getProperty("user.home");
//        File downloadsDir = new File(userHome, "Downloads");
//
//        ByteArrayInputStream bais = new ByteArrayInputStream(processedBytes);
//
//        // Now create an AudioInputStream
//        AudioInputStream ais = new AudioInputStream(bais, equalizer.getAudioFormat(), processedBytes.length / equalizer.getAudioFormat().getFrameSize());
//
//        File outputFile = new File(downloadsDir, "Processed_" + file.getName());
//
//        try {
//            // Write the AudioInputStream to a WAV file
//            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);
//            WindowEvent we = new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST);
//            Event.fireEvent(stage, we);
//            stage.close();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
}
