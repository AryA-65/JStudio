package org.JStudio.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.JStudio.Utils.Descriptions;

/**
 * Class to describe each of the tools to the user
 */
public class HelpController {
    @FXML
    VBox toolBox;
    @FXML
    ScrollPane scrollPane;

    /**
     * The array of all descriptions.
     */
    Descriptions[] tools = {
            new Descriptions(
                    "Plugin: Reverb",
                    "Creates a roomy environment where sound waves are reflected close together",
                    new String[]{"Pre-Delay: The time it takes for the sound to "
                            + "first reflect", "Decay: The time it takes for the reflected audio to decay",
                            "Diffusion: The spacing of the reflected sound waves",
                            "Wet/Dry Mix: The amount of original (dry) and modified (wet) audio",
                            "Output Gain: The volume of the modified audio"},
                    "Reverberated Audio"
            ),
            new Descriptions(
                    "Plugin: Echo",
                    "Creates reflected audio waves spaced far apart",
                    new String[]{"Pre-Delay: The time it takes for the sound to "
                            + "first reflect", "Decay: The time it takes for the reflected audio to decay",
                            "Diffusion: The spacing of the reflected sound waves", "Number of Echos: The number of echos heard",
                            "Wet/Dry Mix: The amount of original (dry) and modified (wet) audio",
                            "Output Gain: The volume of the modified audio"},
                    "Echoed Audio"
            ),
            new Descriptions(
                    "Plugin: Flanger",
                    "Creates wooshing effect by copying the audio, and delaying it with modulated delays",
                    new String[]{"Frequency: The rate at which the delays are modulated",
                            "Deviation: The amplitude of the oscillating modulation function",
                            "Wet/Dry Mix: The amount of original (dry) and modified (wet) audio",
                            "Output Gain: The volume of the modified audio"},
                    "Flanged Audio"
            ),
            new Descriptions(
                    "Plugin: Chorus",
                    "Creates a multiple instrument/voice effect by copying the audio, and delaying it with modulated delays",
                    new String[]{"Frequency: The rate at which the delays are modulated",
                            "Deviation: The amplitude of the oscillating modulation function",
                            "Wet/Dry Mix: The amount of original (dry) and modified (wet) audio",
                            "Output Gain: The volume of the modified audio"},
                    "Chorus Audio"
            ),
            new Descriptions(
                    "Plugin: Phaser",
                    "Creates a sweeping sound by copying the audio, and shifting the phase with modulated phase shifts",
                    new String[]{"Frequency: The rate at which the phase shifts are modulated",
                            "Deviation: The amplitude of the oscillating modulation function",
                            "Wet/Dry Mix: The amount of original (dry) and modified (wet) audio",
                            "Output Gain: The volume of the modified audio"},
                    "Phased Audio"
            ),
            new Descriptions(
                    "Plugin: Equalizer",
                    "Modifies the volume of certain frequencies by using FFT algorithm to get the amplitude of all frequencies in the audio and adjusting the amplitudes",
                    new String[]{"Center Frequency Factor: The factor multiple on the range around the center frequencies of each slider"},
                    "Equalized Audio"
            ),
            new Descriptions(
                    "Plugin: Audio Amplitude",
                    "Modifies the amplitude of an audio file",
                    new String[]{"An audio file (type) wav", "A double value with the knob"},
                    "Audio with modified amplitude levels"
            ),
            new Descriptions(
                    "Plugin: Basic Audio Filters",
                    "Uses basic math methods to cut-off certain frequencies",
                    new String[]{"An audio file (type) wav", "Frequency that is to be used to cut off"},
                    "Audio file with frequencies above or below certain level cut off"
            ),
            new Descriptions(
                    "Plugin: Butterworth filter",
                    "Uses advanced math logic to apply different types of audio filters",
                    new String[]{"An audio file (type) wav", "Frequency that is to be used to cut off", "Type of filter to be applied"},
                    "Audio file with an applied audio filter"
            ),
            new Descriptions(
                    "Piano",
                    "Allows the user to create a custom song using piano keys",
                    new String[]{"Notes: Place notes on the track of each key to add to the song"},
                    "Custom Song With Piano Keys"
            ),
            new Descriptions(
                    "Synthesizer Piano",
                    "Allows the user to create a custom song using custom sounds generated by the synthesizer",
                    new String[]{"Add Track: Create a new track with a new synthesizer preset",
                            "Notes: Place notes on the track of each generated key that will play the sound of the synthesizer preset"},
                    "Custom Song With Custom Sounds"
            ),
            new Descriptions(
                    "Synthesizer",
                    "Generates sound waves based on keyboard inputs with set frequencies with the ability to add an glide effect and speed modifier",
                    new String[]{"3 different oscillators with individual waveform selection ", "Tone and Volume of each oscillator", ""},
                    "Audio file (type) wav that contains the played synth"
            ),
    };

    /**
     * Method that initializes the Help UI
     */
    public void initialize() {
        toolBox.setSpacing(20);

        //Getting each element, creating a node with it then adding it to the scene.
        for (Descriptions tool : tools) {
            VBox toolVBox = new VBox(8);
            toolVBox.setStyle("-fx-padding: 15; -fx-border-color: #ccc; -fx-border-radius: 5;");

            Label nameLabel = new Label("Tool: " + tool.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

            Label descLabel = new Label("Description: " + tool.getDescription());
            descLabel.setWrapText(true);

            StringBuilder inputs = new StringBuilder("Inputs: ");
            for (String input : tool.getInputs()) {
                inputs.append("\n- ").append(input);
            }
            Label inputsLabel = new Label(inputs.toString());

            Label outputLabel = new Label("Output: " + tool.getOutput());

            toolVBox.getChildren().addAll(
                    nameLabel,
                    descLabel,
                    inputsLabel,
                    outputLabel
            );

            toolBox.getChildren().add(toolVBox);
        }
    }
}
