package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TFConcatLayer extends TFLayer {
    List<TFLayer> branchList;

    TFConcatLayer(CaffeLayer caffeLayer, int[] shape) {
        super(caffeLayer, shape);
        this.branchList = new ArrayList<>();
        TFLayerFactory layerFactory = new TFLayerFactory();
        this.outputShape[0] = 0;
        for (CaffeLayer branch: caffeLayer.layerMap.values()) {
            if (branch.top != branch) continue;
            TFLayer layer = layerFactory.create(branch, shape);
            this.branchList.add(layer);
            this.outputShape[0] += layer.outputShape[0];
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
