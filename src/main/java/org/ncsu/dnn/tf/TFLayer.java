package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayerType;

import java.io.PrintStream;
import java.util.Arrays;

public abstract class TFLayer {
    static final String KEY_NAME = "name";
    static final String KEY_INPUT = "input";
    static final String KEY_OUTPUT = "output";
    private static final String SNIPPET_INIT = SimpleCodeGenerator.SNIPPETS.getString("layer.snippet.init");
    private static final String SNIPPET_ADD = SimpleCodeGenerator.SNIPPETS.getString("layer.snippet.add");
    private static final String END_POINT = "end_point";
    private static final String DEFAULT_INPUT = "net";
    static final String DEFAULT_OUTPUT = "net";
    static TFLayer lastOuputNumber;
    String name;
    protected String input, output;
    int[] outputShape;
    abstract String inlineCode(PrintStream out, String indent, String scope);

    TFLayer(Param param) {
        this.input = param.getOrDefault(KEY_INPUT, DEFAULT_INPUT);
        this.output = param.getOrDefault(KEY_OUTPUT, DEFAULT_OUTPUT);
        this.name = param.getOrDefault(KEY_NAME, param.caffeLayer.getName());
//        if (this.name.contains("/")) {
//            this.name = this.name.substring(this.name.lastIndexOf('/') + 1);
//        }
        this.outputShape = param.shape.clone();
        param.param.remove(KEY_NAME); // In case the name is incorrectly passed to other layer
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
