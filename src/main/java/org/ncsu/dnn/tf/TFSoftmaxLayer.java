package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;

public class TFSoftmaxLayer extends TFLayer {
    private static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.softmax.inline");
    TFSoftmaxLayer(Param param) {
        super(param);
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
