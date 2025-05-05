package org.JStudio.Plugins.Models;

import java.io.File;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PluginTest {

    
    /**
     * Test of convertFileToByteTestingMethod method, of class Plugin.
     */
    @Test
    public void testConvertFileToByteTestingMethod() {
        String filePathName1 = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "JUnitAudioTestFiles" + File.separator + "cowbell-74767.wav";
        String filePathName2 = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "JUnitAudioTestFiles" + File.separator + "dirty-tony-cowbell-mx-013-107592.wav";
        String filePathName3 = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "JUnitAudioTestFiles" + File.separator + "lp-cowbell-83904.wav";
        String filePathName4 = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "JUnitAudioTestFiles" + File.separator + "phonk-cowbell-1-200542.wav";
        String filePathName5 = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "JUnitAudioTestFiles" + File.separator + "synth-cowbell-high-169970.wav";
        String filePathName6 = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "JUnitAudioTestFiles" + File.separator + "synth-cowbell-low-169971.wav";
        String filePathName7 = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "JUnitAudioTestFiles" + File.separator + "underwater-white-noise-46423.wav";
        String filePathName8 = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "JUnitAudioTestFiles" + File.separator + "white-noise-179828.wav";
        String filePathName9 = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "JUnitAudioTestFiles" + File.separator + "white-noise-sweep-up-85276.wav";
        String filePathName10 = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "JUnitAudioTestFiles" + File.separator + "woosh-13225.wav";
        
        PluginTesting plugin = new PluginTesting();
        assertArrayEquals(plugin.convertFileToByteTestingMethod(filePathName1),plugin.convertFileToByteTestingMethod(filePathName1));
        assertArrayEquals(plugin.convertFileToByteTestingMethod(filePathName2),plugin.convertFileToByteTestingMethod(filePathName2));
        assertArrayEquals(plugin.convertFileToByteTestingMethod(filePathName3),plugin.convertFileToByteTestingMethod(filePathName3));
        assertArrayEquals(plugin.convertFileToByteTestingMethod(filePathName4),plugin.convertFileToByteTestingMethod(filePathName4));
        assertArrayEquals(plugin.convertFileToByteTestingMethod(filePathName5),plugin.convertFileToByteTestingMethod(filePathName5));
        assertArrayEquals(plugin.convertFileToByteTestingMethod(filePathName6),plugin.convertFileToByteTestingMethod(filePathName6));
        assertArrayEquals(plugin.convertFileToByteTestingMethod(filePathName7),plugin.convertFileToByteTestingMethod(filePathName7));
        assertArrayEquals(plugin.convertFileToByteTestingMethod(filePathName8),plugin.convertFileToByteTestingMethod(filePathName8));
        assertArrayEquals(plugin.convertFileToByteTestingMethod(filePathName9),plugin.convertFileToByteTestingMethod(filePathName9));
        assertArrayEquals(plugin.convertFileToByteTestingMethod(filePathName10),plugin.convertFileToByteTestingMethod(filePathName10));
    }
}