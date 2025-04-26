package org.JStudio;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;

public class FileLoader {
    private VBox tab_vbox;
    private String curUser;
    private int CANVAS_WIDTH = 234, CANVAS_HEIGHT = 64;
    private double fileLength = 0;
    private MediaPlayer mediaPlayer;

    FileLoader(VBox tab_vbox) {
        this.tab_vbox = tab_vbox;
        curUser = System.getProperty("user.name");
//        System.out.println(curUser);
        try {
            loadFolders("C:\\Users\\" + curUser + "\\Music\\JStudio\\audio_Files");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFolders(String path) throws Exception {
//        System.out.println(path);


        File file = new File(path);
        if (file.exists() && file.isDirectory() && file.listFiles() != null) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                if (f.isDirectory()) {
//                    sections.add((VBox) section);
                    tab_vbox.getChildren().add(audioSection(f));
                }
            }
        }
    }

    public Node audioSection(File f) {
        ImageView imageView = new ImageView(new Image("/icons/arrow.png"));
        imageView.setSmooth(true);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);

        VBox rootVBox = new VBox();
        rootVBox.setId(f.getName());
//        VBox.setMargin(rootVBox, new Insets(0,0,0,0));

        Button expandSectionBtn = new Button(f.getName(), imageView);
        expandSectionBtn.setPrefWidth(256 - (5 * 2));
        expandSectionBtn.setAlignment(Pos.TOP_LEFT);
        VBox.setMargin(expandSectionBtn, new Insets(0, 5, 10, 5));

        VBox fileSectionList = new VBox();
        fileSectionList.setAlignment(Pos.TOP_LEFT);

        VBox.setMargin(fileSectionList, new Insets(0, 10, 0, 10));

        Timeline expandTimeline = new Timeline(
                new KeyFrame(Duration.millis(150), new KeyValue(fileSectionList.prefHeightProperty(), Region.USE_COMPUTED_SIZE, Interpolator.EASE_IN))
        );

        Timeline shrinkTimeline = new Timeline(
                new KeyFrame(Duration.millis(150), new KeyValue(fileSectionList.prefHeightProperty(), 0, Interpolator.EASE_OUT))
        );

        RotateTransition rotateTransition = new RotateTransition(Duration.millis(150), imageView);
        rotateTransition.setByAngle(-180);

        expandSectionBtn.setOnAction(e -> {
            if (fileSectionList.isVisible()) {
                rotateTransition.play();
                shrinkTimeline.play();
                shrinkTimeline.setOnFinished(event -> {
                    fileSectionList.setVisible(false);
                    fileSectionList.setManaged(false);
                });
            } else {
                rotateTransition.play();
                fileSectionList.setVisible(true);
                fileSectionList.setManaged(true);
                expandTimeline.play();
            }
        });

//        System.out.println(f.getName());
        for (File file : Objects.requireNonNull(f.listFiles())) {
            if (file.exists() && file.isFile()) {
                fileSectionList.getChildren().add(addAudioFileUI(file));
//                loadingScreen.setLoading_label(String.format("Loading: ", file.getName()), ((double) currentFilePointer / fileCount) * 100);
            }
        }
        fileSectionList.setSpacing(10);

        rootVBox.getChildren().addAll(expandSectionBtn, fileSectionList);

