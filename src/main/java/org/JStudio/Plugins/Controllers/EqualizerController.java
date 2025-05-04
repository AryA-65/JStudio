package org.JStudio.Plugins.Controllers;

import org.JStudio.Plugins.Models.EqualizerBand;
import org.jtransforms.fft.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.JStudio.Plugins.Models.Equalizer;

public class EqualizerController {

    Equalizer equalizer;

    public Equalizer getEqualizer() {
        return equalizer;
    }

    public void setEqualizer(Equalizer equalizer) {
        this.equalizer = equalizer;
    }

    public void processAudio(float[] floatInput) {
        equalizer.setAudioByteInput(equalizer.convertFloatToByteArray(floatInput));
        
        equalizer.setOutputGain(equalizer.getEqView().getOutputGainSlider().getValue());

        //wav files use 16 bit audio format so each data point takes 2 bytes
        //convert the byte array into a short array since a short is 16 bits
        //now every element in the short array is the amplitude of a data point
        equalizer.setSampleRate(44100);

        equalizer.setSamples(new short[equalizer.getAudioByteInput().length / 2]);
        ByteBuffer.wrap(equalizer.getAudioByteInput()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(equalizer.getSamples());
        
        equalizer.setSamples(equalizer.outputGainAudio(equalizer.getSamples()));
        
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
        equalizer.setProcessedFloat(equalizer.convertByteToFloatArray(equalizer.getProcessedBytes()));
        equalizer.setFinalAudio(equalizer.getProcessedBytes());
    }
}
