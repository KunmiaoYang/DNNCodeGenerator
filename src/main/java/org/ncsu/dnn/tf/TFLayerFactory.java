package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.util.Map;

public class TFLayerFactory {
    TFLayer create(CaffeLayer caffeLayer, int[] shape, Map<String, String> param) {
        switch (caffeLayer.type) {
            case Convolution: return new TFConvLayer(caffeLayer, shape, param);
            case Pooling: return new TFPoolLayer(caffeLayer, shape, param);
            case Concat: return new TFConcatLayer(caffeLayer, shape, param);
            case Scope: return new TFScopeLayer(caffeLayer, shape, param);
            case Group: return new TFLogitLayer(caffeLayer, shape, param);
            case Softmax: return new TFSoftmaxLayer(caffeLayer, shape, param);
            default: return null;
        }
    }
}
