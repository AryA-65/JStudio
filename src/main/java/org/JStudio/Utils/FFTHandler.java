package org.JStudio.Utils;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;

public class FFTHandler {
   private double[] fftData;
   private int fftSize = 1024;
   private DoubleFFT_1D fft;

   public FFTHandler() {
       fftData = new double[fftSize * 2];
       fft = new DoubleFFT_1D(fftSize);
       fftData = new double[fftSize * 2];
   }

   public FFTHandler(int size) {
       fftSize = size;
       fftData = new double[fftSize * 2];
       fft = new DoubleFFT_1D(fftSize);
   }

   public void processFFT(ArrayList<Float> inputData) {
       for (int i = 0; i < fftSize * 2; i++) {
           fftData[i * 2] = inputData.get(i);
           fftData[i * 2 + 1] = 0;
       }

       fft.realForward(fftData);
   }

   public void processFFT(short[] inputData) {
       for (int i = 0; i < fftSize * 2; i++) {
           fftData[i * 2] = inputData[i];
           fftData[i * 2 + 1] = 0;
       }

       fft.realForward(fftData);
   }

   public double[] getFFTData() {
       return fftData;
   }

}
