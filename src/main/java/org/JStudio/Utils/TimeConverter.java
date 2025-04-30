package org.JStudio.Utils;

public class TimeConverter {
    public static String longToString(long time) {
        long minutes = (time / 60000);
        long seconds = (time % 60000) / 1000;
        long milliseconds = time % 1000;

        return String.format("%d:%02d:%02d", minutes, seconds, milliseconds);
    }
}
