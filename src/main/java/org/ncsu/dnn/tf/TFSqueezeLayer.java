package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TFSqueezeLayer extends TFLayer {
    public static final String DEFAULT_SQUEEZE_NAME = "SpatialSqueeze";
    private static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.squeeze.inline");
    List<Integer> axis = new ArrayList<>();
    TFSqueezeLayer(Param param) {
        super(param);
        int len = 0;
        for (int i = 0; i < param.shape.length; i++) {
            if (1 == param.shape[i]) axis.add(i);
            else this.outputShape[len++] = this.outputShape[i];
        }
        this.outputShape = Arrays.copyOf(this.outputShape, len);
    }

    @Override
    String inlineCode(PrintStream out, String indent, String scope) {
        out.printf(INLINE, indent, output, input, axis.toString(), name);
        return indent;
    }
}
