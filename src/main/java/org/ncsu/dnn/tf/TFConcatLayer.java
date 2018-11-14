package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.ncsu.dnn.tf.SimpleCodeGenerator.generateWithScope;

public class TFConcatLayer extends TFLayer {
    private static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.concat.inline");
    static final String BRANCH_PREFIX = "branch_";
    List<TFLayer> branchList;
    List<String> branchOutputs;

    TFConcatLayer(Param param) {
        super(param);
        this.branchList = new ArrayList<>();
        this.branchOutputs = new ArrayList<>();
        this.outputShape[0] = 0;
        for (CaffeLayer branch: param.caffeLayer.bottom) {
            if (branch.top != branch) continue;
            TFLayer layer = param.model.layers.get(branch.getName());
            branchOutputs.add(layer.output);
            this.branchList.add(layer);
            this.outputShape[0] += layer.outputShape[0];
        }
        this.output = DEFAULT_OUTPUT;
        param.put(KEY_OUTPUT, DEFAULT_OUTPUT);
        param.model.branchIndex = 0;
    }

    @Override
    String inlineCode(PrintStream out, String indent, String scope) {
        String inside = generateWithScope(out, indent, TFModel.TF_VARIABLE_SCOPE, scope);
//        for (TFLayer branch: branchList) {
//            branch.inlineCode(out, inside, "'" + branch.name + "'");
//        }
        out.printf(INLINE, inside, output, outputShape.length, branchOutputs.toString());
        return indent;
    }

}
