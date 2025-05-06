package org.JStudio.UI;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import org.JStudio.Core.Track;
import org.JStudio.Plugins.Plugin;

import java.util.ArrayList;

public class PluginRenderer {
    private final Pane pluginPane;
    private final ArrayList<PipelineNode> nodes = new ArrayList<>();
    private final ArrayList<ConnectionUI> connections = new ArrayList<>();
    private Track currentTrack;

    public PluginRenderer(Pane pluginPane) {
        this.pluginPane = pluginPane;
    }

    public void clear() {
        pluginPane.getChildren().clear();
        nodes.clear();
        connections.clear();
    }

    public void renderGraph(Track track) {
        clear();
        this.currentTrack = track;

        if (track == null) {
            Label message = new Label("No Track Selected: Plugins Unavailable");
            message.setLayoutX(pluginPane.getWidth() / 2);
            message.setLayoutY(pluginPane.getHeight() / 2);
            pluginPane.getChildren().add(message);
            return;
        }

        double startX = 50;
        double startY = pluginPane.getHeight() / 2 - 16;

        PipelineNode inputNode = new PipelineNode(track);
        inputNode.setLayoutX(startX);
        inputNode.setLayoutY(startY);
        pluginPane.getChildren().add(inputNode);
        nodes.add(inputNode);

        startX += 200;
        PipelineNode lastNode = inputNode;

        for (Plugin plugin : track.getPlugins()) {
            PipelineNode pluginNode = new PipelineNode(plugin);
            pluginNode.setLayoutX(startX);
            pluginNode.setLayoutY(startY);
            pluginPane.getChildren().add(pluginNode);
            nodes.add(pluginNode);

            ConnectionUI connection = new ConnectionUI(lastNode.getOutputPort(), pluginNode.getInputPort());
            pluginPane.getChildren().add(connection);
            connections.add(connection);
            Platform.runLater(connection::update);

            lastNode = pluginNode;
            startX += 200;

            attachPluginNodeListener(pluginNode);
        }

        PipelineNode outputNode = new PipelineNode("OUTPUT_NODE");
        outputNode.setLayoutX(startX);
        outputNode.setLayoutY(startY);
        pluginPane.getChildren().add(outputNode);
        nodes.add(outputNode);

        ConnectionUI finalConnection = new ConnectionUI(lastNode.getOutputPort(), outputNode.getInputPort());
        pluginPane.getChildren().add(finalConnection);
        connections.add(finalConnection);
        Platform.runLater(finalConnection::update);
    }

    private void attachPluginNodeListener(PipelineNode node) {
        node.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                deleteNode(node);
                renderGraph(currentTrack);
            }
        });
    }

    public void deleteNode(PipelineNode node) {
        if (node.getNode() instanceof Plugin plugin) {
            int index = currentTrack.getPlugins().indexOf(plugin);
            if (index == -1) return;

            currentTrack.getPlugins().remove(index);
        }
    }

    public ArrayList<PipelineNode> getNodes() {
        return nodes;
    }

    public ArrayList<ConnectionUI> getConnections() {
        return connections;
    }
}
