package org.JStudio.Utils;

import com.sun.management.OperatingSystemMXBean;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class to show the user the CPU usage.
 */
public class SystemMonitor {
    private final int DUR = 60;
    private final LinkedList<Double> CPUBUFF = new LinkedList<>(), MEMBUFF = new LinkedList<>();
    private final OperatingSystemMXBean OS = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private final Canvas C;
    private final double WIDTH, HEIGHT;
    private ScheduledExecutorService exec;
    private Image cpuImg = new Image("/icons/cpu.png"), memImg = new Image("/icons/memory.png");
    private boolean hover = false;


    /**
     * Creates a new SystemMonitor and attaches mouse event listeners to the provided canvas.
     *
     * @param systemCanvas the JavaFX Canvas used to draw the CPU and memory graphs
     */
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

    /**
     * Starts the system monitoring with a periodic updates and redraws.
     * Updates occur every second on a background thread.
     */
    public void start() {
        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            updateData();
            Platform.runLater(this::draw);
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Stops the monitoring and shuts down the scheduled executor service.
     */
    public void stop() {
        exec.shutdownNow();
    }

    /**
     * Checks the current CPU and memory usage and records the results to their respective buffers.
     */
    private void updateData() {
        double cpuUsage = OS.getCpuLoad();
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

    /**
     * Clears and redraws the entire canvas with the current CPU and memory usage graphs
     */
    private void draw() {
        GraphicsContext gc = C.getGraphicsContext2D();
        gc.clearRect(0, 0, C.getWidth(), C.getHeight());

        byte halfH = (byte) (HEIGHT / 2);

        drawGraph(gc, MEMBUFF, halfH, (byte) 0, true);
        drawGraph(gc, CPUBUFF, (byte) C.getHeight(), halfH, false);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(0, halfH, WIDTH, halfH);
    }

    /**
     * Draws an area graph based on a buffer of values.
     *
     * @param gc    the GraphicsContext used to draw
     * @param buff  the buffer containing usage values (0.0 to 1.0)
     * @param btm   the bottom Y coordinate of the graph
     * @param tp    the top Y coordinate of the graph
     * @param sl    true if drawing memory graph, false for CPU
     */
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
