package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.Arrays;

public abstract class TFLayer {
    String name;
    int[] outputShape;
    abstract String inlineCode();
    abstract void generateCode(PrintStream out, String input, String indent);

    TFLayer(CaffeLayer caffeLayer, int[] shape) {
        this.name = caffeLayer.getName();
        if (this.name.contains("/")) {
            this.name = this.name.substring(this.name.lastIndexOf('/') + 1);
        }
        this.outputShape = shape.clone();
    }

    @Override
    public String toString() {
        return this.name + " " + Arrays.toString(this.outputShape);
    }
}
