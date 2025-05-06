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

    /**
     * Class that defines the mnath methods used in syntesizer
     */
    public static class Math {
        /**
         * Offsets a base frequency by a given multiplier in powers of two
         *
         * @param baseFrequency the starting frequency in Hz
         * @param frequencyMultiplier the power-of-two multiplier for frequency offset
         * @return the resulting frequency after offset
         */
        public static double offsetTone(double baseFrequency, double frequencyMultiplier) {
            return baseFrequency * pow(2.0, frequencyMultiplier);
        }

        /**
         * Converts a linear frequency (Hz) to angular frequency
         *
         * @param freq the frequency in Hz
         * @return the angular frequency in radians per second
         */
        public static double frequencyToAngularFrequency(double freq) {
            return 2 * PI * freq;
        }

        /**
         * Calculates the frequency (in Hz) of a piano key based on its key number.
         *
         * @param keyNum the MIDI key number
         * @return the frequency in hertz
         */
        public static double getKeyFrequency(int keyNum) {
            return pow(root(2, 12), keyNum - 49) * 440;
        }

        /**
         * Returns the frequency in Hz of a musical key number.
         *
         * @param num the key number
         * @return the frequency in Hz corresponding to the key number
         */
        public static double root(double num, double root) {
            return pow(E, log(num) / root);
        }
    }

    /**
     * Class that defines audio constants
     */
    public static class AudioInfo {
        public static final int SAMPLE_RATE = 44100;
        public static final int STARTING_KEY = 16;
        public static final int KEY_FREQUENCY_INCREMENT = 2;
        public static final char[] KEYS = "zxcvbnm,./asdfghjkl;'qwertyuiop[]#".toCharArray();
    }

}