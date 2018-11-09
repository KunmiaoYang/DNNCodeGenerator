package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

public class TFLayerFactory {
    TFLayer create(CaffeLayer caffeLayer, int height, int width) {
        switch (caffeLayer.type) {
            case Convolution: return new TFConvLayer(caffeLayer, height, width);
            case Pooling: return new TFPoolLayer(caffeLayer, height, width);
            case Concat: return new TFConcatLayer(caffeLayer, height, width);
            case Scope: return new TFScopeLayer(caffeLayer, height, width);
            case Group: return new TFLogitLayer(caffeLayer, height, width);
            case Softmax: return new TFSoftmaxLayer(caffeLayer, height, width);
            default: return null;
        }
    }
}
