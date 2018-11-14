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
        int i = 0;
        Param branchParam = new Param(param);
//        for (CaffeLayer branch: param.caffeLayer.layerMap.values()) {
        for (CaffeLayer branch: param.caffeLayer.bottom) {
            if (branch.top != branch) continue;
            branchParam.caffeLayer = branch;
            String branchOutput = BRANCH_PREFIX+(i++);
            branchParam.put(KEY_OUTPUT, branchOutput);
            TFLayer layer = param.layerFactory.create(branchParam);
            branchOutputs.add(branchOutput);
//            layer.setOutput(branchOutput);
            this.branchList.add(layer);
            this.outputShape[0] += layer.outputShape[0];
            param.model.layers.remove(layer.name);
        }
        this.output = DEFAULT_OUTPUT;
    }

    @Override
    String inlineCode(PrintStream out, String indent, String scope) {
        String inside = generateWithScope(out, indent, TFModel.TF_VARIABLE_SCOPE, scope);
        for (TFLayer branch: branchList) {
            branch.inlineCode(out, inside, "'" + branch.name + "'");
        }
        out.printf(INLINE, inside, output, outputShape.length, branchOutputs.toString());
        return indent;
    }

}
