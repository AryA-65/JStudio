package org.JStudio.Utils;

public class Descriptions {
    private String name;
    private String description; // what is the tool
    private String[] inputs;
    private String output;

    public Descriptions(String name, String description, String[] inputs, String output) {
        this.name = name;
        this.description = description;
        this.inputs = inputs;
        this.output = output;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getInputs() {
        return inputs;
    }

    public void setInputs(String[] inputs) {
        this.inputs = inputs;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
