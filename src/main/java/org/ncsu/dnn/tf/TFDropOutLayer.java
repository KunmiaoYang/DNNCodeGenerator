package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;

public class TFDropOutLayer extends TFLayer {
    private static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.dropout.inline");
    double ratio;
    TFDropOutLayer(Param param) {
        super(param);
        this.ratio = 1.0 - Double.parseDouble(param.caffeLayer.paramMap.get("dropout_param.dropout_ratio").getVal());
    }

    @Override
    String inlineCode(PrintStream out, String indent, String scope) {
        out.printf(INLINE, indent, output, input, String.valueOf(ratio), name);
        return indent;
    }
}
