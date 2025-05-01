package org.JStudio.Utils;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SystemMonitor { //make into static class
    private final int DUR = 60;
    private final LinkedList<Double> CPUBUFF = new LinkedList<>(), MEMBUFF = new LinkedList<>();
    private final OperatingSystemMXBean OS = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private final Canvas C;
    private final double WIDTH, HEIGHT;
    private ScheduledExecutorService exec;
    private Image cpuImg = new Image("/icons/cpu.png"), memImg = new Image("/icons/memory.png");
    private boolean hover = false;

    public SystemMonitor(Canvas systemCanvas) {
        this.C = systemCanvas;
        this.WIDTH = systemCanvas.getWidth();
        this.HEIGHT = systemCanvas.getHeight();

        C.setOnMouseEntered(e -> {
            hover = true;
            Platform.runLater(this::draw);
        });
        C.setOnMouseExited(e -> {
            hover = false;
            Platform.runLater(this::draw);
        });
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
//        double norm = (OS.getTotalMemorySize() - OS.getFreeMemorySize()) / 1e9;
        double norm = (double) (OS.getTotalMemorySize() - OS.getFreeMemorySize()) / OS.getTotalMemorySize();

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
            c = Color.web("#f89820");
        } else {
            c = Color.web("#5382a1");
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
        gc.setFont(Font.font("Inter", 6));

        if (hover) {
            if (sl) {
                gc.drawImage(memImg, 0,1,10,10);
            } else {
                gc.drawImage(cpuImg, 0,13,10,10);
            }
        } else {
            if (sl) {
                gc.fillText(String.format("%1.2f %%", MEMBUFF.getLast()), 2, 6);
            } else {
                gc.fillText(String.format("%3.0f %%", CPUBUFF.getLast() * 100), 2, 18);
            }
        }
    }
}
