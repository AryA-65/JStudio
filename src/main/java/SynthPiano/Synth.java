package SynthPiano;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuButton;
import javafx.stage.Stage;
import org.JStudio.Plugins.Controllers.PopUpController;

public class Synth {
    private final Map<MenuButton, String> waveformSelection = new HashMap<>();
    private final double[] oscillatorFrequencies = new double[3];
    private double frequency = 0;
    private final int NORMALIZER = 6;
    private int wavePos;
    private boolean shouldGenerate;
    private Random random = new Random();
    private String txt1 = "Sine", txt2 = "Sine", txt3 = "Sine";
    private AudioThread auTh;
    private Stage tempStage;
    private SynthPianoController notesController;
    private PopUpController popUpController;
    private GraphicsContext gc;

    //getters and setters
    public AudioThread getAuTh() {
        return auTh;
    }

    public double getFrequency() {
        return frequency;
    }

    public GraphicsContext getGc() {
        return gc;
    }

    public int getNormalizer() {
        return NORMALIZER;
    }

    public SynthPianoController getNotesController() {
        return notesController;
    }

    public double[] getOscillatorFrequencies() {
        return oscillatorFrequencies;
    }

    public PopUpController getPopUpController() {
        return popUpController;
    }

    public Random getRandom() {
        return random;
    }

    public Stage getTempStage() {
        return tempStage;
    }

    public String getTxt1() {
        return txt1;
    }

    public String getTxt2() {
        return txt2;
    }

    public String getTxt3() {
        return txt3;
    }

    public int getWavePos() {
        return wavePos;
    }

    public Map<MenuButton, String> getWaveformSelection() {
        return waveformSelection;
    }

    public boolean isShouldGenerate() {
        return shouldGenerate;
    }

    public void setAuTh(AudioThread auTh) {
        this.auTh = auTh;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public void setGc(GraphicsContext gc) {
        this.gc = gc;
    }

    public void setNotesController(SynthPianoController notesController) {
        this.notesController = notesController;
    }

    public void setPopUpController(PopUpController popUpController) {
        this.popUpController = popUpController;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setShouldGenerate(boolean shouldGenerate) {
        this.shouldGenerate = shouldGenerate;
    }

    public void setTempStage(Stage tempStage) {
        this.tempStage = tempStage;
    }

    public void setTxt1(String txt1) {
        this.txt1 = txt1;
    }

    public void setTxt2(String txt2) {
        this.txt2 = txt2;
    }

    public void setTxt3(String txt3) {
        this.txt3 = txt3;
    }

    public void setWavePos(int wavePos) {
        this.wavePos = wavePos;
    }
}
