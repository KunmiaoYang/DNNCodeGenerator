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
                    break;
                case Reshape:
                    this.squeezeLayer = new TFSqueezeLayer(subParam);
                    this.squeezeLayer.name = TFSqueezeLayer.DEFAULT_SQUEEZE_NAME;
                    this.outputShape = squeezeLayer.outputShape;
                    this.output = squeezeLayer.output;
                    break;
            }
        }
    }

    String getRelativeScope(String parent, String scope) {
        if (parent.equals("")) return scope;
        return scope.substring(parent.length() + 1);
    }

    @Override
    void inlineCode(PrintStream out, Map<String, String> context) {
        context.put(KEY_SCOPE_STRING, getRelativeScope(name, dropOutLayer.name));
        dropOutLayer.inlineCode(out, context);
        context.put(KEY_SCOPE_STRING, addQuotes(getRelativeScope(name, convLayer.name)));
        convLayer.inlineCode(out,context);
        squeezeLayer.inlineCode(out, context);

        context.put(KEY_SCOPE_PATH, super.getParaentScope());
    }

    @Override
    String getParaentScope() {
        return this.name;
    }
}
