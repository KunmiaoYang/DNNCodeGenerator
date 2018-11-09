package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.Arrays;

public abstract class TFLayer {
    static final String SNIPPET_INIT = CodeGenerator.SNIPPETS.getString("layer.snippet.init");
    static final String SNIPPET_ADD = CodeGenerator.SNIPPETS.getString("layer.snippet.add");
    protected static final String END_POINT = "end_point";
    private static final String DEFAULT_INPUT = "net";
    private static final String DEFAULT_OUTPUT = "net";
    String name;
    protected String input, output;
    int[] outputShape;
    abstract String inlineCode(PrintStream out, String indent, String scope);

    TFLayer(CaffeLayer caffeLayer, int[] shape) {
        this.input = DEFAULT_INPUT;
        this.output = DEFAULT_OUTPUT;
        this.name = caffeLayer.getName();
        if (this.name.contains("/")) {
            this.name = this.name.substring(this.name.lastIndexOf('/') + 1);
        }
        this.outputShape = shape.clone();
    }

    void generateCode(PrintStream out, String indent) {
        out.printf(SNIPPET_INIT, indent, this.name);
        indent = this.inlineCode(out, indent, END_POINT);
        out.printf(SNIPPET_ADD, indent, this.output);
    }

    @Override
    public String toString() {
        return this.name + " " + Arrays.toString(this.outputShape);
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
