package org.JStudio.Utils;

import java.io.File;

public class Checksum {
    public static boolean fileExists(String filename) {
        return new File(filename).exists();
    }

    public static boolean folderExists(String filename) {
        return new File(filename).exists();
    }

    public static void createFolder(String filename) {
        File folder = new File(filename);
        folder.mkdir();
    }
}
