package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;

public abstract class TFLayer {
    String name;
    int outputHeight, outputWidth;
    abstract String inlineCode();
    abstract void generateCode(PrintStream out, String input, String indent);

    TFLayer(CaffeLayer caffeLayer, int height, int width) {
        this.name = caffeLayer.getName();
        if (this.name.contains("/")) {
            this.name = this.name.substring(this.name.lastIndexOf('/') + 1);
        }
        this.outputHeight = height;
        this.outputWidth = width;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
