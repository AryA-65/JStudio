package org.JStudio.Views;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class Effects {
    public static Timeline reveal(Node node) {
        return new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(node.opacityProperty(), 1, javafx.animation.Interpolator.EASE_IN)));
    }

    public static Timeline hide(Node node) {
        return new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(node.opacityProperty(), 0, javafx.animation.Interpolator.EASE_IN)));
    }

}
