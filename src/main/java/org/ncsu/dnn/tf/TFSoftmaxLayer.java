package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.Map;

public class TFSoftmaxLayer extends TFLayer {
    private static final String INLINE = CodeGenerator.SNIPPETS.getString("layer.softmax.inline");
    TFSoftmaxLayer(CaffeLayer caffeLayer, int[] shape, Map<String, String> param) {
        super(caffeLayer, shape, param);
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
