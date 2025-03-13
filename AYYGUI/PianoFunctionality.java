package com.example.pianoapp;

import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.midi.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class pianoApp extends Application {
    private MidiChannel channel;

    private List<Note> notes = Arrays.asList(
      new Note("C", KeyCode.A, 60),
      new Note("D", KeyCode.S, 62),
      new Note("E", KeyCode.D, 64),
      new Note("F", KeyCode.F, 65),
      new Note("G", KeyCode.G, 67),
      new Note("A", KeyCode.H, 69),
      new Note("B", KeyCode.J, 71),
      new Note("C", KeyCode.K, 72),
      new Note("D", KeyCode.L, 74),
      new Note("E", KeyCode.SEMICOLON, 76)
    );

    private HBox root = new HBox(15);


    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(createContent());
        scene.setOnKeyPressed(e -> onKeyPress(e.getCode()));

        stage.setScene(scene);
        stage.show();
    }

    private void onKeyPress(KeyCode code) {
        root.getChildren()
                .stream()
                .map(view -> (NoteView) view)
                .filter(view -> view.note.key.equals(code))
                .forEach(view -> {
                    FillTransition ft = new FillTransition(
                            Duration.seconds(0.15),
                            view.bg,
                            Color.WHITE,
                            Color.BLACK
                    );
                    ft.setCycleCount(2);
                    ft.setAutoReverse(true);
                    ft.play();
                    channel.noteOn(view.note.number, 90);
                });
    }

    private Parent createContent() {
        loadChannel();

        root.setPrefSize(600,150);

        notes.forEach(note -> {
            NoteView view = new NoteView(note);
            root.getChildren().addAll(view);
        });
        return root;
    }

    private void loadChannel() {
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            synthesizer.loadInstrument(synthesizer.getDefaultSoundbank().getInstruments()[0]);
            channel = synthesizer.getChannels()[0];

        } catch (MidiUnavailableException e) {
            System.out.println("Cannot get the Synth.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }


    public class Note {
        private String name;
        private KeyCode key;
        private int number;

        public Note(String name, KeyCode key, int number) {
            this.name = name;
            this.key = key;
            this.number = number;
        }
    }

    public class NoteView extends StackPane {
    private Note note;
    private Rectangle bg = new Rectangle(50,150, Color.WHITE);

        public NoteView(Note note) {
            this.note = note;

            bg.setStroke(Color.BLACK);
            bg.setStrokeWidth(2.5);
            getChildren().addAll(bg, new Text(note.name));
        }
    }
}

