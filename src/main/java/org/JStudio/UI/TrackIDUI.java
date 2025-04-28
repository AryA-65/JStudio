package org.JStudio.UI;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.JStudio.Core.Track;

import java.util.List;

public class TrackIDUI extends Pane {
    private final Track track;
    private final TextField trackName = new TextField();
    private final MutedBTN mutedBtn;

    private final List<String> MATTE_COLORS = List.of(
            "#FF6B6B", // Matte Red
            "#FF9F5B", // Matte Orange
            "#FFD166", // Matte Yellow
            "#06D6A0", // Matte Green
            "#1B9AAA", // Matte Teal
            "#118AB2", // Matte Blue
            "#9A4C95", // Matte Purple
            "#EF476F", // Matte Pink
            "#8338EC", // Matte Violet
            "#FF5F7E", // Matte Coral
            "#3D5A80", // Matte Navy
            "#98C1D9", // Matte Sky
            "#E0FBFC", // Matte Light Cyan
            "#EE6C4D", // Matte Burnt Orange
//            "#293241", // Matte Deep Blue
            "#8D99AE", // Matte Slate Gray
            "#70C1B3", // Matte Mint
            "#B8F2E6", // Matte Pastel Mint
            "#F7B267", // Matte Warm Sand
            "#F4845F"  // Matte Soft Clay
    );

    public TrackIDUI(Track track) {
        this.track = track;

        setPrefSize(126,64);
        setId("trackID");
        setStyle("-fx-background-color: " + MATTE_COLORS.get(Integer.parseInt(track.getId().get()) % MATTE_COLORS.size()) + ";");

        trackName.setFont(new Font("Inter Regular", 10));
        trackName.setLayoutX(4);
        trackName.setLayoutY(4);
        trackName.textProperty().bindBidirectional(track.getName());
        trackName.setId("trackIDtext");

        mutedBtn = new MutedBTN(114, 52, track);

        getChildren().addAll(trackName, mutedBtn);

        setOnMouseClicked(e -> {

        });
    }

    private void initActions() {
        setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {

            }
            if (e.getButton() == MouseButton.PRIMARY) {

            }
        });
    }

}
