package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;

public class TFSoftmaxLayer extends TFLayer {
    private static final String INLINE = CodeGenerator.SNIPPETS.getString("layer.softmax.inline");
    TFSoftmaxLayer(CaffeLayer caffeLayer, int[] shape) {
        super(caffeLayer, shape);
    }

    @Override
    String inlineCode(PrintStream out, String indent, String scope) {
        return indent;
    }

    @Override
    void generateCode(PrintStream out, String indent) {
        out.printf(INLINE, indent, name, input, name);
    }
}
