package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TFSqueezeLayer extends TFLayer {
    List<Integer> axis = new ArrayList<>();
    TFSqueezeLayer(CaffeLayer caffeLayer, int[] shape) {
        super(caffeLayer, shape);
        int len = 0;
        for (int i = 0; i < shape.length; i++) {
            if (1 == shape[i]) axis.add(i);
            else this.outputShape[len++] = this.outputShape[i];
        }
        this.outputShape = Arrays.copyOf(this.outputShape, len);
    }

    @Override
    String inlineCode() {
        return null;
    }

    @Override
    void generateCode(PrintStream out, String input, String indent) {

    }
}
