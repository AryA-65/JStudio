package org.JStudio.Plugins.Models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PluginTest {

    
    /**
     * Test of convertFileToByteTestingMethod method, of class Plugin.
     */
    @Test
    public void testConvertFileToByteTestingMethod() {
        String filePathName1 = "\\src\\main\\resources\\JUnitAudioTestFiles\\cowbell-74767.wav";
        String filePathName2 = "\\src\\main\\resources\\JUnitAudioTestFiles\\dirty-tony-cowbell-mx-013-107592.wav";
        String filePathName3 = "\\src\\main\\resources\\JUnitAudioTestFiles\\lp-cowbell-83904.wav";
        String filePathName4 = "\\src\\main\\resources\\JUnitAudioTestFiles\\phonk-cowbell-1-200542.wav";
        String filePathName5 = "\\src\\main\\resources\\JUnitAudioTestFiles\\synth-cowbell-high-169970.wav";
        String filePathName6 = "\\src\\main\\resources\\JUnitAudioTestFiles\\synth-cowbell-low-169971.wav";
        String filePathName7 = "\\src\\main\\resources\\JUnitAudioTestFiles\\underwater-white-noise-46423.wav";
        String filePathName8 = "\\src\\main\\resources\\JUnitAudioTestFiles\\white-noise-179828.wav";
        String filePathName9 = "\\src\\main\\resources\\JUnitAudioTestFiles\\white-noise-sweep-up-85276.wav";
        String filePathName10 = "\\src\\main\\resources\\JUnitAudioTestFiles\\woosh-13225.wav";
        
        Reverb plugin = new Reverb(1,1,1,1);
        assertEquals(plugin.convertFileToByteTestingMethod(filePathName1),plugin.convertFileToByteTestingMethod(filePathName1));
        assertEquals(plugin.convertFileToByteTestingMethod(filePathName2),plugin.convertFileToByteTestingMethod(filePathName2));
        assertEquals(plugin.convertFileToByteTestingMethod(filePathName3),plugin.convertFileToByteTestingMethod(filePathName3));
        assertEquals(plugin.convertFileToByteTestingMethod(filePathName4),plugin.convertFileToByteTestingMethod(filePathName4));
        assertEquals(plugin.convertFileToByteTestingMethod(filePathName5),plugin.convertFileToByteTestingMethod(filePathName5));
        assertEquals(plugin.convertFileToByteTestingMethod(filePathName6),plugin.convertFileToByteTestingMethod(filePathName6));
        assertEquals(plugin.convertFileToByteTestingMethod(filePathName7),plugin.convertFileToByteTestingMethod(filePathName7));
        assertEquals(plugin.convertFileToByteTestingMethod(filePathName8),plugin.convertFileToByteTestingMethod(filePathName8));
        assertEquals(plugin.convertFileToByteTestingMethod(filePathName9),plugin.convertFileToByteTestingMethod(filePathName9));
        assertEquals(plugin.convertFileToByteTestingMethod(filePathName10),plugin.convertFileToByteTestingMethod(filePathName10));
    }
}