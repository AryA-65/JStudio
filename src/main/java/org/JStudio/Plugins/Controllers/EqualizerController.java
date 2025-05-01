package org.JStudio.Plugins.Controllers;

import java.io.ByteArrayInputStream;
import org.JStudio.Plugins.Models.EqualizerBand;
import org.JStudio.Plugins.Views.EqualizerView;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.*;
import org.jtransforms.fft.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class EqualizerController extends Thread {

    private AudioFormat audioFormat;
    private float sampleRate;
    private int fftSize;
    private DoubleFFT_1D fft;
    private byte[] audioBytes;
    private byte[] processedBytes;
    private short[] samples;
    private double[] fftData;
    private EqualizerView eqView;
    private SourceDataLine line;
    private File file;
    private Stage stage;
    private StringProperty name = new SimpleStringProperty("Equalizer");

    public EqualizerController(File audioFile) {
        this.file = audioFile;
    }

    //getters and setters
    public void setEqView(EqualizerView eqView) {
        this.eqView = eqView;
    }

    public SourceDataLine getLine() {
        return line;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void run() {

        try {
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

    public void export() {
        String userHome = System.getProperty("user.home");
        File downloadsDir = new File(userHome, "Downloads");

        ByteArrayInputStream bais = new ByteArrayInputStream(processedBytes);

        // Now create an AudioInputStream
        AudioInputStream ais = new AudioInputStream(bais, audioFormat, processedBytes.length / audioFormat.getFrameSize());

        File outputFile = new File(downloadsDir, "Processed_" + file.getName());

        try {
            // Write the AudioInputStream to a WAV file
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);
            WindowEvent we = new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST);
            Event.fireEvent(stage, we);
            stage.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
