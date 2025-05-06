package org.JStudio.UI;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import org.JStudio.Core.Track;
import org.JStudio.UIController;

import java.util.List;

//the UI behind the colored blocks at the beginning of the tracks
public class TrackIDUI extends Pane {
    private final Track track;
    private final TextField trackName = new TextField();
    private final MutedBTN mutedBtn;
    private PluginRenderer renderer;

    //list of different colors for the trackIDUI background
    public static final List<String> MATTE_COLORS = List.of(
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

    /**
     * Creates an id for the track and the user can change the name of the track
     * @param track reference to the track
     * @param reference reference to the main controller
     */
    //sets parameters
    public TrackIDUI(Track track, UIController reference) {
        this.track = track;
        renderer = new PluginRenderer(reference.plugin_pane);

        setPrefSize(126,64);
        setId("trackID");
        setStyle("-fx-background-color: " + MATTE_COLORS.get(Integer.parseInt(track.getId().get()) % MATTE_COLORS.size()) + "; -fx-border-color: transparent;");

        trackName.setFont(new Font("Inter Regular", 10));
        trackName.setLayoutX(4);
        trackName.setLayoutY(4);
        trackName.textProperty().bindBidirectional(track.getName());
        trackName.setId("trackIDtext");

        mutedBtn = new MutedBTN(114, 52, track);

        getChildren().addAll(trackName, mutedBtn);

        //show a track is selected
        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                for (Node node : getParent().getChildrenUnmodifiable()) {
                    if (!node.equals(this)) {
                        node.getStyleClass().remove("selected_track");
                    }
                }

                getStyleClass().add("selected_track");

                renderer.renderGraph(track);
                reference.channel_pipeline.getSelectionModel().select(1);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                getStyleClass().remove("selected_track");

                reference.plugin_pane.getChildren().clear();

                Label message = new Label("No Track Selected: Plugins Unavailable");
                message.setLayoutX(reference.plugin_pane.getWidth() / 2);
                message.setLayoutY(reference.plugin_pane.getHeight() / 2);
                reference.plugin_pane.getChildren().add(message);
            }
        });
    }
}
