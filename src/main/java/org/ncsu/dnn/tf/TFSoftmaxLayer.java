package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;

public class TFSoftmaxLayer extends TFLayer {
    TFSoftmaxLayer(CaffeLayer caffeLayer, int height, int width) {
        super(caffeLayer, height, width);
    }

    @Override
    String inlineCode() {
        return null;
    }

    @Override
    void generateCode(PrintStream out, String input, String indent) {

    }
}
