package org.JStudio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ClipMovingManager extends Application {

    private double canvasBaseWidth = 50;
    private final double canvasHeight = 100;
    private final double resizeBorder = 10;
    private final double canvasWidth = 50;

    private Pane pane;
    private Pane pane2;
    private Pane currentPane;
    private VBox vbox;

    private double xStart;
    private double oldMousePos;
    private double newMousePos;

    private boolean isResizingRight = false;
    private boolean isResizingLeft = false;

    private List<Canvas> canvasList;
    private boolean overlaps;

    private boolean hovering;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //create pane
        StackPane stackPane = new StackPane();
        Canvas canvas = new Canvas(800, 100);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        vbox = new VBox();
        pane = new Pane();
        pane2 = new Pane();

        pane.setPrefSize(800, 100);
        pane2.setPrefSize(800, 100);

        //add Event Handler on mouse pressed that adds a rectangle to the pane
        pane.setOnMousePressed(this::addCanvas);
        pane2.setOnMousePressed(this::addCanvas);
//        pane.setBorder(Border.stroke(Color.GREY));
//        pane2.setBorder(Border.stroke(Color.GREY));

        pane.setOnMouseEntered(mouseEvent -> {
            currentPane = pane;
        });
        pane2.setOnMouseEntered(mouseEvent -> {
            currentPane = pane2;
        });

        drawShapes(gc);
        stackPane.getChildren().addAll(canvas, pane);
        vbox.getChildren().addAll(stackPane, pane2);

        //create scene
        Scene scene = new Scene(vbox, 800, 200);
        stage.setScene(scene);
        stage.show();
    }

    //Makes a canvas draggable
    private void dragRectangle(Canvas dynCanvas) {
        //add Event Hnadler on mouse pressed that saves the starting position of the canvas
        dynCanvas.setOnMousePressed(mouseEvent -> {
            xStart = mouseEvent.getX(); //mouse position relative to the origin of the canvas
            oldMousePos = mouseEvent.getX();
            newMousePos = mouseEvent.getX();

            isResizingLeft = (newMousePos > 0) && (newMousePos < resizeBorder);
            isResizingRight = (newMousePos < dynCanvas.getWidth()) && (newMousePos > dynCanvas.getWidth() - resizeBorder);
        });

        //add Event Handler on mouse dragged that allows the canvases to be dragged along the pane and be resized if the borders are dragged
        dynCanvas.setOnMouseDragged(mouseEvent -> {
            newMousePos = mouseEvent.getX();
            canvasList = getCanvases();
            overlaps = false;

            double deltaMousePos = newMousePos - oldMousePos; //get how much the mouse moved since starting the drag

            //resize left
            if (isResizingLeft) {
                double nextPosX = dynCanvas.getLayoutX() + deltaMousePos; //get the next position that the canvas will be in once moved
                double nextWidth = dynCanvas.getWidth() - deltaMousePos; // get the next width that the canvas will have once resized

                detectOverlap(dynCanvas, nextPosX, nextWidth);

                if (!overlaps) {
                    dynCanvas.setLayoutX(nextPosX);
                    dynCanvas.setWidth(nextWidth);

                    if (dynCanvas.getWidth() < canvasWidth) { //width is now below the minimum
                        double shrunkenWidth = dynCanvas.getWidth(); //get the width it was reduced to
                        dynCanvas.setWidth(canvasWidth); //set the width back to the minimum
                        dynCanvas.setLayoutX(dynCanvas.getLayoutX() - (canvasWidth - shrunkenWidth)); //move the canvas back by the difference between the minimum width and the width it was reduced to
                    }
            GraphicsContext gc = dynCanvas.getGraphicsContext2D();
            drawCanvas(gc, (int)dynCanvas.getWidth(), (int)dynCanvas.getHeight());
                }
                //resize right
            } else if (isResizingRight) {
                double nextWidth = dynCanvas.getWidth() + deltaMousePos; //get the next position that the canvas will be in once moved
                overlaps = false;

                detectOverlap(dynCanvas, dynCanvas.getLayoutX(), nextWidth);

                if (!overlaps) {
                    dynCanvas.setWidth(nextWidth);

                    if (dynCanvas.getWidth() < canvasWidth) {
                        dynCanvas.setWidth(canvasWidth);
                    }
                    oldMousePos = newMousePos;
                    GraphicsContext gc = dynCanvas.getGraphicsContext2D();
                    drawCanvas(gc, (int)dynCanvas.getWidth(), (int)dynCanvas.getHeight());
                }

                //move along pane
            } else {
                double nextPosX = dynCanvas.getLayoutX() + deltaMousePos; //get the next position that the canvas will be in once moved
                overlaps = false;

                detectOverlap(dynCanvas, nextPosX, dynCanvas.getWidth());

                //if it doesn't intersect then move the original canvas to that position
                if (!overlaps) {
                    dynCanvas.setLayoutX(nextPosX);
                }
            }
        }
        );

        dynCanvas.setOnMouseReleased(mouseEvent -> {
            isResizingRight = false;
            isResizingLeft = false;
        });

    }

    //Adds a canvas to the pane centered at the current mouse position
    private void addCanvas(MouseEvent e) {
        //get array list of all canvas in the pane
        canvasList = getCanvases();
        double x = e.getSceneX(); //get mouse position
        overlaps = false;

        //if there are already canvases in the array list (not placing the first one)
        if (!canvasList.isEmpty()) {
            for (int i = 0; i < canvasList.size(); i++) {
                Canvas canvas = new Canvas(canvasBaseWidth, canvasHeight);
                canvas.setLayoutX(x - canvasBaseWidth / 2);
                //make sure it does not intersect with other canvases in the pane
                if (canvasList.get(i).getBoundsInParent().intersects(canvas.getBoundsInParent())) {
                    overlaps = true;
                }
            }
        }
        //if the are not intersection issues then add a canvas to the pane
        if (!overlaps) {
            // Change size of canvas based on clip length
            byte[] originalAudio = null;
            try {
                File file = new FileChooser().showOpenDialog(null);
                originalAudio = Files.readAllBytes(file.toPath());
            } catch (IOException ex) {
                System.out.println(ex);
            }
            if (originalAudio.length > 100000) {
                canvasBaseWidth = (int) (originalAudio.length/50000);
            } else {
                canvasBaseWidth = (int) (originalAudio.length/1000);
            }
            Canvas canvas = new Canvas(canvasBaseWidth, canvasHeight);
            canvas.setLayoutX(x - canvasBaseWidth / 2);
            dragRectangle(canvas); //make the rectangle draggable
            canvas.setOpacity(0.75);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            drawCanvas(gc, (int)canvas.getWidth(), (int)canvas.getHeight());
            canvas.setOnMouseEntered(mouseEvent -> {
                hovering = true;
                drawCanvas(gc, (int)canvas.getWidth(), (int)canvas.getHeight());
            });
            canvas.setOnMouseExited(mouseEvent -> {
                hovering = false;
                drawCanvas(gc, (int)canvas.getWidth(), (int)canvas.getHeight());
            });
            currentPane.getChildren().add(canvas);
        }
    }

    private void detectOverlap(Canvas canvas, double nextPosX, double nextWidth) {
        //Create a temporary canvas to detect if it will intersect with any other canvases once moved
        Canvas tempCanvas = new Canvas(nextWidth, canvas.getHeight());
        tempCanvas.setLayoutX(nextPosX);

        //Determine if intersection with the edge of the pane
        if (tempCanvas.getLayoutX() < 0 || tempCanvas.getLayoutX() + tempCanvas.getWidth() > currentPane.getWidth()) {
            overlaps = true;
            return;
        }

        //Determine if intersecting with other canvases
        for (int i = 0; i < canvasList.size(); i++) {
            if (canvasList.get(i) != canvas) {
                if (canvasList.get(i).getBoundsInParent().intersects(tempCanvas.getBoundsInParent())) {
                    overlaps = true;
                    return;
                }
            }
        }
    }

    //returns an array list of all canvases in the pane
    private List<Canvas> getCanvases() {
        //create 
        canvasList = new ArrayList<>();
        for (Node n : currentPane.getChildren()) {
            canvasList.add((Canvas) n);
        }
        return canvasList;
    }

    private void drawShapes(GraphicsContext gc) {
        double xPos = 0;
        for (int i = 0; i <= 16; i++) {
            xPos += 50;
            gc.strokeLine(xPos, 0, xPos, 200);
        }
    }
    
    // Fill the canvas no matter its size
    private void drawCanvas(GraphicsContext gc, int canvasWidth, int canvasHeight) {
        if (hovering) {
            gc.setFill(Color.BLUE);
        } else {
            gc.setFill(Color.BLACK);
        }
        gc.fillRect(0, 0, canvasWidth, canvasHeight);
    }
}
