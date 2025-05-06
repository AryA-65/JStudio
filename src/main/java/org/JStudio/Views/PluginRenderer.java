package org.JStudio.Views;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import org.JStudio.Models.Core.Track;
import org.JStudio.Plugins.Plugin;

import java.util.ArrayList;

//handles the pipeline display window
public class PluginRenderer {
    private final Pane pluginPane;
    private final ArrayList<PipelineNode> nodes = new ArrayList<>();
    private final ArrayList<ConnectionUI> connections = new ArrayList<>();
    private Track currentTrack;

    //constructor, set pluginPane
    public PluginRenderer(Pane pluginPane) {
        this.pluginPane = pluginPane;
    }

    //clears the pane
    public void clear() {
        pluginPane.getChildren().clear();
        nodes.clear();
        connections.clear();
    }

    //displays the window and it's components
    public void renderGraph(Track track) {
        clear();
        this.currentTrack = track;

        //make sure a track is selected
        if (track == null) {
            Label message = new Label("No Track Selected: Plugins Unavailable");
            message.setLayoutX(pluginPane.getWidth() / 2);
            message.setLayoutY(pluginPane.getHeight() / 2);
            pluginPane.getChildren().add(message);
            return;
        }

        double startX = 50;
        double startY = pluginPane.getHeight() / 2 - 16;

        //gets the input node
        PipelineNode inputNode = new PipelineNode(track);
        inputNode.setLayoutX(startX);
        inputNode.setLayoutY(startY);
        pluginPane.getChildren().add(inputNode);
        nodes.add(inputNode);

        startX += 200;
        PipelineNode lastNode = inputNode;

        //displays the plugins as pipeline nodes and their connections to eachother
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

        //displays the output node
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

    //delete node on right-click
    private void attachPluginNodeListener(PipelineNode node) {
        node.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                deleteNode(node);
                renderGraph(currentTrack);
            }
        });
    }

    //deletes node
    public void deleteNode(PipelineNode node) {
        if (node.getNode() instanceof Plugin plugin) {
            int index = currentTrack.getPlugins().indexOf(plugin);
            if (index == -1) return;

            currentTrack.getPlugins().remove(index);
        }
    }

    //getters
    public ArrayList<PipelineNode> getNodes() {
        return nodes;
    }

    public ArrayList<ConnectionUI> getConnections() {
        return connections;
    }
}
