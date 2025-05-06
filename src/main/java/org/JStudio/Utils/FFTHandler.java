package org.JStudio.Utils;

import org.jtransforms.fft.DoubleFFT_1D;
import java.util.ArrayList;

/**
 * Class that handles FFT operations.
 */
public class FFTHandler {
   private double[] fftData;
   private int fftSize = 1024;
   private DoubleFFT_1D fft;

    /**
     * Creates a new FFT instance with default size (1024)
     */
   public FFTHandler() {
       fftData = new double[fftSize * 2];
       fft = new DoubleFFT_1D(fftSize);
   }

    /**
     * Creates a new FFT instance with specified size
     * @param size Size of the FFT window
     */
   public FFTHandler(int size) {
       fftSize = size;
       fftData = new double[fftSize * 2];
       fft = new DoubleFFT_1D(fftSize);
   }

    /**
     * Processes FFT on a list of float samples
     * @param inputData List of float samples
     */
   public void processFFT(ArrayList<Float> inputData) {
       for (int i = 0; i < fftSize * 2; i++) {
           fftData[i * 2] = inputData.get(i);
           fftData[i * 2 + 1] = 0;
       }

       fft.realForward(fftData);
   }
    /**
     * Processes FFT on a list of short samples
     * @param inputData List of short samples
     */
   public void processFFT(short[] inputData) {
       for (int i = 0; i < fftSize * 2; i++) {
           fftData[i * 2] = inputData[i];
           fftData[i * 2 + 1] = 0;
       }

       fft.realForward(fftData);
   }

    /**
     * Returns the computed FFT data.
     * @return The FFT output array.
     */
   public double[] getFFTData() {
       return fftData;
   }

}
