package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.ncsu.dnn.tf.CodeGenerator.generateWithScope;

public class TFConcatLayer extends TFLayer {
    private static final String INLINE = CodeGenerator.SNIPPETS.getString("layer.concat.inline");
    private static final String BRANCH_PREFIX = "branch_";
    List<TFLayer> branchList;
    List<String> branchOutputs;

    TFConcatLayer(CaffeLayer caffeLayer, int[] shape) {
        super(caffeLayer, shape);
        this.branchList = new ArrayList<>();
        this.branchOutputs = new ArrayList<>();
        TFLayerFactory layerFactory = new TFLayerFactory();
        this.outputShape[0] = 0;
        int i = 0;
        for (CaffeLayer branch: caffeLayer.layerMap.values()) {
            if (branch.top != branch) continue;
            TFLayer layer = layerFactory.create(branch, shape);
            String branchOutput = BRANCH_PREFIX+(i++);
            branchOutputs.add(branchOutput);
            layer.setOutput(branchOutput);
            this.branchList.add(layer);
            this.outputShape[0] += layer.outputShape[0];
        }
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
