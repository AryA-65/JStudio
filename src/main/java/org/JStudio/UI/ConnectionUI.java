package org.JStudio.UI;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;

public class ConnectionUI extends CubicCurve {
    private final Circle startNode;
    private final Circle endNode;

    public ConnectionUI(Circle start, Circle end) {
        this.startNode = start;
        this.endNode = end;

        setStroke(Color.web("#00FD11"));
        setStrokeWidth(2);
        setFill(null);
        setId("connection_node");

        start.getParent().layoutXProperty().addListener((obs, old, newVal) -> update());
        start.getParent().layoutYProperty().addListener((obs, old, newVal) -> update());
        end.getParent().layoutXProperty().addListener((obs, old, newVal) -> update());
        end.getParent().layoutYProperty().addListener((obs, old, newVal) -> update());

        update();
    }

    public void update() {
        double startX = startNode.getParent().getLayoutX() + 54;
        double startY = startNode.getParent().getLayoutY() + 16;
        double endX = endNode.getParent().getLayoutX();
        double endY = endNode.getParent().getLayoutY() + 16;

        setStartX(startX);
        setStartY(startY);
        setEndX(endX);
        setEndY(endY);

        double controlOffset = Math.abs(endX - startX) * 0.5;

        setControlX1(startX + controlOffset);
        setControlY1(startY);
        setControlX2(endX - controlOffset);
        setControlY2(endY);
    }

    public Circle getStartNode() {
        return startNode;
    }

    public Circle getEndNode() {
        return endNode;
    }
}
