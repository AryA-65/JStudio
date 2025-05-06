package org.JStudio.Utils;

import java.io.File;

/**
 * Class that checks if a directory exists or not
 */
public class Checksum {
    /**
     * Method that checks whether a file exists or not
     * @param filename the name of the searched file
     * @return a boolean value {true if it exists}
     */
    public static boolean fileExists(String filename) {
        return new File(filename).exists();
    }

    /**
     * Method that checks whether a folder exists or not
     * @param filename the name of the searched folder
     * @return a boolean value {true if it exists}
     */
    public static boolean folderExists(String filename) {
        return new File(filename).exists();
    }

    /**
     * Method that creates a folder
     * @param filename the file path for the creation of the desired directory
     */
    public static void createFolder(String filename) {
        File folder = new File(filename);
        folder.mkdir();
    }

}
