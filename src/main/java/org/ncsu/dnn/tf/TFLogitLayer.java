package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;

public class TFLogitLayer extends TFLayer {
    TFDropOutLayer dropOutLayer;
    TFConvLayer convLayer;
    TFSqueezeLayer squeezeLayer;

    TFLogitLayer(CaffeLayer caffeLayer, int[] shape) {
        super(caffeLayer, shape);
        for (CaffeLayer subLayer: caffeLayer.group) {
            switch (subLayer.type) {
                case Dropout:
                    this.dropOutLayer = new TFDropOutLayer(subLayer, this.outputShape);
                    break;
                case Convolution:
                    this.convLayer = new TFConvLayer(subLayer, this.outputShape);
                    this.outputShape = convLayer.outputShape;
                    break;
                case Reshape:
                    this.squeezeLayer = new TFSqueezeLayer(subLayer, this.outputShape);
                    this.squeezeLayer.name = "SpatialSqueeze";
                    this.outputShape = squeezeLayer.outputShape;
                    break;
            }
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
