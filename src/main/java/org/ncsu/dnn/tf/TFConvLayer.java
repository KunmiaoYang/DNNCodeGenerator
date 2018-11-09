package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.Map;

public class TFConvLayer extends TFLayer {
    private static final String INLINE = CodeGenerator.SNIPPETS.getString("layer.conv.inline");
    private static final String OPTION_NO_ACTIVATION = ", activation_fn=None";
    private static final String OPTION_NO_NORMALIZER = ", normalizer_fn=None";
    int numOutput;
    int kernelHeight, kernelWidth;
    int stride;
    boolean hasNormal, hasActivation;

    public TFConvLayer(CaffeLayer caffeLayer, int[] shape, Map<String, String> param) {
        super(caffeLayer, shape, param);
        this.numOutput = Integer.parseInt(caffeLayer.paramMap.get("convolution_param.num_output").getVal());
        this.kernelHeight = Integer.parseInt(caffeLayer.paramMap.get("convolution_param.kernel_size").getVal());
        this.kernelWidth = kernelHeight;
        this.stride = Integer.parseInt(caffeLayer.paramMap.get("convolution_param.stride").getVal());
        this.outputShape[0] = numOutput;
        this.outputShape[1] /= stride;
        this.outputShape[2] /= stride;
        this.hasNormal = false;
        this.hasActivation = false;
        for (CaffeLayer subLayer: caffeLayer.group) {
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
        out.printf(INLINE, indent, output, input, outputShape[0], kernelHeight, kernelWidth,
                option, scope);
        return indent;
    }
}
