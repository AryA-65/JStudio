package org.JStudio.Views;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.util.Objects;

//The UI behind the different "sections" of audio files
public class SectionUI extends VBox {
    private final ImageView expand_img = new ImageView(new Image("/icons/arrow.png"));
    private final Button section_btn = new Button("", expand_img);
    private final VBox section_content = new VBox();
    private final Timeline expand_anim = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(section_content.prefHeightProperty(), 0)), new KeyFrame(Duration.millis(150), new KeyValue(section_content.prefHeightProperty(), Region.USE_COMPUTED_SIZE, Interpolator.EASE_IN)));
    private final Timeline collapse_anim = new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(section_content.prefHeightProperty(), Region.USE_COMPUTED_SIZE)), new KeyFrame(Duration.millis(150), new KeyValue(section_content.prefHeightProperty(), 0, Interpolator.EASE_IN)));
    private final RotateTransition rotate = new RotateTransition(Duration.millis(150), expand_img);
    private final String sectionName;

    //setup parameters
    public SectionUI(File file) {
        setAlignment(Pos.TOP_CENTER);

        sectionName = file.getName();

        expand_img.setSmooth(true);
        expand_img.setFitWidth(16);
        expand_img.setFitHeight(16);

        section_content.setAlignment(Pos.TOP_CENTER);
        section_content.setSpacing(10);
        section_content.setPrefHeight(Region.USE_COMPUTED_SIZE);
        VBox.setMargin(section_content, new Insets(0, 10, 0, 10));

        section_btn.setText(file.getName());
        section_btn.setPrefWidth(246);
        section_btn.setAlignment(Pos.TOP_LEFT);
        VBox.setMargin(section_btn, new Insets(0, 0, 10, 0));

        rotate.setByAngle(-180);

        //collapse section
        section_btn.setOnAction(e -> {
            if (section_content.isVisible()) {
                rotate.play();
                collapse_anim.play();
                collapse_anim.setOnFinished(event -> {
                    section_content.setVisible(false);
                    section_content.setManaged(false);
                });
            } else { //expand section
                rotate.play();
                section_content.setVisible(true);
                section_content.setManaged(true);
                expand_anim.play();
            }
        });

        getChildren().addAll(section_btn, section_content);

        //add audio files to the sections
        for (File f : Objects.requireNonNull(file.listFiles())) {
            if (f.exists() && f.isFile()) {
//                if (f.getName().endsWith(".mp3")) {continue;} //remove this line of code when mp3 support is added
                section_content.getChildren().add(new FileUI(f));
            }
        }
    }

    //getter
    public String getSectionName() {
        return sectionName;
    }

    //test code for animation
//    public void fadeOutVBoxAndShrink(VBox vbox, double durationMillis) {
//        ObservableList<Node> children = vbox.getChildren();
//        double initialHeight = vbox.getHeight();
//
//        vbox.setPrefHeight(initialHeight); // lock initial height for animation
//
//        for (int i = 0; i < children.size(); i++) {
//            Node child = children.get(i);
//            Timeline fadeTimeline = new Timeline(
//                    new KeyFrame(Duration.ZERO, new KeyValue(child.opacityProperty(), 1)),
//                    new KeyFrame(Duration.millis(durationMillis), new KeyValue(child.opacityProperty(), 0, Interpolator.EASE_IN))
//            );
//            fadeTimeline.setDelay(Duration.millis(i * 100));
//            fadeTimeline.play();
//        }
//
//        Timeline heightTimeline = new Timeline(
//                new KeyFrame(Duration.ZERO, new KeyValue(vbox.prefHeightProperty(), initialHeight)),
//                new KeyFrame(Duration.millis(durationMillis + children.size() * 100), new KeyValue(vbox.prefHeightProperty(), 0, Interpolator.EASE_IN))
//        );
//        heightTimeline.play();
//    }
//
//    public void fadeInVBoxAndGrow(VBox vbox, double durationMillis) {
//        ObservableList<Node> children = vbox.getChildren();
//
//        // Prepare VBox
//        vbox.setPrefHeight(0);
//
//        for (Node child : children) {
//            child.setOpacity(0);
//        }
//
//        Timeline heightTimeline = new Timeline(
//                new KeyFrame(Duration.ZERO, new KeyValue(vbox.prefHeightProperty(), 0)),
//                new KeyFrame(Duration.millis(durationMillis + children.size() * 100),
//                        new KeyValue(vbox.prefHeightProperty(), Region.USE_COMPUTED_SIZE, Interpolator.EASE_IN))
//        );
//        heightTimeline.setOnFinished(e -> vbox.setPrefHeight(Region.USE_COMPUTED_SIZE));
//        heightTimeline.play();
//
//        for (int i = 0; i < children.size(); i++) {
//            Node child = children.get(i);
//            Timeline fadeTimeline = new Timeline(
//                    new KeyFrame(Duration.ZERO, new KeyValue(child.opacityProperty(), 0)),
//                    new KeyFrame(Duration.millis(durationMillis), new KeyValue(child.opacityProperty(), 1, Interpolator.EASE_IN))
//            );
//            fadeTimeline.setDelay(Duration.millis(i * 100));
//            fadeTimeline.play();
//        }
//    }
}
