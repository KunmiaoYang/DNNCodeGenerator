package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.Map;

import static org.ncsu.dnn.tf.TFConvLayer.*;

public class TFInnerProductLayer extends TFLayer {
    private int kernelHeight, kernelWidth;
    private boolean hasNormal, hasActivation;
    TFSqueezeLayer squeezeLayer;
    TFInnerProductLayer(Param param) {
        super(param);
        lastOuputNumber = this;
        this.kernelHeight = param.shape[1];
        this.kernelWidth = param.shape[2];
        this.outputShape[0] = Integer.parseInt(param.caffeLayer.paramMap.get("inner_product_param.num_output").getVal());
        this.hasNormal = false;
        this.hasActivation = false;
        for (CaffeLayer subLayer: param.caffeLayer.group) {
            switch (subLayer.type) {
                case BatchNorm:
                    this.hasNormal = true;
                    break;
                case ReLU:
                    this.hasActivation = true;
                    break;
            }
        }
        this.squeezeLayer = new TFSqueezeLayer(param);
        this.squeezeLayer.name = TFSqueezeLayer.DEFAULT_SQUEEZE_NAME;
        this.outputShape = squeezeLayer.outputShape;
        this.output = squeezeLayer.output;
    }

    @Override
    void inlineCode(PrintStream out, Map<String, String> context) {
        String option = "";
        if (!hasActivation) option += OPTION_NO_ACTIVATION;
        if (!hasNormal) option += OPTION_NO_NORMALIZER;
        String outputClasses = this == lastOuputNumber ? "num_classes": String.valueOf(outputShape[0]);
        out.printf(TFConvLayer.INLINE, context.get(KEY_INDENT),
                output, input, outputClasses, kernelHeight, kernelWidth,
                option, context.get(KEY_SCOPE_STRING));
        squeezeLayer.inlineCode(out,context);
    }
}
