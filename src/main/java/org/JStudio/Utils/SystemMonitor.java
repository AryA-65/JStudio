package org.JStudio.Utils;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SystemMonitor {
    private final int DUR = 60;
    private final LinkedList<Double> CPUBUFF = new LinkedList<>(), MEMBUFF = new LinkedList<>();
    private final OperatingSystemMXBean OS = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private final Canvas C;
    private final double WIDTH, HEIGHT;
    private ScheduledExecutorService exec;

    public SystemMonitor(Canvas systemCanvas) {
        this.C = systemCanvas;
        this.WIDTH = systemCanvas.getWidth();
        this.HEIGHT = systemCanvas.getHeight();
    }

    public void start() {
        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            updateData();
            Platform.runLater(this::draw);
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        exec.shutdownNow();
    }

    private void updateData() {
        double cpuUsage = OS.getCpuLoad();

        Runtime rt = Runtime.getRuntime();
        double norm = (double) (rt.totalMemory() - rt.freeMemory()) / rt.totalMemory();

        synchronized (CPUBUFF) {
            if (CPUBUFF.size() >= DUR) CPUBUFF.removeFirst();
            CPUBUFF.add(cpuUsage);
        }

        synchronized (MEMBUFF) {
            if (MEMBUFF.size() >= DUR) MEMBUFF.removeFirst();
            MEMBUFF.add(norm);
        }
    }

    private void draw() {
        GraphicsContext gc = C.getGraphicsContext2D();
        gc.clearRect(0, 0, C.getWidth(), C.getHeight());

        byte halfH = (byte) (HEIGHT / 2);

        drawGraph(gc, MEMBUFF, halfH, (byte) 0, true);
        drawGraph(gc, CPUBUFF, (byte) C.getHeight(), halfH, false);
//        drawCpuGraph(gc, CPUBUFF, (byte) C.getHeight(), halfH, false);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(0, halfH, WIDTH, halfH);
    }

    private void drawGraph(GraphicsContext gc, LinkedList<Double> buff, byte btm, byte tp, boolean sl) {
        byte h = (byte) (btm - tp);
        double stepX = WIDTH / DUR;
        double[] xPoints, yPoints;

        synchronized (buff) {
            short s = (short) buff.size();
            xPoints = new double[s];
            yPoints = new double[s];

            for (int i = 0; i < s; i++) {
                xPoints[i] = stepX * i;
                double value = Math.max(0, Math.min(1, buff.get(i)));
                yPoints[i] = btm - (value * h);
            }
        }

        Color c;

        if (sl) {
            c = Color.web("#F57C00");
        } else {
            c = Color.web("#3A6DF0");
        }

        gc.setFill(c);
        gc.beginPath();
        gc.moveTo(0, btm);
        for (int i = 0; i < xPoints.length; i++) {
            gc.lineTo(xPoints[i], yPoints[i]);
        }
        gc.lineTo(xPoints[xPoints.length - 1], btm);
        gc.closePath();
        gc.fill();

        gc.setStroke(c);
        gc.setLineWidth(1);
        for (int i = 1; i < xPoints.length; i++) {
            gc.strokeLine(xPoints[i - 1], yPoints[i - 1], xPoints[i], yPoints[i]);
        }

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Inter", 8));

        if (sl) {
            gc.fillText(String.format("%.1f GB", MEMBUFF.getLast()), 2, 8);
        } else {
            gc.fillText(String.format("%3.0f", CPUBUFF.getLast() * 100), 2, 21);
        }
    }

    //test, not perfect
    private void drawCpuGraph(GraphicsContext gc, LinkedList<Double> buff, byte btm, byte tp, boolean v) {
        byte h = (byte) (btm - tp);
        double stepX = WIDTH / DUR;
        double barWidth = stepX * 0.8;
        double[] xPoints, yPoints;

        synchronized (buff) {
            short s = (short) buff.size();
            xPoints = new double[s];
            yPoints = new double[s];

            for (int i = 0; i < s; i++) {
                xPoints[i] = stepX * i;
                double value = Math.max(0, Math.min(1, buff.get(i)));
                yPoints[i] = btm - (value * h);
            }
        }

        Color c = Color.web("#3A6DF0");
        gc.setFill(c);

        if (v) {
            for (int i = 0; i < xPoints.length; i++) {
                gc.fillRect(xPoints[i] - barWidth / 2, yPoints[i], barWidth, btm - yPoints[i]);
            }
        } else { //trying to emulate the btop dots, but very shitty implementation
            for (int i = 0; i < xPoints.length; i++) {
                double x = xPoints[i];
                double y = yPoints[i];

                for (double j = y; j < btm; j++) {
                    gc.fillOval(x - 0.5, j - 0.5, 1, 1);
                }
            }
        }

    }

}
