package org.JStudio;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileChooserController {

    @FXML
    private ScrollPane cache_scroll;
    @FXML
    private ImageView select_file;
    @FXML
    private ImageView close_btn;
    @FXML
    private VBox files_cache;
    @FXML
    private HBox top_bar;
    @FXML
    private Stage rootStage;

    //normal params
    private double xOffset;
    private double yOffset;
    private Queue<Map.Entry<String, String>> cache;
    String cachePath = System.getenv("LOCALAPPDATA") + "\\JStudio\\cache\\file_chooser.bin";
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public void setStage(Stage stage) {
        this.rootStage = stage;
    }

    //make hashmap into a Deque with entries
    @FXML
    public void initialize() {
        //init cache
        cache = new LinkedList<>();

        openCache();

        //init buttons/images
        select_file.setImage(new Image("/icons/load.png"));
        close_btn.setImage(new Image("/icons/close.png"));

        //init element functions
        top_bar.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            xOffset = rootStage.getX() - event.getScreenX();
            yOffset = rootStage.getY() - event.getScreenY();
        });

        top_bar.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            rootStage.setX(event.getScreenX() + xOffset);
            rootStage.setY(event.getScreenY() + yOffset);
        });

        select_file.setOnMouseClicked(event -> {
            String file = openFiles().getAbsolutePath();
            Date curDate = new Date();

            if (file != null) {
                if (cache.contains(new AbstractMap.SimpleEntry<>(file, curDate))) {return;}

                if (cache.size() > 255) {
                    cache.poll();
                    System.out.println("Removed last entry");
                }

                String formattedDate = sdf.format(curDate);
                cache.add(new AbstractMap.SimpleEntry<>(file, formattedDate));
                addEntryToList();
//                sortCache();
            }
        });

        close_btn.setOnMouseClicked(event -> {
            writeCache();
            rootStage.close();
        });

        cache_scroll.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.isShiftDown()) {
                double delta = e.getDeltaX() * 0.005;
                cache_scroll.setHvalue(cache_scroll.getHvalue() - delta);
                e.consume();
            }
        });
    }

    private File openFiles() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Open File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.ogg"),
                new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        return fileChooser.showOpenDialog(rootStage);
    }

    private void openCache() {
        File cacheFile = new File(cachePath);

        //init cache file
        if (!cacheFile.exists()) {
            try {
                Files.createFile(Paths.get(cachePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        try (DataInputStream in = new DataInputStream(new FileInputStream(cacheFile))) {
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                String fileName = in.readUTF();
                String fileDate = in.readUTF();
                cache.add(new AbstractMap.SimpleEntry<>(fileName, fileDate));
                addEntryToList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Cache successfully opened");
    }

    private void writeCache() {
        File cacheFile = new File(cachePath);

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(cacheFile))) {
            dos.writeInt(cache.size());
            for (Map.Entry<String, String> entry : cache) {
                dos.writeUTF(entry.getKey());
                dos.writeUTF(entry.getValue());
            }
            System.out.printf("Cache saved!. Cache size %d.\n", cache.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //make one to added single entries when loading, and one to clear and add entries when opening a new file
    private void addEntryToList() {
        files_cache.getChildren().clear();

        for (Map.Entry<String, String> entry : cache) {
            HBox fileBox = new HBox();
            fileBox.setSpacing(5);
            Label fileLocation = new Label(entry.getKey());
            Label lastOpen = new Label(entry.getValue());
            lastOpen.setMinWidth(Region.USE_COMPUTED_SIZE);
            fileBox.getChildren().addAll(fileLocation, lastOpen);

            fileBox.setOnDragDetected(event -> {
                Dragboard db = fileBox.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.putString(entry.getKey());
                db.setContent(content);
                event.consume();
            });

            files_cache.getChildren().add(fileBox);
        }
    }
}

