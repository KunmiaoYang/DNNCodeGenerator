package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.ncsu.dnn.caffe.CaffeLayerType.Concat;
import static org.ncsu.dnn.tf.SimpleCodeGenerator.generateWithScope;

public class TFScopeLayer extends TFLayer {
    List<TFLayer> layerList;

    TFScopeLayer(Param param) {
        super(param);
        this.layerList = new ArrayList<>();
        Param subParam = new Param(param);
        for (CaffeLayer branch: param.caffeLayer.layerMap.values()) {
            if (branch.top != branch) continue;
            subParam.caffeLayer = branch;
            TFLayer layer = param.layerFactory.create(subParam);
            this.layerList.add(layer);
            System.arraycopy(layer.outputShape, 0, this.outputShape, 0, 3);
            param.put(KEY_INPUT, layer.output);
        }
    }

//    TFScopeLayer (Param param) {
//        super(param);
//        this.layerList = new ArrayList<>();
//        TFLayerFactory layerFactory = new TFLayerFactory();
//        while (null != param.caffeLayer && Concat != param.caffeLayer.type) {
//            TFLayer layer = layerFactory.create(param);
//            if (null == layer) break;
//            this.layerList.add(layer);
//            param.shape = layer.outputShape;
//            param.put(KEY_INPUT, layer.output);
//            if (param.caffeLayer.next.size() > 1) layerList.add(TFModel.addBranch(param));
//
//        }
//    }

    @Override
    String inlineCode(PrintStream out, String indent, String scope) {
        String inside = generateWithScope(out, indent, TFModel.TF_VARIABLE_SCOPE, scope);
        for (TFLayer layer: layerList) {
            layer.inlineCode(out, inside, "'" + layer.name + "'");
        }
        return indent;
    }
}
