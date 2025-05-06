package org.JStudio.Utils;

/**
 * Represents the data for a tool or plugin in JStudio, including its name, description, input types, and output type.
 */
public class Descriptions {
    private String name;
    private String description; // what is the tool
    private String[] inputs;
    private String output;

    /**
     * Constructor that creates a Description object
     * @param name the name of the tool
     * @param description a short description of what the tool does
     * @param inputs all the inputs the tool requires
     * @param output the expected output of the tool
     */
    public Descriptions(String name, String description, String[] inputs, String output) {
        this.name = name;
        this.description = description;
        this.inputs = inputs;
        this.output = output;
    }

    /**
     * Returns the name of the tool.
     *
     * @return The tool's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the tool.
     *
     * @param name The name to set for the tool.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of the tool.
     *
     * @return A string explaining what the tool does.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the tool.
     *
     * @param description A textual explanation of the tool's purpose.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the expected input types for the tool.
     *
     * @return An array of input descriptions.
     */
    public String[] getInputs() {
        return inputs;
    }

    /**
     * Sets the expected input types for the tool.
     *
     * @param inputs An array of input descriptions.
     */
    public void setInputs(String[] inputs) {
        this.inputs = inputs;
    }

    /**
     * Returns the output type of the tool.
     *
     * @return A description of the output.
     */
    public String getOutput() {
        return output;
    }

    /**
     * Sets the output type of the tool.
     *
     * @param output A description of the output.
     */
    public void setOutput(String output) {
        this.output = output;
    }

}
