package org.JStudio.UI;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import org.JStudio.Plugins.Models.Plugin;

public class PluginNode extends StackPane {
    private final Label name = new Label();
    private final Circle inputPort = new Circle(), outputPort = new Circle();
    private double offsetX, offsetY;
    private final Object plugin; //turn this into plugin

    public PluginNode(Object plugin) {
        this.plugin = plugin;
        setPrefSize(108, 32); //placeholder values
        setId("plugin_node");

        if ((plugin instanceof Plugin)) {
            name.setText(((Plugin) plugin).getName());
        } else {
            name.setText("Plugin");
        }
        name.setFont(new Font("Inter", 10));

        inputPort.setRadius(5);
        outputPort.setRadius(5);

        outputPort.setTranslateX(54);
        outputPort.setTranslateY(0);

        inputPort.setTranslateX(-54);
        inputPort.setTranslateY(0);

        inputPort.setId("input_port");
        outputPort.setId("output_port");

        setOnMousePressed(e -> {
            offsetX = e.getX();
            offsetY = e.getY();
        });

        setOnMouseDragged(e -> {
            Point2D localPoint = getParent().sceneToLocal(e.getSceneX(), e.getSceneY());
            relocate(localPoint.getX() - offsetX, localPoint.getY() - offsetY);
            e.consume();
        });

        setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
                //opening the plugin
            }
        });

        getChildren().addAll(name, inputPort, outputPort);
    }

    public Circle getInputPort() {
        return inputPort;
    }

    public Circle getOutputPort() {
        return outputPort;
    }

    public Object getPlugin() {
        return plugin;
    }
}
