package org.ncsu.dnn.tf;

public class TFLayerFactory {
    TFLayer create(Param param) {
        switch (param.caffeLayer.type) {
            case Convolution: return new TFConvLayer(param);
            case Pooling: return new TFPoolLayer(param);
            case Concat: return new TFConcatLayer(param);
            case Scope: return new TFScopeLayer(param);
            case Group: return new TFLogitLayer(param);
            case Softmax: return new TFSoftmaxLayer(param);
            default: return null;
        }
    }
}
