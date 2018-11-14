package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    void inlineCode(PrintStream out, Map<String, String> context) {
        out.printf(INLINE, context.get(KEY_INDENT),
                output, outputShape.length, branchOutputs.toString());
        String parentScope = super.getParentScope();
        context.put(KEY_SCOPE_PATH, parentScope);
        context.put(KEY_INDENT, getIndent(context, parentScope));
    }

    @Override
    String getParentScope() {
        return this.name;
    }
}
