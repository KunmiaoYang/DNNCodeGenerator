package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;

public class TFLogitLayer extends TFLayer {
    TFDropOutLayer dropOutLayer;
    TFConvLayer convLayer;
    TFSqueezeLayer squeezeLayer;

    TFLogitLayer(CaffeLayer caffeLayer, int height, int width) {
        super(caffeLayer, height, width);
        for (CaffeLayer subLayer: caffeLayer.group) {
            switch (subLayer.type) {
                case Dropout:
                    this.dropOutLayer = new TFDropOutLayer(subLayer, height, width);
                    break;
                case Convolution:
                    this.convLayer = new TFConvLayer(subLayer, height, width);
                    break;
                case Reshape:
                    this.squeezeLayer = new TFSqueezeLayer(subLayer, height, width);
                    this.squeezeLayer.name = "SpatialSqueeze";
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
