package org.JStudio.Plugins;

import org.JStudio.Utils.FFTHandler;

import java.util.ArrayList;

public class Analyzer {
    private FFTHandler fftHandler = new FFTHandler();
    private boolean paused = false;
    private ArrayList<Float> buffer = new ArrayList<>(1024);

    public boolean put(float data) {
        if (buffer.size() >= 1024) return true;
        buffer.add(data);
        return false;
    }

    public double[] process() { //processes every 1024 chunks
        fftHandler.processFFT(buffer);
        return fftHandler.getFFTData();
    }
}
