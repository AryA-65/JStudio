package org.JStudio.Utils;

/**
 * Class to convert the time of
 */
public class TimeConverter {
    /**
     * Converts a duration in milliseconds to a formatted string "MM:SS:ms".
     *
     * @param time The duration in milliseconds.
     * @return A string representing the time in the format "minutes:seconds:milliseconds"
     */
    public static String longToString(long time) {
        long minutes = (time / 60000);
        long seconds = (time % 60000) / 1000;
        long milliseconds = time % 1000;

        return String.format("%d:%02d:%02d", minutes, seconds, milliseconds);
    }

    /**
     * Converts a duration in seconds (as a double) to a formatted string "MM:SS:ms".
     *
     * @param time The duration in seconds (can include a decimal fraction).
     * @return A string representing the time in the format "minutes:seconds:milliseconds
     */
    public static String doubleToString(double time) {
        long minutes = (long) (time / 60);
        long seconds = (long) (time % 60);
        long milliseconds = (long) ((time * 1000) % 1000);

        return String.format("%d:%02d:%02d", minutes, seconds, milliseconds);
    }
}
