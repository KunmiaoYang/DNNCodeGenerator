package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;

public class TFDropOutLayer extends TFLayer {
    double ratio;
    TFDropOutLayer(CaffeLayer caffeLayer, int height, int width) {
        super(caffeLayer, height, width);
        this.ratio = 1.0 - Double.parseDouble(caffeLayer.paramMap.get("dropout_param.dropout_ratio").getVal());
    }

    @Override
    String inlineCode() {
        return null;
    }

    @Override
    void generateCode(PrintStream out, String input, String indent) {

    }
}
