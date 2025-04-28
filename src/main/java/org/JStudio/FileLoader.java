package org.JStudio;

import javafx.scene.layout.VBox;

import org.JStudio.UI.SectionUI;

import java.io.*;
import java.util.*;

public class FileLoader {
    private VBox tab_vbox;
    private String curUser;

    FileLoader(VBox tab_vbox) {
        this.tab_vbox = tab_vbox;
        curUser = System.getProperty("user.name");
        try {
            loadFolders("C:\\Users\\" + curUser + "\\Music\\JStudio\\audio_Files");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFolders(String path) throws Exception {
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
