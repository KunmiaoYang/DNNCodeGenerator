package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ncsu.dnn.tf.CodeGenerator.*;

public class TFScopeLayer extends TFLayer {
    List<TFLayer> layerList;

    TFScopeLayer(CaffeLayer caffeLayer, int[] shape, Map<String, String> param) {
        super(caffeLayer, shape, param);
        this.layerList = new ArrayList<>();
        TFLayerFactory layerFactory = new TFLayerFactory();
        for (CaffeLayer branch: caffeLayer.layerMap.values()) {
            if (branch.top != branch) continue;
            TFLayer layer = layerFactory.create(branch, this.outputShape, param);
            this.layerList.add(layer);
            System.arraycopy(layer.outputShape, 0, this.outputShape, 0, 3);
            param.put(KEY_INPUT, layer.output);
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
}
