package org.JStudio.Plugins.Models;

import java.io.File;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import org.JStudio.Plugins.Views.EqualizerView;
import org.jtransforms.fft.DoubleFFT_1D;

public class Equalizer extends Plugin{
    private AudioFormat audioFormat;
    private float sampleRate;
    private int fftSize;
    private DoubleFFT_1D fft;
    private byte[] audioBytes;
    private byte[] processedBytes;
    private short[] samples;
    private float[] processedFloat;
    private double[] fftData;
    private EqualizerView eqView;
    private SourceDataLine line;
    private File file;
    private Stage stage;
    private StringProperty name = new SimpleStringProperty("Equalizer");
    private Thread audioThread;

    public Equalizer() {
        convertAudioFileToByteArray();
    }

    public byte[] getAudioBytes() {
        return audioBytes;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public EqualizerView getEqView() {
        return eqView;
    }

    public DoubleFFT_1D getFft() {
        return fft;
    }

    public double[] getFftData() {
        return fftData;
    }

    public int getFftSize() {
        return fftSize;
    }

    public File getFile() {
        return file;
    }

    public SourceDataLine getLine() {
        return line;
    }

    public byte[] getProcessedBytes() {
        return processedBytes;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public short[] getSamples() {
        return samples;
    }

    public Stage getStage() {
        return stage;
    }

    public Thread getAudioThread() {
        return audioThread;
    }

    public float[] getProcessedFloat() {
        return processedFloat;
    }

    public void setProcessedFloat(float[] processedFloat) {
        this.processedFloat = processedFloat;
    }

    public void setAudioThread(Thread audioThread) {
        this.audioThread = audioThread;
    }

    public void setAudioBytes(byte[] audioBytes) {
        this.audioBytes = audioBytes;
    }

    public void setAudioFormat(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
    }

    public void setEqView(EqualizerView eqView) {
        this.eqView = eqView;
    }

    public void setFft(DoubleFFT_1D fft) {
        this.fft = fft;
    }

    public void setFftData(double[] fftData) {
        this.fftData = fftData;
    }

    public void setFftSize(int fftSize) {
        this.fftSize = fftSize;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setLine(SourceDataLine line) {
        this.line = line;
    }

    public void setProcessedBytes(byte[] processedBytes) {
        this.processedBytes = processedBytes;
    }

    public void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setSamples(short[] samples) {
        this.samples = samples;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
}
