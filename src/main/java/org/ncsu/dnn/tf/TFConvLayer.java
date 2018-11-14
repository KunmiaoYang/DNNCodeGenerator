package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;
import org.ncsu.dnn.caffe.Token;

import java.io.PrintStream;

public class TFConvLayer extends TFLayer {
    private static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.conv.inline");
    private static final String OPTION_NO_ACTIVATION = ", activation_fn=None";
    private static final String OPTION_NO_NORMALIZER = ", normalizer_fn=None";
    private static TFConvLayer last;
    private int kernelHeight, kernelWidth;
    private int stride;
    private boolean hasNormal, hasActivation;

    TFConvLayer(Param param) {
        super(param);
        last = this;
        Token kernelSize = param.caffeLayer.paramMap.get("convolution_param.kernel_size");
        if (kernelSize != null) {
            this.kernelHeight = Integer.parseInt(kernelSize.getVal());
            this.kernelWidth = kernelHeight;
        } else {
            this.kernelHeight = Integer.parseInt(param.caffeLayer.paramMap.get("convolution_param.kernel_h").getVal());
            this.kernelWidth = Integer.parseInt(param.caffeLayer.paramMap.get("convolution_param.kernel_w").getVal());
        }
        this.stride = Integer.parseInt(param.caffeLayer.paramMap.get("convolution_param.stride").getVal());
        this.outputShape[0] = Integer.parseInt(param.caffeLayer.paramMap.get("convolution_param.num_output").getVal());
        this.outputShape[1] /= stride;
        this.outputShape[2] /= stride;
        this.hasNormal = false;
        this.hasActivation = false;
        for (CaffeLayer subLayer: param.caffeLayer.group) {
            switch (subLayer.type) {
                case BatchNorm:
                    hasNormal = true;
                    break;
                case ReLU:
                    hasActivation = true;
                    break;
            }
        }
    }

    @Override
    public String inlineCode(PrintStream out, String indent, String scope) {
        String option = stride > 1? ", stride=" + stride: "";
        if (!hasActivation) option += OPTION_NO_ACTIVATION;
        if (!hasNormal) option += OPTION_NO_NORMALIZER;
        String outputClasses = this == last? "num_classes": String.valueOf(outputShape[0]);
        out.printf(INLINE, indent, output, input, outputClasses, kernelHeight, kernelWidth,
                option, scope);
        return indent;
    }
}
