package org.ncsu.dnn.tf;

import org.ncsu.dnn.SimpleCodeGenerator;

import java.io.PrintStream;
import java.util.Map;

import static org.ncsu.dnn.tf.TFModel.KEY_INDENT_STRING;

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
        String indent = context.get(KEY_INDENT_BASE);
        String indentString = context.get(KEY_INDENT_STRING);
        for (char c: name.toCharArray()) {
            if (c == '/') indent += indentString;
        }
        out.printf(INLINE, indent, name, input, name);
    }
}
