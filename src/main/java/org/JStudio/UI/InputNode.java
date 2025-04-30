package org.JStudio.UI;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.*;
import org.JStudio.Core.Track;

public class InputNode extends StackPane {
    private final Label name = new Label();
    private final Circle output = new Circle();
    private double offsetX, offsetY;
    private final Track input;

    public InputNode(Track input) {
        this.input = input;
        setPrefSize(96, 32);
        setId("plugin_node");

        name.textProperty().bind(input.getName());

        output.setRadius(5);
        output.setTranslateX(48);
        output.setId("output_port");

        setOnMousePressed(e -> {
            offsetX = e.getX();
            offsetY = e.getY();
        });

        setOnMouseDragged(e -> {
            Point2D localPoint = getParent().sceneToLocal(e.getSceneX(), e.getSceneY());
            relocate(localPoint.getX() - offsetX, localPoint.getY() - offsetY);
            e.consume();
        });

        getChildren().addAll(output, name);
    }

    public Circle getOutput() {
        return output;
    }
}
