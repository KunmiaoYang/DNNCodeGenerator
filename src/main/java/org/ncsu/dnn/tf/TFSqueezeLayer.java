package org.ncsu.dnn.tf;

import org.ncsu.dnn.SimpleCodeGenerator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.ncsu.dnn.tf.TFModel.KEY_INDENT;

public class TFSqueezeLayer extends TFLayer {
    public static final String DEFAULT_SQUEEZE_NAME = "SpatialSqueeze";
    private static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.squeeze.inline");
    List<Integer> axis = new ArrayList<>();
    TFSqueezeLayer(Param param) {
        super(param);
        int len = 0;
        this.outputShape[len++] = 1;
        for (int i = 1; i < param.shape.length; i++) {
            if (1 == param.shape[i]) axis.add(i);
            else this.outputShape[len++] = this.outputShape[i];
        }
        this.outputShape = Arrays.copyOf(this.outputShape, len);
    }

    @Override
    void inlineCode(PrintStream out, Map<String, String> context) {
        out.printf(INLINE, context.get(KEY_INDENT), output, input, axis.toString(), name);
    }
}
