package org.JStudio.Utils;

import javafx.scene.layout.VBox;
import org.JStudio.UI.SectionUI;

import java.io.*;
import java.nio.file.Files;
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
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            musicPath = userHome + "/Music/JStudio/audio_Files";
        } else {
            throw new RuntimeException("Unsupported OS");
        }

        File targetDir = new File(musicPath);
        File sourceDir = new File("src/main/resources/audio_Files");

        // Only attempt to move if source exists and target doesn't
        if (!targetDir.exists()) {
            if (sourceDir.exists()) {
                try {
                    Files.createDirectories(targetDir.getParentFile().toPath()); // Ensure parent exists
                    Files.move(sourceDir.toPath(), targetDir.toPath());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to move audio_Files directory", e);
                }
            } else {
                System.err.println("Warning: Default audio_Files not found in resources. Creating empty folder instead.");
                targetDir.mkdirs(); 
            }
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
            System.gc();
        } else if (!file.exists()) {
            throw new Exception("Folder does not exist");
        }
    }

    public static String getMusicPath() {
        return musicPath;
    }

    // Method to create the folder only if it doesn't exist
    private static void createFolderIfNeeded(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }
}