        return rootVBox;
    }

    public Node addAudioFileUI(File file) {
        VBox rootVBox = new VBox();

        String fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
        String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);

        rootVBox.setId(fileName);
        rootVBox.setPrefHeight(Region.USE_COMPUTED_SIZE);

        Pane container = new Pane();
        container.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        container.setStyle("-fx-background-color: #808080; -fx-background-radius: 5px");

        Label audioFileName = new Label(fileName);
        audioFileName.setMaxWidth(128);
        audioFileName.setTextOverrun(OverrunStyle.ELLIPSIS);
        Label audioFileExt = new Label(extension);
        audioFileExt.setMaxWidth(32);
        audioFileExt.setTextOverrun(OverrunStyle.ELLIPSIS);
        Label audioFileLength = new Label("0:00");
        audioFileLength.setMaxWidth(48);
        audioFileLength.setTextOverrun(OverrunStyle.ELLIPSIS);

        Canvas audioFileDataVis = new Canvas();
        audioFileDataVis.setWidth(234);
        audioFileDataVis.setHeight(64);
        audioFileDataVis.setStyle("-fx-background-color: transparent;");

        GraphicsContext gc = audioFileDataVis.getGraphicsContext2D();
        gc.setFill(Color.web("A9A9A9"));
        gc.fillRoundRect(0, 0, audioFileDataVis.getWidth(), audioFileDataVis.getHeight(), 10, 10);

        float[][] audioData = null;

        try {
            audioData = readAudioFile(file);
            audioFileLength.setText(String.format("%.2fs", fileLength));
            if (audioData != null) {
                boolean isStereo = audioData[1] != null;
                float[] leftChannel = downsample(audioData[0], CANVAS_WIDTH);
                float[] rightChannel = isStereo ? downsample(audioData[1], CANVAS_WIDTH) : null;
                drawWaveform(gc, leftChannel, rightChannel);
            }
        } catch (Exception e) {
            System.out.println("This file gives exception: " + file.getName());
            e.printStackTrace();
        }

        HBox audioFileInfo = new HBox();
        audioFileInfo.setSpacing(5);
        audioFileInfo.setLayoutY(container.getPrefHeight() - 17);
        audioFileInfo.setLayoutX(3);
        container.prefHeightProperty().addListener((observable, oldValue, newValue) -> {
            audioFileInfo.setLayoutY(container.getPrefHeight() - 17);
        });

        Timeline expandTimeline = new Timeline(
                new KeyFrame(Duration.millis(150),
                        new KeyValue(container.prefHeightProperty(), 80, Interpolator.EASE_IN))
        );

        Timeline shrinkTimeline = new Timeline(
                new KeyFrame(Duration.millis(150),
                        new KeyValue(container.prefHeightProperty(), 64, Interpolator.EASE_OUT))
        );

        audioFileDataVis.setOnMouseEntered(e -> {
            expandTimeline.play();
//            System.out.println("entered" + container.getPrefHeight());
        });

        audioFileDataVis.setOnMouseExited(e -> {
            shrinkTimeline.play();
//            System.out.println("exited" + container.getPrefHeight());
        });

        audioFileDataVis.setOnMouseClicked(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            if (e.getButton() == MouseButton.PRIMARY) {
                try {
                    Media media = new Media(file.toURI().toString());
                    mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.play();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                if (mediaPlayer != null) {
                    mediaPlayer = null;
                }
            }

        });

        audioFileInfo.getChildren().addAll(audioFileName, audioFileExt, audioFileLength);
        audioFileInfo.setStyle("-fx-background-color: transparent;");

        container.getChildren().addAll(audioFileInfo, audioFileDataVis);

        rootVBox.getChildren().add(container);

        rootVBox.setOnDragDetected(event -> {
            Dragboard db = rootVBox.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
//            content.putFiles(Collections.singletonList(file));
            content.putFiles(Collections.singletonList(file));
            db.setContent(content);
            event.consume();

//            System.out.println("drag detected");
        });



        return rootVBox;
    }

    //test
    private double getWavLength(File file) {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = ais.getFormat();
            long frames = ais.getFrameLength();
            return frames / format.getFrameRate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private float[][] readAudioFile(File file) throws Exception {
        if (file.getName().toLowerCase().endsWith(".mp3")) {
            return readMp3(file);
        } else if (file.getName().toLowerCase().endsWith(".wav")) {
            fileLength = getWavLength(file);
            return readWavFile(file);
        } else {
            throw new IllegalArgumentException("Unsupported file format");
        }

    }

    //wav section
    private float[][] readWavFile(File file) throws UnsupportedAudioFileException, IOException { //Reading wav file (this method repeats a lot, move every other version to this one)
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();

        int sampleSize = format.getSampleSizeInBits();
        int channels = format.getChannels();
        byte[] pcmData = audioInputStream.readAllBytes();
        int bytesPerSample = sampleSize / 8;
        int totalSamples = pcmData.length / (bytesPerSample * channels);

        float[] leftChannel = new float[totalSamples];
        float[] rightChannel = (channels == 2) ? new float[totalSamples] : null;

        for (int i = 0; i < totalSamples; i++) {
            int sampleIndex = i * channels * bytesPerSample;

            // Extract Left Channel
            leftChannel[i] = extractSample(pcmData, sampleIndex, sampleSize);

            // Extract Right Channel (if Stereo)
            if (channels == 2) {
                rightChannel[i] = extractSample(pcmData, sampleIndex + bytesPerSample, sampleSize);
            }
        }

        return new float[][]{leftChannel, rightChannel};
    }

    private float extractSample(byte[] data, int index, int bitDepth) { //Extracting audio (support for 8, 16 and 24 bit audio - 32 bit will also be supported soon)
        int sample = 0;

        //combine this with the edian class that ahmet made (arya)
        if (bitDepth == 8) {
            sample = (data[index] & 0xFF) - 128;
            return sample / 128.0f; //converting unsigned 8-bit to signed
        } else if (bitDepth == 16) {
            sample = ((data[index + 1] << 8) | (data[index] & 0xFF));
            return sample / 32768.0f; //converting 16-bit signed to unsigned
        } else if (bitDepth == 24) {
            sample = ((data[index + 2] << 16) | ((data[index + 1] & 0xFF) << 8) | (data[index] & 0xFF));
            if (sample > 0x7FFFFF) sample -= 0x1000000; //converting 24-bit signed to unsigned
            return sample / 8388608.0f;
        }

        return sample; // Unsupported bit depth
    }
    //end of wav section

    //mp3 section
    private float[][] readMp3(File file) throws Exception {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        Bitstream bitstream = new Bitstream(inputStream);
        Decoder decoder = new Decoder();
        float[] leftChannel = new float[10000000];
        float[] rightChannel = new float[10000000];

        int index = 0;
        boolean isStereo = false;

        while (true) {
            Header frameHeader = bitstream.readFrame();
            if (frameHeader == null) break;

            SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
            isStereo = output.getChannelCount() == 2;

            for (int i = 0; i < output.getBufferLength(); i++) {
                if (isStereo) {
                    if (i % 2 == 0) leftChannel[index] = output.getBuffer()[i] / 32768.0f;
                    else rightChannel[index] = output.getBuffer()[i] / 32768.0f;
                } else {
                    leftChannel[index] = output.getBuffer()[i] / 32768.0f;
                }
                index++;
            }

            bitstream.closeFrame();
        }

        return new float[][]{trimArray(leftChannel, index), isStereo ? trimArray(rightChannel, index) : null};
    }

    private float[] trimArray(float[] array, int size) {
        float[] trimmed = new float[size];
        System.arraycopy(array, 0, trimmed, 0, size);
        return trimmed;
    }
    //end of mp3 section

    //Downsampling to make waveforms fit in the canvas (saving gpu compute power)
    private float[] downsample(float[] samples, int targetSize) {
        float[] downsampled = new float[targetSize];
        int step = samples.length / targetSize;

        for (int i = 0; i < targetSize; i++) {
            downsampled[i] = samples[i * step];
        }

        return downsampled;
    }

    //Draws both left and right channel for stereo audio, and only left for mono audio
    private void drawWaveform(GraphicsContext gc, float[] left, float[] right) {
        double midY = CANVAS_HEIGHT / 2.0;
//        double offset = (right != null) ? CANVAS_HEIGHT * 0.025 : 0; //Offsetting channels slightly for visibility (might remove later)

        double step = CANVAS_WIDTH / (double) left.length;

        // Fill under left channel
        gc.setFill(Color.web("#8B2E2E", 0.25));
        gc.beginPath();
        gc.moveTo(0, midY);
        for (int i = 0; i < left.length; i++) {
            double x = i * step;
            double y = midY - left[i] * midY;
            gc.lineTo(x, y);
        }
        gc.lineTo(CANVAS_WIDTH, midY);
        gc.closePath();
        gc.fill();

        gc.setStroke(Color.web("#8B2E2E"));
        drawPoint(gc, left, midY, step);

        if (right == null) {return;}
        gc.setFill(Color.web("#2C3E50"));
        gc.beginPath();
        gc.moveTo(0, midY);
        for (int i = 0; i < right.length; i++) {
            double x = i * step;
            double y = midY - right[i] * midY;
            gc.lineTo(x, y);
        }
        gc.lineTo(CANVAS_WIDTH, midY);
        gc.closePath();
        gc.fill();

        gc.setStroke(Color.web("#2C3E50", 0.25));
        drawPoint(gc, right, midY, step);
    }

    private void drawPoint(GraphicsContext gc, float[] buff, double midY, double step) {
        gc.setLineWidth(1.2);
        gc.beginPath();
        for (int i = 0; i < buff.length; i++) {
            double x = i * step;
            double y = midY - buff[i] * midY;
            if (i == 0) gc.moveTo(x, y);
            else gc.lineTo(x, y);
        }
        gc.stroke();
    }
}
