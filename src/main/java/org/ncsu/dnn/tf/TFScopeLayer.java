package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

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
    String inlineCode() {
        return null;
    }

    @Override
    void generateCode(PrintStream out, String input, String indent) {

    }
}
