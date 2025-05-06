package org.JStudio.Plugins.SynthUtil;


import static java.lang.Math.*;
import static java.lang.Math.log;

/**
 * Handles different actions
 */
public class Utility {
    public static void handleProcedure(Procedure procedure, boolean printStackTrace) {
        try {
            procedure.invoke();
        } catch (Exception e) {

            if (printStackTrace) {
                e.printStackTrace();
            }
        }
    }

    public static class Math {
        public static double offsetTone(double baseFrequency, double frequencyMultiplier) {
            return baseFrequency * pow(2.0, frequencyMultiplier);
        }

        public static double frequencyToAngularFrequency(double freq) {
            return 2 * PI * freq;
        }

        public static double getKeyFrequency(int keyNum) {
            return pow(root(2, 12), keyNum - 49) * 440;
        }

        public static double root(double num, double root) {
            return pow(E, log(num) / root);
        }
    }

    public static class AudioInfo {
        public static final int SAMPLE_RATE = 44100;
        public static final int STARTING_KEY = 16;
        public static final int KEY_FREQUENCY_INCREMENT = 2;
        public static final char[] KEYS = "zxcvbnm,./asdfghjkl;'qwertyuiop[]#".toCharArray();
    }

}