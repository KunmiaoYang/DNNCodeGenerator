package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TFSqueezeLayer extends TFLayer {
    List<Integer> axis = new ArrayList<>();
    TFSqueezeLayer(CaffeLayer caffeLayer, int height, int width) {
        super(caffeLayer, height, width);
        if (1 == height) axis.add(1);
        if (1 == width) axis.add(2);
    }

    @Override
    String inlineCode() {
        return null;
    }

    @Override
    void generateCode(PrintStream out, String input, String indent) {

    }
}
