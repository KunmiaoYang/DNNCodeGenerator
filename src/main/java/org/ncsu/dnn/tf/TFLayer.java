package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;

public abstract class TFLayer {
    static final String KEY_INPUT = "input";
    static final String KEY_OUTPUT = "output";
    private static final String SNIPPET_INIT = SimpleCodeGenerator.SNIPPETS.getString("layer.snippet.init");
    private static final String SNIPPET_ADD = SimpleCodeGenerator.SNIPPETS.getString("layer.snippet.add");
    private static final String END_POINT = "end_point";
    private static final String DEFAULT_INPUT = "net";
    private static final String DEFAULT_OUTPUT = "net";
    String name;
    protected String input, output;
    int[] outputShape;
    abstract String inlineCode(PrintStream out, String indent, String scope);

    TFLayer(CaffeLayer caffeLayer, int[] shape, Map<String, String> param) {
        this.input = param.getOrDefault(KEY_INPUT, DEFAULT_INPUT);
        this.output = param.getOrDefault(KEY_OUTPUT, DEFAULT_OUTPUT);
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
}
