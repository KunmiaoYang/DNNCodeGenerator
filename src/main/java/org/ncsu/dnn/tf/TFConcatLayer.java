package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ncsu.dnn.tf.SimpleCodeGenerator.generateWithScope;

public class TFConcatLayer extends TFLayer {
    private static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.concat.inline");
    private static final String BRANCH_PREFIX = "branch_";
    List<TFLayer> branchList;
    List<String> branchOutputs;

    TFConcatLayer(CaffeLayer caffeLayer, int[] shape, Map<String, String> param) {
        super(caffeLayer, shape, param);
        this.branchList = new ArrayList<>();
        this.branchOutputs = new ArrayList<>();
        TFLayerFactory layerFactory = new TFLayerFactory();
        this.outputShape[0] = 0;
        int i = 0;
        for (CaffeLayer branch: caffeLayer.layerMap.values()) {
            if (branch.top != branch) continue;
            String branchOutput = BRANCH_PREFIX+(i++);
            Map<String, String> branchParam = new HashMap<>(param);
            branchParam.put(KEY_OUTPUT, branchOutput);
            TFLayer layer = layerFactory.create(branch, shape, branchParam);
            branchOutputs.add(branchOutput);
//            layer.setOutput(branchOutput);
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
