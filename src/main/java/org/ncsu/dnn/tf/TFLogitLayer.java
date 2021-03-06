package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.Map;

public class TFLogitLayer extends TFLayer {
    private static final String DEFAULT_OUTPUT = "logits";
    TFDropOutLayer dropOutLayer;
    TFConvLayer convLayer;
    TFSqueezeLayer squeezeLayer;

    TFLogitLayer(Param param) {
        super(param);
        Param subParam = new Param(param);
        for (CaffeLayer subLayer: param.caffeLayer.group) {
            subParam.caffeLayer = subLayer;
            switch (subLayer.type) {
                case Dropout:
                    this.dropOutLayer = new TFDropOutLayer(subParam);
                    this.output = dropOutLayer.output;
                    break;
                case Convolution:
                    subParam.put(KEY_OUTPUT, DEFAULT_OUTPUT);
                    this.convLayer = new TFConvLayer(subParam);
                    this.outputShape = convLayer.outputShape;
                    this.output = convLayer.output;
                    subParam.put(KEY_INPUT, convLayer.output);
                    subParam.shape = convLayer.outputShape;
                    break;
                case Reshape:
                    this.squeezeLayer = new TFSqueezeLayer(subParam);
                    this.squeezeLayer.name = TFSqueezeLayer.DEFAULT_SQUEEZE_NAME;
                    this.outputShape = squeezeLayer.outputShape;
                    this.output = squeezeLayer.output;
                    subParam.shape = squeezeLayer.outputShape;
                    break;
            }
        }
        this.name = convLayer.getParentScope();
    }

    @Override
    void inlineCode(PrintStream out, Map<String, String> context) {
        context.put(KEY_SCOPE_STRING, getRelativeScope(name, dropOutLayer.name));
        dropOutLayer.inlineCode(out, context);
        context.put(KEY_SCOPE_STRING, addQuotes(getRelativeScope(name, convLayer.name)));
        convLayer.inlineCode(out,context);
        squeezeLayer.inlineCode(out, context);

        context.put(KEY_SCOPE_PATH, super.getParentScope());
    }

    @Override
    String getParentScope() {
        return this.name;
    }
}
