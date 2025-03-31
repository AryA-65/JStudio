package org.JStudio.UI;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;

public class Knob extends StackPane { //Needs more time to think (maybe interface class?)
    private final double MIN_VAL, MAX_VAL;
    private final DoubleProperty value = new SimpleDoubleProperty();

    private Canvas canvas;
    private double centerX, centerY, radius;

    enum TYPE {
        REG, ARC, MIX
    }

    Knob() {
        this.MIN_VAL = 0.0;
        this.MAX_VAL = 1.0;
        this.value.set(this.MAX_VAL);

    }

    Knob(double minVal, double maxVal, double initVal, double radius) {
        MIN_VAL = minVal;
        MAX_VAL = maxVal;

        value.set(initVal);

        canvas = new Canvas(radius * 1.1, radius * 1.1);
    }

    Knob(double radius) {
        this();

        canvas = new Canvas(radius * 1.1, radius * 1.1);
    }




}
