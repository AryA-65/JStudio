package org.JStudio.UI;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;

public class ConnectionUI extends CubicCurve {
    public ConnectionUI(Circle start, Circle end) {
//        setStartX(start.getLayoutX());
//        setStartY(start.getLayoutY());
//        setEndX(end.getLayoutX());
//        setEndY(end.getLayoutY());
        setStroke(Color.web("#00FD11"));
        setStrokeWidth(2);
        setFill(null);
        setId("connection_node");

        start.getParent().layoutXProperty().addListener((obs, old, newVal) -> update(start, end));
        start.getParent().layoutYProperty().addListener((obs, old, newVal) -> update(start, end));
        end.getParent().layoutXProperty().addListener((obs, old, newVal) -> update(start, end));
        end.getParent().layoutYProperty().addListener((obs, old, newVal) -> update(start, end));

        update(start, end);
    }

    public void update(Circle start, Circle end) {
        double startX = start.getParent().getLayoutX() + ((StackPane) start.getParent()).getLayoutBounds().getWidth();
        System.out.printf("Start position %.1f, translateX %.1f\n", startX, ((StackPane) start.getParent()).getLayoutBounds().getWidth());
        double startY = start.getParent().getLayoutY() + 16;
        double endX = end.getParent().getLayoutX();
        double endY = end.getParent().getLayoutY() + 16;

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
}
