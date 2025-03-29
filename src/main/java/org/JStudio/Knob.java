package org.JStudio;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;

public class Knob {
    private double curAngle, curValue, radius;
    private final double MIN_ANGLE = 225.0, MAX_ANGLE = 315.0, MIN_VALUE = 0.0, MAX_VALUE = 1.0;
    private int xPos, yPos, centerX, centerY;

    private Arc curValueVis;
    private Circle dimple, knobCircle;
    private Pane container;

    Knob(double angle, int radius) {
        this.curAngle = MAX_ANGLE;
        this.curValue = MAX_VALUE;

        this.centerX = centerX;
        this.centerY = centerY;

        this.radius = radius;

        container = new Pane();
        container.setPrefSize(radius * 2 + (radius * 0.1), radius * 2 + (radius * 0.1));

        knobCircle.setRadius(radius);
        knobCircle.setCenterX(centerX);
        knobCircle.setCenterY(centerY);

        dimple.setRadius(1);
        dimple.setCenterX(centerX - radius + 2);
        dimple.setCenterY(centerY);

        container.getChildren().add(knobCircle);
    }

    public Node getRoot() {
        return container;
    }

    public void setAngle(double angle) {
        this.radius = 5.0;
        this.curAngle = angle;
        this.curValue = calculateVal();
    }

    public double getCurAngle() {
        return curAngle;
    }

    public double calculateVal() {
        double realAngle = 0;
        if (curAngle <= MIN_ANGLE && curAngle >= 0) {
            realAngle = MIN_ANGLE - curAngle;
        } else if (curAngle <= MAX_ANGLE) {
            realAngle = curAngle - MAX_ANGLE;
        }

        return (realAngle / 270);
    }

    public double getCurValue() {
        return curValue;
    }
}
