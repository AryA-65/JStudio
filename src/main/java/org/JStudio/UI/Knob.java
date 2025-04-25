package org.JStudio.UI;

import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ScrollEvent;

//public class Knob extends StackPane { //Needs more time to think (maybe interface class?)
//    private final double MIN_VAL, MAX_VAL;
//    private final DoubleProperty value = new SimpleDoubleProperty();
//
//    private Canvas canvas;
//    private double centerX, centerY, radius;
//
//    enum TYPE {
//        REG, ARC, MIX
//    }
//
//    Knob() {
//        this.MIN_VAL = 0.0;
//        this.MAX_VAL = 1.0;
//        this.value.set(this.MAX_VAL);
//
//    }
//
//    Knob(double minVal, double maxVal, double initVal, double radius) {
//        MIN_VAL = minVal;
//        MAX_VAL = maxVal;
//
//        value.set(initVal);
//
//        canvas = new Canvas(radius * 1.1, radius * 1.1);
//    }
//
//    Knob(double radius) {
//        this();
//
//        canvas = new Canvas(radius * 1.1, radius * 1.1);
//    }
//
//
//
//
//}


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Knob extends Canvas {
    private static final double DEFAULT_SIZE = 100;
    private static final double MIN_ANGLE = -135;
    private static final double MAX_ANGLE = 135;
    private static final double MID_ANGLE = 0;

    private final DoubleProperty value = new SimpleDoubleProperty(0.0);
    private final DoubleProperty min = new SimpleDoubleProperty(0.0);
    private final DoubleProperty max = new SimpleDoubleProperty(1.0);
    private final BooleanProperty snapEnabled = new SimpleBooleanProperty(false);
    private final DoubleProperty snapStep = new SimpleDoubleProperty(1.0);
    private final ObjectProperty<Type> type = new SimpleObjectProperty<>(Type.REG);

    private double dragStartY;
    private double dragStartValue;

    private Tooltip tooltip;

    public enum Type {
        REG,
        BIP
    }

    public Knob(double size, boolean se, double ss, Type type) {
        super(size, size);
        setSnapEnabled(se);
        setSnapStep(ss);
        init(type);
    }

    public Knob() {
        this(DEFAULT_SIZE, false, 0, Type.REG);
    }

    private void init(Type type) {
        setRotate(-90);

        if (type == Type.REG) {
            setValues(0,1);
        } else if (type == Type.BIP) {
            setValues(-1,1);
        }

        value.addListener((obs, oldVal, newVal) -> draw());
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());

        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);
        setOnScroll(this::onMouseWheelScrolled);

        tooltip = new Tooltip();
        Tooltip.install(this, tooltip);

        draw();
    }

    private void onMousePressed(MouseEvent e) {
        dragStartY = e.getSceneY();
        dragStartValue = getValue();
    }

    private void onMouseDragged(MouseEvent e) {
        double delta = dragStartY - e.getSceneY();
        double sensitivity = 0.005;
        double range = max.get() - min.get();
        setValue(clamp(dragStartValue + delta * sensitivity * range, min.get(), max.get()));
    }

    private void onMouseWheelScrolled(ScrollEvent e) {
        double delta = e.getDeltaY();
        double sensitivity = (snapEnabled.get() ? snapStep.get() / (delta * 1) : 0.00025);
        double range = max.get() - min.get();
        double newValue = getValue() + delta * Math.abs(sensitivity) * range;

        setValue(clamp(newValue, min.get(), max.get()));
    }

    private void draw() {
        double w = getWidth();
        double h = getHeight();
        double centerX = w / 2;
        double centerY = h / 2;
        double radius = Math.min(w, h) / 2 - 5;

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        gc.setFill(Color.BLACK);
        gc.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth((radius * 2) * (2 / 72.5));
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        int numDots = snapEnabled.get() ? (int) ((1 / snapStep.get()) + 1): 0;

        if (numDots > 0) {
            double dotRadius = (radius * 2) * ((double) 2 / 72.5);
            double dotDistance = radius + 3;

            double valueRange = max.get() - min.get();
            double valueStep = valueRange / (numDots - 1);
            double currentValue = getValue();
            double tolerance = valueStep * 0.1;

            for (int i = 0; i < numDots; i++) {
                double dotAngle = MIN_ANGLE + (i * (MAX_ANGLE - MIN_ANGLE) / (numDots - 1));
                double dotRadians = Math.toRadians(dotAngle);
                double dotX = centerX + Math.cos(dotRadians) * dotDistance;
                double dotY = centerY + Math.sin(dotRadians) * dotDistance;

                double dotValue = min.get() + (i * valueStep);

                boolean isActive = (dotValue - currentValue) <= tolerance;
//            System.out.println(isActive);

                gc.setFill(isActive ? Color.WHITE : Color.BLACK);
                gc.fillOval(dotX - dotRadius, dotY - dotRadius, dotRadius * 2, dotRadius * 2);
            }
        }


        double angle = MIN_ANGLE + getNormalizedValue() * (MAX_ANGLE - MIN_ANGLE);
        double radians = Math.toRadians(angle);
        double dimpleRadius = (radius * 2) * ((double) 5 / 72.5);
        double dimpleCenterX = centerX + Math.cos(radians) * (radius - (dimpleRadius * 2.5));
        double dimpleCenterY = centerY + Math.sin(radians) * (radius - (dimpleRadius * 2.5));

        gc.setFill(Color.ORANGE);
        gc.fillOval(dimpleCenterX - dimpleRadius, dimpleCenterY - dimpleRadius, dimpleRadius * 2, dimpleRadius * 2);

//        gc.setStroke(Color.WHITE);
//        gc.setLineWidth(2);
//        gc.strokeLine(centerX, centerY, endX, endY);

        tooltip.setText(String.format("Value: %.2f\nMin: %.2f, Max: %.2f", getValue(), min.get(), max.get()));
    }

    private double getNormalizedValue() {
        return (value.get() - min.get()) / (max.get() - min.get());
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public double getValue() {
        return value.get();
    }

    public void setValue(double val) {
        if (snapEnabled.get()) {
            val = Math.round(val / snapStep.get()) * snapStep.get();
        }
        value.set(clamp(val, min.get(), max.get()));
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValues(double min_v, double max_v) {
        setMin(min_v);
        setMax(max_v);
    }

    public double getMin() {
        return min.get();
    }

    public void setMin(double val) {
        min.set(val);
    }

    public DoubleProperty minProperty() {
        return min;
    }

    public double getMax() {
        return max.get();
    }

    public void setMax(double val) {
        max.set(val);
    }

    public DoubleProperty maxProperty() {
        return max;
    }

    public boolean isSnapEnabled() {
        return snapEnabled.get();
    }

    public void setSnapEnabled(boolean snapEnabled) {
        this.snapEnabled.set(snapEnabled);
    }

    public BooleanProperty snapEnabledProperty() {
        return snapEnabled;
    }

    public double getSnapStep() {
        return snapStep.get();
    }

    public void setSnapStep(double snapStep) {
        this.snapStep.set(snapStep);
    }

    public DoubleProperty snapStepProperty() {
        return snapStep;
    }
}
