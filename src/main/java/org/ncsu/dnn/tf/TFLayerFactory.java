package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

public class TFLayerFactory {
    TFLayer create(CaffeLayer caffeLayer, int[] shape) {
        switch (caffeLayer.type) {
            case Convolution: return new TFConvLayer(caffeLayer, shape);
            case Pooling: return new TFPoolLayer(caffeLayer, shape);
            case Concat: return new TFConcatLayer(caffeLayer, shape);
            case Scope: return new TFScopeLayer(caffeLayer, shape);
            case Group: return new TFLogitLayer(caffeLayer, shape);
            case Softmax: return new TFSoftmaxLayer(caffeLayer, shape);
            default: return null;
        }
    }
}
