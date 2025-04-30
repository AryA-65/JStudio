package org.JStudio.Utils;

import javafx.scene.layout.VBox;

import org.JStudio.UI.SectionUI;

import java.io.*;
import java.util.*;

public class FileLoader {
    private static VBox tab_vbox;

    public static void init(VBox vbox) {
        tab_vbox = vbox;
        try {
            loadFolders("C:\\Users\\" + System.getProperty("user.name") + "\\Music\\JStudio\\audio_Files");
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
}
