package org.JStudio.UI;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import org.JStudio.Core.Track;
import org.JStudio.Plugins.Plugin;

public class PipelineNode extends StackPane {
    private final Label name = new Label();
    private final Circle inputPort = new Circle(), outputPort = new Circle();
    private double offsetX, offsetY;
    private final Object node;
    private final NodeType type;

    public enum NodeType {
        IN, OUT, PLUGIN
    }

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
            name.setText("Test Plugin");
            getChildren().addAll(name, inputPort, outputPort);
            setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        //open this plugin
                    } else if (e.getButton() == MouseButton.SECONDARY) {
                        //remove this plugin from the pipeline and from the track
                    }
                }
            });
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

        setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                offsetX = e.getX();
                offsetY = e.getY();
            }
        });

        setOnMouseDragged(e -> {
            Point2D localPoint = getParent().sceneToLocal(e.getSceneX(), e.getSceneY());
            relocate(localPoint.getX() - offsetX, localPoint.getY() - offsetY);
            e.consume();
        });
    }

    public Circle getInputPort() {
        return inputPort;
    }

    public Circle getOutputPort() {
        return outputPort;
    }

    public Object getNode() {
        return node;
    }

    public boolean hasInput() {
        return type == NodeType.PLUGIN || type == NodeType.OUT;
    }

    public boolean hasOutput() {
        return type == NodeType.PLUGIN || type == NodeType.IN;
    }

}
