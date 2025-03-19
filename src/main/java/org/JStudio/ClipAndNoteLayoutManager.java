package org.JStudio;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ClipAndNoteLayoutManager extends Application {

    private final double rectangleBaseWidth = 50;
    private final double rectangleHeight = 100;
    private final double resizeBorder = 10;
    private final double rectMinWidth = 50;

    private Pane pane;
    private Pane pane2;
    private Pane currentPane;
    private VBox vbox;
    
    private double xStart;
    private double oldMousePos;
    private double newMousePos;

    private boolean isResizingRight = false;
    private boolean isResizingLeft = false;

    private List<Rectangle> rectangles;
    private boolean overlaps;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //create pane
        vbox = new VBox();
        pane = new Pane();
        pane2 = new Pane();
        
        pane.setPrefSize(800,100);
        pane2.setPrefSize(800,100);
        
        //add Event Handler on mouse pressed that adds a rectangle to the pane
        pane.setOnMousePressed(this::addRectangle);
        pane2.setOnMousePressed(this::addRectangle);
        
        pane.setOnMouseEntered(mouseEvent -> {
            currentPane = pane;
        });
        pane2.setOnMouseEntered(mouseEvent -> {
            currentPane = pane2;
        });
        
        vbox.getChildren().addAll(pane, pane2);
        //create scene
        Scene scene = new Scene(vbox, 800, 200);
        stage.setScene(scene);
        stage.show();
    }
    
    //Makes a rectangle draggable
    private void dragRectangle(Rectangle dynRect) {
        //add Event Hnadler on mouse pressed that saves the starting position of the rectangle
        dynRect.setOnMousePressed(mouseEvent -> {
            xStart = mouseEvent.getX(); //mouse position relative to the origin of the rectangle
            oldMousePos = mouseEvent.getX();
            newMousePos = mouseEvent.getX();

            isResizingLeft = (newMousePos > 0) && (newMousePos < resizeBorder);
            isResizingRight = (newMousePos < dynRect.getWidth()) && (newMousePos > dynRect.getWidth() - resizeBorder);
        });

        //add Event Handler on mouse dragged that allows the rectangles to be dragged along the pane and be resized if the borders are dragged
        dynRect.setOnMouseDragged(mouseEvent -> {
            newMousePos = mouseEvent.getX();
            rectangles = getRectangles();
            overlaps = false;
            //resize left
            if (isResizingLeft) {
                double nextPosX= mouseEvent.getSceneX() - xStart; //get the next position that the rectangle will be in once moved
                overlappingRectangles(dynRect, nextPosX);

                if (!overlaps) {
                    double deltaMousePos = newMousePos - xStart;
                    dynRect.setLayoutX(dynRect.getLayoutX() + deltaMousePos);
                    dynRect.setWidth(dynRect.getWidth() - deltaMousePos);

                    if (dynRect.getWidth() < rectMinWidth) { //width is now below the minimum
                        double shrunkenWidth = dynRect.getWidth(); //get the width it was reduced to
                        dynRect.setWidth(rectMinWidth); //set the width back to the minimum
                        dynRect.setLayoutX(dynRect.getLayoutX() - (rectMinWidth - shrunkenWidth)); //move the rectangle back by the difference between the minimum width and the width it was reduced to
                    }
                    
                }
                //resize right
            } else if (isResizingRight) {
                double nextPosX = mouseEvent.getSceneX() - xStart; //get the next position that the rectangle will be in once moved
                overlaps = false;
                
                overlappingRectangles(dynRect, nextPosX);

                if (!overlaps) {
                    double deltaMousePos = newMousePos - oldMousePos;
                    dynRect.setWidth(dynRect.getWidth() + deltaMousePos);

                    if (dynRect.getWidth() < rectMinWidth) {
                        dynRect.setWidth(rectMinWidth);
                    }
                    oldMousePos = newMousePos;
                }

                //move along pane
            } else {
                //get arraylist of all rectangles in the pane
                rectangles = getRectangles();
                overlaps = false;

                double nextPosX = mouseEvent.getSceneX() - xStart; //get the next position that the rectangle will be in once moved
                overlappingRectangles(dynRect, nextPosX);

                //if it doesn't intersect then move the original rectangle to that position
                if (!overlaps) {
                    dynRect.setLayoutX(nextPosX);
                }
            }
        }
        );

        dynRect.setOnMouseReleased(mouseEvent -> {
            isResizingRight = false;
            isResizingLeft = false;
        });

    }

    //Adds a rectangle to the pane centered at the current mouse position
    private void addRectangle(MouseEvent e) {
        //get array list of all rectangles in the pane
        rectangles = getRectangles();
        double x = e.getSceneX(); //get mouse position
        overlaps = false;

        //if there are already rectangles in the array list (not placing the first one)
        if (!rectangles.isEmpty()) {
            for (int i = 0; i < rectangles.size(); i++) {
                Rectangle rectangle = new Rectangle(rectangleBaseWidth, rectangleHeight);
                rectangle.setLayoutX(x - rectangleBaseWidth / 2);
                //make sure it does not intersect with other rectangles in the pane
                if (rectangles.get(i).getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                    overlaps = true;
                }
            }
        }
        //if the are not intersection issues then add a rectangle to the pane
        if (!overlaps) {
            Rectangle rectangle = new Rectangle(rectangleBaseWidth, rectangleHeight);
            rectangle.setLayoutX(x - rectangleBaseWidth / 2);
            dragRectangle(rectangle); //make the rectangle draggable
            currentPane.getChildren().add(rectangle);
        }
    }

    private void overlappingRectangles(Rectangle rectangle, double nextPosX) {
        //Create a temporary rectangle to detect if it will intersect with any other rectangles once moved
        Rectangle tempRect = new Rectangle(rectangle.getTranslateX(), rectangle.getTranslateY(), rectangle.getWidth(), rectangle.getHeight());
        tempRect.setLayoutX(nextPosX);
        
        //Determine if intersecting
        for (int i = 0; i < rectangles.size(); i++) {
            if (rectangles.get(i) != rectangle) {
                if (rectangles.get(i).getBoundsInParent().intersects(tempRect.getBoundsInParent())) {
                    overlaps = true;
                    return;
                }
            }
        }
    }

    //returns an array list of all rectangles in the pane
    private List<Rectangle> getRectangles() {
        //create 
        rectangles = new ArrayList<>();
        for (Node n : currentPane.getChildren()) {
            rectangles.add((Rectangle) n);
        }
        return rectangles;
    }
}
