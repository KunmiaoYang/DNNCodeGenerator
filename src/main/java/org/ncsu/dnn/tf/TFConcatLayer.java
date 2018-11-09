package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TFConcatLayer extends TFLayer {
    List<TFLayer> branchList;

    TFConcatLayer(CaffeLayer caffeLayer, int height, int width) {
        super(caffeLayer, height, width);
        this.branchList = new ArrayList<>();
        TFLayerFactory layerFactory = new TFLayerFactory();
        for (CaffeLayer branch: caffeLayer.layerMap.values()) {
            if (branch.top != branch) continue;
            this.branchList.add(layerFactory.create(branch, height, width));
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
