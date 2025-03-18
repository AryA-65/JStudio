package org.JStudio;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ClipAndNoteLayoutManager extends Application {

    Pane pane;
    double xStart;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //create pane
        pane = new Pane();
        
        //add Event Handler on mouse pressed that adds a rectangle to the pane
        pane.setOnMousePressed(this::addRectangle);
        
        //create scene
        Scene scene = new Scene(pane, 800, 100);
        stage.setScene(scene);
        stage.show();
    }

    //Makes a rectangle draggable
    private void dragRectangle(Rectangle movingRect) {
        //add Event Hnadler on mouse pressed that saves the starting position of the rectangle
        movingRect.setOnMousePressed(mouseEvent -> {
            xStart = mouseEvent.getSceneX();
        });

        //add Event Handler on mouse dragged that allows the rectangles to be dragged along the pane
        movingRect.setOnMouseDragged(mouseEvent -> {
        //get arraylist of all rectangles in the pane
        List<Rectangle> rectangles = getSprites();
        boolean overlaps = false;
        double nextPosX = mouseEvent.getSceneX() - xStart; //get the next position that the rectangle will be in once moved
        //Create a temporary rectangle to detect if it will intersect with any other rectangles once moved
        Rectangle tempRect = new Rectangle(movingRect.getTranslateX(),movingRect.getTranslateY(),movingRect.getWidth(),movingRect.getHeight());
        tempRect.setLayoutX(nextPosX);
        
            //Determine if intersecting
            for (int i = 0; i < rectangles.size(); i++) {
                if (rectangles.get(i) != movingRect) {
                    if (rectangles.get(i).getBoundsInParent().intersects(tempRect.getBoundsInParent())) {
                        overlaps = true;
                    }
                }
            }
            
            //if it doesn't intersect then move the original rectangle to that position
            if(!overlaps){
                movingRect.setLayoutX(nextPosX);
            }
        });
    }

    //Adds a rectangle to the pane centered at the current mouse position
    private void addRectangle(MouseEvent e) {
        //get array list of all rectangles in the pane
        List<Rectangle> rectangles = getSprites();
        double x = e.getSceneX(); //get mouse position
        boolean overlaps = false;

        //if there are already rectangles in the array list (not placing the first one)
        if (!rectangles.isEmpty()) {
            for (int i = 0; i < rectangles.size(); i++) {
                Rectangle rectangle = new Rectangle(50, 100);
                rectangle.setTranslateX(x - 25);
                //make sure it does not intersect with other rectangles in the pane
                if (rectangles.get(i).getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                    overlaps = true;
                }
            }
        }
        //if the are not intersection issues then add a rectangle to the pane
        if (!overlaps) {
            Rectangle rectangle = new Rectangle(50, 100);
            rectangle.setTranslateX(x - 25);
            dragRectangle(rectangle); //make the rectangle draggable
            pane.getChildren().add(rectangle);
        }
    }

    //returns an array list of all rectangles in the pane
    private List<Rectangle> getSprites() {
        //create 
        List<Rectangle> rectangles = new ArrayList<>();
        for (Node n : pane.getChildren()) {
            rectangles.add((Rectangle) n);
        }
        return rectangles;
    }
}
