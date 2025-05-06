package org.JStudio.Views;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import org.JStudio.Models.Core.Track;
import org.JStudio.Plugins.Plugin;

//Handles the pipeline nodes
public class PipelineNode extends StackPane {
    private final Label name = new Label();
    private final Circle inputPort = new Circle(), outputPort = new Circle();
    private double offsetX, offsetY;
    private final Object node;
    private final NodeType type;

    //types of nodes
    public enum NodeType {
        IN, OUT, PLUGIN
    }

    //constructor, sets parameters
    public PipelineNode(Object node) {
        this.node = node;
        setPrefSize(108, 32);
        setId("pipeline_node");

        inputPort.setRadius(5);
        outputPort.setRadius(5);
        inputPort.setTranslateX(-54);
        outputPort.setTranslateX(54);
        inputPort.setId("input_port");
        outputPort.setId("output_port");

        name.setFont(new Font("Inter", 10));

        if (node instanceof Plugin plugin) {
            type = NodeType.PLUGIN;
//            name.setText(plugin.getName());
            name.setText(plugin.getName().get());
            getChildren().addAll(name, inputPort, outputPort);
        } else if (node instanceof Track track) {
            type = NodeType.IN;
            name.textProperty().bind(track.getName());
            getChildren().addAll(outputPort, name);
        } else if (node instanceof String s && s.equals("OUTPUT_NODE")) {
            type = NodeType.OUT;
            name.setText("Output");
            getChildren().addAll(inputPort, name);
        } else {
            throw new IllegalArgumentException("Unsupported node type");
        }

        //get mouse position
        setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                offsetX = e.getX();
                offsetY = e.getY();
            }
        });

        //dragging logic to move pipeline nodes
        setOnMouseDragged(e -> {
            //get mouse data
            Point2D localPoint = getParent().sceneToLocal(e.getSceneX(), e.getSceneY());
            double newX = localPoint.getX() - offsetX;
            double newY = localPoint.getY() - offsetY;

            double parentWidth = ((Region) getParent()).getWidth();
            double parentHeight = ((Region) getParent()).getHeight();

            double nodeWidth = getBoundsInParent().getWidth();
            double nodeHeight = getBoundsInParent().getHeight();

            newX = Math.max(0, Math.min(newX, parentWidth - nodeWidth));
            newY = Math.max(0, Math.min(newY, parentHeight - nodeHeight));

            //move nodes
            relocate(newX, newY);
            e.consume();
        });
    }

    //getters
    public Circle getInputPort() {
        return inputPort;
    }

    public Circle getOutputPort() {
        return outputPort;
    }

    public Object getNode() {
        return node;
    }

    //checks for input/output
    public boolean hasInput() {
        return type == NodeType.PLUGIN || type == NodeType.OUT;
    }

    public boolean hasOutput() {
        return type == NodeType.PLUGIN || type == NodeType.IN;
    }

}
