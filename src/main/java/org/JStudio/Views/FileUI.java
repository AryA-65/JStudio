package org.JStudio.Views;

import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.JStudio.Utils.AudioFileExtractor;

import java.io.*;
import java.util.List;

/**
 * Handles drawing of samples
 */
public class FileUI extends Pane {
    private final HBox file_info = new HBox();
    private final Canvas canvas = new Canvas(192, 48);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();
    private final Label f_name = new Label(), f_ext = new Label(), f_size = new Label();
    private final Timeline reveal = Effects.reveal(file_info);
    private final Timeline hide = Effects.hide(file_info);
    private MediaPlayer mediaPlayer;
    private int sample_rate;

    public FileUI(File file) {
        setMaxHeight(48);
        setMaxWidth(200);
        setCursor(Cursor.HAND);
        setId("file_ui");

        canvas.setWidth(200);
        canvas.setHeight(48);
//        canvas.setId("file_canvas");

        file_info.setPrefWidth(200);
        file_info.setPrefHeight(48);

        f_name.setText(file.getName().substring(0, file.getName().lastIndexOf('.')));
        f_name.setMaxWidth(100);
//        f_name.setWrapText(true);
        f_name.setTextOverrun(OverrunStyle.ELLIPSIS);
        f_name.setFont(new Font("Inter", 10));

        f_ext.setText(file.getName().substring(file.getName().lastIndexOf('.') + 1));
        f_ext.setMaxWidth(32);
//        f_ext.setWrapText(true);
        f_ext.setTextOverrun(OverrunStyle.ELLIPSIS);
        f_ext.setFont(new Font("Inter", 10));

        f_size.setText("0.00");
        f_size.setMaxWidth(48);
//        f_size.setWrapText(true);
        f_size.setTextOverrun(OverrunStyle.ELLIPSIS);
        f_size.setFont(new Font("Inter", 10));

        try {
            float[][] audioData = AudioFileExtractor.readFile(file);
            if (AudioFileExtractor.isMp3()) f_size.setText(String.format("%.2f", AudioFileExtractor.getMP3Length()));
            else f_size.setText(String.format("%.2f", AudioFileExtractor.getWavLength()));
            if (audioData != null) {
                boolean is_stereo = audioData[1] != null;
                float[] left_ch = AudioFileExtractor.downSample(audioData[0], (int) canvas.getWidth());
                float[] right_ch = is_stereo ? AudioFileExtractor.downSample(audioData[0], (int) canvas.getWidth()) : null;
                draw_data(left_ch, right_ch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOnMouseEntered(e -> reveal.play());
        setOnMouseExited(e -> hide.play());
        setOnMouseClicked(e -> {
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

        setOnDragDetected(e -> {
            Dragboard db = startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putFiles(List.of(file));
            db.setContent(content);
        });

        file_info.setSpacing(4);
        file_info.setAlignment(Pos.BOTTOM_LEFT);
        file_info.getChildren().addAll(f_name, f_ext, f_size);
        file_info.setPadding(new Insets(0,5,0,5));
        file_info.setId("hover_file");
        file_info.setOpacity(0);
        file_info.setPickOnBounds(false);

        getChildren().addAll(canvas, file_info);
    }

   /**
    * Draws the data of the sample
    */
    private void draw_data(float[] left, float[] right) {
        double midY = canvas.getHeight() / 2.0;
        double step = canvas.getWidth() / (double) left.length;

        gc.setFill(Color.web("#8B2E2E", 0.25));
        gc.beginPath();
        gc.moveTo(0, midY);
        for (int i = 0; i < left.length; i++) {
            double x = i * step;
            double y = midY - left[i] * midY;
            gc.lineTo(x, y);
        }
        gc.lineTo(canvas.getWidth(), midY);
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
        gc.lineTo(canvas.getWidth(), midY);
        gc.closePath();
        gc.fill();

        gc.setStroke(Color.web("#2C3E50", 0.25));
        drawPoint(gc, right, midY, step);
    }

    /**
     * Draws the wave line of the sample
     */
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

    public String getFileName() {
        return f_name.getText();
    }
}
