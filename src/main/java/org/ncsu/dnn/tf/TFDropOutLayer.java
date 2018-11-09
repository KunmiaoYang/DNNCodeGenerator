package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.Map;

public class TFDropOutLayer extends TFLayer {
    private static final String INLINE = CodeGenerator.SNIPPETS.getString("layer.dropout.inline");
    double ratio;
    TFDropOutLayer(CaffeLayer caffeLayer, int[] shape, Map<String, String> param) {
        super(caffeLayer, shape, param);
        this.ratio = 1.0 - Double.parseDouble(caffeLayer.paramMap.get("dropout_param.dropout_ratio").getVal());
    }

    @Override
    String inlineCode(PrintStream out, String indent, String scope) {
        out.printf(INLINE, indent, output, input, String.valueOf(ratio), name);
        return indent;
    }
}
