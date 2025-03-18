package javaapplication1;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


public class JavaApplication1 extends Application{
    Pane pane;
    double xStart;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        pane = new Pane();
        Scene scene = new Scene(pane, 800, 100);
        pane.setOnMousePressed(this::addRectangle);
        stage.setScene(scene);
        stage.show();
    }
    
    private void dragRectangle(Rectangle rectangle) {
        List<Rectangle> rectangles = getSprites();

        rectangle.setOnMousePressed(mouseEvent -> {
        xStart = mouseEvent.getSceneX();
        });
        
        rectangle.setOnMouseDragged(mouseEvent -> {
            for (int i=0; i < rectangles.size(); i++) {
                if (!rectangles.get(i).getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                    rectangle.setLayoutX(mouseEvent.getSceneX() - xStart);
                }
            } 
        });
    }
    
    private void addRectangle(MouseEvent e) {
        List<Rectangle> rectangles = getSprites();
        double x = e.getSceneX();
        boolean overlaps = false;
        
        if (!rectangles.isEmpty()) {
            for (int i=0; i < rectangles.size(); i++) {
                Rectangle rectangle = new Rectangle(50, 100);
                rectangle.setTranslateX(x - 25);
                if (rectangles.get(i).getBoundsInParent().intersects(rectangle.getBoundsInParent())) {
                    System.out.println("Dont place");
                    overlaps = true;
                }
            }
        }
        
        if (!overlaps) {
            Rectangle rectangle = new Rectangle(50, 100);
            rectangle.setTranslateX(x - 25);
            pane.getChildren().add(rectangle);
            dragRectangle(rectangle);
        }
    }
    
    private List<Rectangle> getSprites() {
        List<Rectangle> rectangles = new ArrayList<>();
        for (Node n : pane.getChildren()) {
                rectangles.add((Rectangle) n); 
        }
        return rectangles;
    }
}


