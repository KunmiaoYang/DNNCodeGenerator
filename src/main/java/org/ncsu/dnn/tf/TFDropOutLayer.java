package org.ncsu.dnn.tf;

import org.ncsu.dnn.SimpleCodeGenerator;

import java.io.PrintStream;
import java.util.Map;

import static org.ncsu.dnn.tf.TFModel.KEY_INDENT;

public class TFDropOutLayer extends TFLayer {
    private static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.dropout.inline");
    double ratio;
    TFDropOutLayer(Param param) {
        super(param);
        this.ratio = 1.0 - Double.parseDouble(param.caffeLayer.paramMap.get("dropout_param.dropout_ratio").getVal());
    }

    @Override
    void inlineCode(PrintStream out, Map<String, String> context) {
        out.printf(INLINE, context.get(KEY_INDENT), output, input, String.valueOf(ratio), context.get(KEY_SCOPE_STRING));
    }
}
