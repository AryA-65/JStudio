package org.JStudio.Views;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

//handles the plugin UI
public class PluginUI extends StackPane {
    private final Label plug_name, plug_desc;
    private ImageView background;
    private final VBox content = new VBox();
//    private final Object plugin;

    //constructor, sets parameters
    public PluginUI(String name, String desc, String imagePath) {
        plug_name = new Label(name);
        plug_desc = new Label(desc);
//        this.plugin = plugin;
        background = null;
        if (imagePath != null) background = new ImageView(new Image(imagePath));

//        setWidth(200);
        setHeight(48);
        setId("plugin_ui");
        setAlignment(Pos.CENTER);

        if (imagePath != null) {
            background.setFitWidth(getWidth());
            background.setFitHeight(getHeight());
            background.setPreserveRatio(false);
        }

        content.setPrefSize(Region.USE_COMPUTED_SIZE, 48);
        content.setSpacing(2);
        content.setAlignment(Pos.CENTER);
        content.setOpacity(0);
        content.setId("plugin_content");
        Rectangle clip = new Rectangle(200, 48);
        content.setClip(clip);

        plug_name.setTranslateY(50);
        plug_desc.setTranslateY(50);

        content.getChildren().addAll(plug_name, plug_desc);
//        getChildren().addAll(background, content);
        getChildren().add(content);

        setupMouseHandler();
    }

    // manages zoom and transitions
    private void setupMouseHandler() {
        setOnMouseEntered(e -> {
            ScaleTransition zoomIn = null;
            if (background != null) {
                zoomIn = new ScaleTransition(Duration.millis(300), background);
                zoomIn.setToX(1.1);
                zoomIn.setToY(1.1);
            }

            TranslateTransition nameIn = new TranslateTransition(Duration.millis(300), plug_name);
            nameIn.setToY(0);
            TranslateTransition descIn = new TranslateTransition(Duration.millis(300), plug_desc);
            descIn.setToY(0);
            FadeTransition contentFade = new FadeTransition(Duration.millis(300), content);
            contentFade.setToValue(1);

            if (background != null) new ParallelTransition(zoomIn, nameIn, descIn, contentFade).play();
            else new ParallelTransition(nameIn, descIn, contentFade).play();
        });

        setOnMouseExited(e -> {
            ScaleTransition zoomOut = null;
            if (background != null) {
                zoomOut = new ScaleTransition(Duration.millis(300), background);
                zoomOut.setToX(1.0);
                zoomOut.setToY(1.0);
            }

            TranslateTransition nameOut = new TranslateTransition(Duration.millis(300), plug_name);
            nameOut.setToY(50);
            TranslateTransition descOut = new TranslateTransition(Duration.millis(300), plug_desc);
            descOut.setToY(50);
            FadeTransition contentFade = new FadeTransition(Duration.millis(300), content);
            contentFade.setToValue(0);

            if (background != null) new ParallelTransition(zoomOut, nameOut, descOut, contentFade).play();
            else new ParallelTransition(nameOut, descOut, contentFade).play();
        });
    }
}
