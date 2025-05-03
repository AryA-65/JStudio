package EQoldTest;


import EQoldTest.EqualizerBand;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.*;
import org.jtransforms.fft.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EqualizerController extends Thread {

    private AudioFormat audioFormat;
    private float sampleRate;
    private int fftSize;
    private DoubleFFT_1D fft;
    private byte[] audioBytes;
    private byte[] processedBytes;
    private short[] samples;
    private double[] fftData;
    private EQoldTest.EqualizerView eqView;
    private SourceDataLine line;

    public void setEqView(EQoldTest.EqualizerView eqView) {
        this.eqView = eqView;
    }

    @Override
    public void run() {

        try {
            // get wav file
            File file = new File("C:\\Users\\The Workstation\\Music\\JStudio\\audio_Files\\SFXs\\woosh-13225.wav");

            //turn file into audio input stream
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            audioFormat = ais.getFormat(); //get format of audio to ensure proper conversion later
            sampleRate = audioFormat.getSampleRate(); //get the sample rate
            audioBytes = ais.readAllBytes(); //get array of all bytes from the stream
            ais.close();

            //wav files use 16 bit audio format so each data point takes 2 bytes
            //convert the byte array into a short array since a short is 16 bits
            //now every element in the short array is the amplitude of a data point
            samples = new short[audioBytes.length / 2];
            ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);

            //FFT
            // Convert short array to double array (for FFT)
            fftSize = samples.length;
            fftData = new double[fftSize * 2];
            for (int i = 0; i < fftSize; i++) {
                fftData[2 * i] = samples[i];  // Real part
                fftData[2 * i + 1] = 0;       // Imaginary part (zero)
            }

            // Apply FFT to transform into frequency domain
            fft = new DoubleFFT_1D(fftSize);
            fft.realForward(fftData);

            //get equalizer bands
            EqualizerBand[] eqBands = eqView.getEqBands();
            
            // Manipulate magnitude of frequencies
            for (int i = 0; i < fftSize; i++) {
                double frequency = (i * sampleRate) / fftSize;
                for (int j = 0; j < eqBands.length; j++) {
                    //check if the frequency of each sample is in range of each band
                    if (frequency >= eqBands[j].getLowFrequency() && frequency <= eqBands[j].getHighFrequency()) {
                        // Manipulate the magnitude by Manipulating the real and imaginary parts
                        fftData[2 * i] *= eqBands[j].getValue(); // real part
                        fftData[2 * i + 1] *= eqBands[j].getValue(); // imaginary part
                        break;
                    }
                }
            }

            // Inverse FFT to get back to time domain
            fft.realInverse(fftData, true);

            //convert double array back to short array
            for (int i = 0; i < samples.length; i++) {
                // Ensure it's within the valid range for short
                samples[i] = (short) Math.min(Math.max(fftData[2 * i], Short.MIN_VALUE), Short.MAX_VALUE);
            }

            //convert short array back to a byte array
            ByteBuffer buffer = ByteBuffer.allocate(samples.length * 2);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            for (short sample : samples) {
                buffer.putShort(sample);
            }

            processedBytes = buffer.array();

            //use source data line to play the byte array
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);
            line.start();

            // write the audio bytes to play
            line.write(processedBytes, 0, processedBytes.length);

            // close after finishing playing
            line.drain();
            line.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //close the line that is playing the audio
    public void stopPlaying() {
        if (line != null) {
            line.close();
        }
        this.interrupt(); //interrupt the thread
    }

}
