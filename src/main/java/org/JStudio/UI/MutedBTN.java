package org.JStudio.UI;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import org.JStudio.Core.Mixer;
import org.JStudio.Core.Track;

public class MutedBTN extends Pane {
    public MutedBTN(double layoutX, double layoutY, Object input) {
        setPrefSize(8, 8);
        setMaxHeight(8);
        setMaxWidth(8);
        setLayoutX(layoutX);
        setLayoutY(layoutY);
        toFront();
        getStyleClass().add("active");

        if (input instanceof Track) {
            ((Track) input).getMuted().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    getStyleClass().remove("muted");
                    getStyleClass().add("active");
                } else {
                    getStyleClass().remove("active");
                    getStyleClass().add("muted");
                }
            });

            setOnMouseReleased(e -> {
                if (e.getButton() == MouseButton.PRIMARY && contains(e.getX(), e.getY())) {
                    ((Track) input).getMuted().set(!((Track) input).getMuted().get());
                }
            });
        } else if (input instanceof Mixer) {
            ((Mixer) input).getMuted().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    getStyleClass().remove("muted");
                    getStyleClass().add("active");
                } else {
                    getStyleClass().remove("active");
                    getStyleClass().add("muted");
                }
            });

            setOnMouseReleased(e -> {
                if (e.getButton() == MouseButton.PRIMARY && contains(e.getX(), e.getY())) {
                    ((Mixer) input).getMuted().set(!((Mixer) input).getMuted().get());
                }
            });
        }
    }
}
