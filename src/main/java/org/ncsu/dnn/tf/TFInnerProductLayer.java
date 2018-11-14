package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;

import static com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart.INLINE;
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
    String inlineCode(PrintStream out, String indent, String scope) {
        String option = "";
        if (!hasActivation) option += OPTION_NO_ACTIVATION;
        if (!hasNormal) option += OPTION_NO_NORMALIZER;
        String outputClasses = this == lastOuputNumber ? "num_classes": String.valueOf(outputShape[0]);
        out.printf(TFConvLayer.INLINE, indent, output, input, outputClasses, kernelHeight, kernelWidth,
                option, scope);
        squeezeLayer.inlineCode(out, indent, scope);
        return indent;
    }
}
