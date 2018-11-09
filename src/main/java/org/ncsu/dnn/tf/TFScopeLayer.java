package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.ncsu.dnn.tf.CodeGenerator.*;

public class TFScopeLayer extends TFLayer {
    List<TFLayer> layerList;

    TFScopeLayer(CaffeLayer caffeLayer, int[] shape) {
        super(caffeLayer, shape);
        this.layerList = new ArrayList<>();
        TFLayerFactory layerFactory = new TFLayerFactory();
        for (CaffeLayer branch: caffeLayer.layerMap.values()) {
            if (branch.top != branch) continue;
            TFLayer layer = layerFactory.create(branch, this.outputShape);
            this.layerList.add(layer);
            System.arraycopy(layer.outputShape, 0, this.outputShape, 0, 3);
        }
    }

    @Override
    String inlineCode(PrintStream out, String indent, String scope) {
        String inside = generateWithScope(out, indent, TFModel.TF_VARIABLE_SCOPE, scope);
        for (TFLayer layer: layerList) {
            layer.inlineCode(out, inside, "'" + layer.name + "'");
        }
        return indent;
    }

    @Override
    public void setOutput(String output) {
        super.setOutput(output);
        String input = this.input;
        for (TFLayer layer: layerList) {
            layer.setInput(input);
            layer.setOutput(this.output);
            input = layer.output;
        }
    }
}
