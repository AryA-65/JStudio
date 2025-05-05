package org.JStudio.Utils;

public class TimeConverter {
    public static String longToString(long time) {
        long minutes = (time / 60000);
        long seconds = (time % 60000) / 1000;
        long milliseconds = time % 1000;

        return String.format("%d:%02d:%02d", minutes, seconds, milliseconds);
    }

    public static String doubleToString(double time) {
        long minutes = (long) (time / 60);
        long seconds = (long) (time % 60);
        long milliseconds = (long) ((time * 1000) % 1000);

        return String.format("%d:%02d:%02d", minutes, seconds, milliseconds);
    }
}
