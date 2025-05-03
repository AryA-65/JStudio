package org.JStudio.UI;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class PluginUI extends StackPane {
    private final Label plug_name, plug_desc;
    private final ImageView background;
    private final VBox content = new VBox();

    public PluginUI(String name, String desc, String imagePath) {
        plug_name = new Label(name);
        plug_desc = new Label(desc);
        background = new ImageView(new Image(imagePath));

        setWidth(200);
        setHeight(48);
        setId("plugin_ui");

        Rectangle clip = new Rectangle(getWidth(), getHeight());
        setClip(clip);

        background.setFitWidth(getWidth());
        background.setFitHeight(getHeight());
        background.setPreserveRatio(false);

        content.setPrefSize(getWidth(), getHeight());
        content.setSpacing(2);
        content.setAlignment(Pos.CENTER);
        content.setOpacity(0);
        content.setId("plugin_content");

        plug_name.setTranslateY(50);
        plug_desc.setTranslateY(50);
        plug_name.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        plug_desc.setStyle("-fx-text-fill: white;");

        content.getChildren().addAll(plug_name, plug_desc);
        getChildren().addAll(background, content);

        setupMouseHandler();
    }

    private void setupMouseHandler() {
        setOnMouseEntered(e -> {
            ScaleTransition zoomIn = new ScaleTransition(Duration.millis(300), background);
            zoomIn.setToX(1.1);
            zoomIn.setToY(1.1);

            TranslateTransition nameIn = new TranslateTransition(Duration.millis(300), plug_name);
            nameIn.setToY(0);
            TranslateTransition descIn = new TranslateTransition(Duration.millis(300), plug_desc);
            descIn.setToY(0);
            FadeTransition contentFade = new FadeTransition(Duration.millis(300), content);
            contentFade.setToValue(1);

            new ParallelTransition(zoomIn, nameIn, descIn, contentFade).play();
        });

        setOnMouseExited(e -> {
            ScaleTransition zoomOut = new ScaleTransition(Duration.millis(300), background);
            zoomOut.setToX(1.0);
            zoomOut.setToY(1.0);

            TranslateTransition nameOut = new TranslateTransition(Duration.millis(300), plug_name);
            nameOut.setToY(50);
            TranslateTransition descOut = new TranslateTransition(Duration.millis(300), plug_desc);
            descOut.setToY(50);
            FadeTransition contentFade = new FadeTransition(Duration.millis(300), content);
            contentFade.setToValue(0);

            new ParallelTransition(zoomOut, nameOut, descOut, contentFade).play();
        });
    }
}
