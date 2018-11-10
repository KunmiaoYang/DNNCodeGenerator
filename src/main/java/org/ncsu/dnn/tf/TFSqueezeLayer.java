package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TFSqueezeLayer extends TFLayer {
    private static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.squeeze.inline");
    List<Integer> axis = new ArrayList<>();
    TFSqueezeLayer(CaffeLayer caffeLayer, int[] shape, Map<String, String> param) {
        super(caffeLayer, shape, param);
        int len = 0;
        for (int i = 0; i < shape.length; i++) {
            if (1 == shape[i]) axis.add(i);
            else this.outputShape[len++] = this.outputShape[i];
        }
        this.outputShape = Arrays.copyOf(this.outputShape, len);
    }

    @Override
    String inlineCode(PrintStream out, String indent, String scope) {
        out.printf(INLINE, indent, output, input, axis.toString(), name);
        return indent;
    }

    @Override
    void generateCode(PrintStream out, String indent) {

    }
}
