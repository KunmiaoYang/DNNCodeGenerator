package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;

import static org.ncsu.dnn.tf.SimpleCodeGenerator.generateWithScope;

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
                    param.put(KEY_OUTPUT, DEFAULT_OUTPUT);
                    this.convLayer = new TFConvLayer(subParam);
                    this.outputShape = convLayer.outputShape;
                    this.output = convLayer.output;
                    param.put(KEY_INPUT, convLayer.output);
                    break;
                case Reshape:
                    this.squeezeLayer = new TFSqueezeLayer(subParam);
                    this.squeezeLayer.name = "SpatialSqueeze";
                    this.outputShape = squeezeLayer.outputShape;
                    this.output = squeezeLayer.output;
                    break;
            }
        }
    }

    @Override
    String inlineCode(PrintStream out, String indent, String scope) {
        String inside = generateWithScope(out, indent, TFModel.TF_VARIABLE_SCOPE, scope);
        dropOutLayer.inlineCode(out, inside, "'" + dropOutLayer.name + "'");
        convLayer.inlineCode(out, inside, "'" + convLayer.name + "'");
        squeezeLayer.inlineCode(out, inside, scope);
        return inside;
    }
}
