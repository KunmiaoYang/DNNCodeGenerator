package org.ncsu.dnn.tf;

public class TFLayerFactory {
    TFModel model;

    public TFLayerFactory(TFModel model) {
        this.model = model;
    }

    TFLayer create(Param param) {
        TFLayer layer = this.model.layers.get(param.getName());
        if (null != layer) return layer;
        switch (param.caffeLayer.type) {
            case Concat: return new TFConcatLayer(param);
            case Convolution: return new TFConvLayer(param);
            case Dropout: return new TFDropOutLayer(param);
            case InnerProduct: return new TFInnerProductLayer(param);
            case Input: return new TFInputLayer(param);
            case Group: return new TFLogitLayer(param);
            case Pooling: return new TFPoolLayer(param);
            case Scope: return new TFScopeLayer(param);
            case Softmax: return new TFSoftmaxLayer(param);
            default: return null;
        }
    }
}
