package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;

public class TFConvLayer extends TFLayer {
    private static final String INLINE = CodeGenerator.SNIPPETS.getString("layer.conv.inline");
    int numOutput;
    int kernelHeight, kernelWidth;
    int stride;
    boolean hasNormal, hasActivation;

    public TFConvLayer(CaffeLayer caffeLayer, int[] shape) {
        super(caffeLayer, shape);
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
        out.printf(INLINE, indent, output, input, outputShape[0], kernelHeight, kernelWidth,
                stride > 1? ", stride=" + stride: "", scope);
        return indent;
    }
}
