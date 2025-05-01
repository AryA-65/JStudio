package org.JStudio.Utils;

import javafx.scene.layout.VBox;

import org.JStudio.UI.SectionUI;

import java.io.*;
import java.util.*;

public class FileLoader {
    private static VBox tab_vbox;

    private static String musicPath;

    public static void init(VBox vbox) {
        tab_vbox = vbox;

        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        if (os.contains("win")) {
            musicPath = userHome + "\\Music\\JStudio\\audio_Files";
        } else if (os.contains("mac")) {
            musicPath = userHome + "/Music/JStudio/audio_Files";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            musicPath = userHome + "/Music/JStudio/audio_Files";
        } else {
            throw new RuntimeException("Unsupported OS");
        }

        try {
            loadFolders(musicPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadFolders(String path) throws Exception {
        File file = new File(path);
        if (file.exists() && file.isDirectory() && file.listFiles() != null) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                if (f.isDirectory()) {
                    tab_vbox.getChildren().add(new SectionUI(f));
                }
            }
        } else if (!file.exists()) {
            throw new Exception("Folder does not exist");
        }
    }

    public static String getMusicPath() {
        return musicPath;
    }
}
