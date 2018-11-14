package org.ncsu.dnn.tf;

import java.io.PrintStream;
import java.util.Map;

public class TFSoftmaxLayer extends TFLayer {
    private static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.softmax.inline");
    TFSoftmaxLayer(Param param) {
        super(param);
    }

    @Override
    void inlineCode(PrintStream out, Map<String, String> context) {
    }

    @Override
    void generateCode(PrintStream out, Map<String, String> context) {
        out.printf(INLINE, context.get(KEY_INDENT), name, input, name);
    }
}
