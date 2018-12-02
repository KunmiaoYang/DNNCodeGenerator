package org.ncsu.dnn.tf;

import org.ncsu.dnn.SimpleCodeGenerator;
import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ncsu.dnn.tf.TFModel.KEY_INDENT;
import static org.ncsu.dnn.tf.TFModel.SHAPE_C;

public class TFConcatLayer extends TFLayer {
    private static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.concat.inline");
    static final String BRANCH_PREFIX = "branch_";
    List<TFLayer> branchList;
    List<String> branchOutputs;

    TFConcatLayer(Param param) {
        super(param);
        this.branchList = new ArrayList<>();
        this.branchOutputs = new ArrayList<>();
        this.outputShape[SHAPE_C] = 0;
        for (CaffeLayer branch: param.caffeLayer.bottom) {
            if (branch.top != branch) continue;
            TFLayer layer = param.model.layers.get(branch.getName());
            branchOutputs.add(layer.output);
            this.branchList.add(layer);
            this.outputShape[SHAPE_C] += layer.outputShape[SHAPE_C];
            layer.canPrune = false;
        }
        this.output = DEFAULT_OUTPUT;
        param.put(KEY_OUTPUT, DEFAULT_OUTPUT);
        param.param.remove(KEY_CONCAT_NAME);
        param.branch = -1;
        param.model.branchIndex = 0;
    }

    @Override
    void inlineCode(PrintStream out, Map<String, String> context) {
        out.printf(INLINE, context.get(KEY_INDENT),
                output, SHAPE_C, branchOutputs.toString());
        String parentScope = super.getParentScope();
        context.put(KEY_SCOPE_PATH, parentScope);
        context.put(KEY_INDENT, getIndent(context, parentScope));
    }

    @Override
    String getParentScope() {
        return this.name;
    }
}
