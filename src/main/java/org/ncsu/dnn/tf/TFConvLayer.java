package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;

public class TFConvLayer extends TFLayer {
    int numOutput;
    int kernelHeight, kernelWidth;
    int stride;
    boolean hasNormal, hasActivation;

    public TFConvLayer(CaffeLayer caffeLayer, int height, int width) {
        super(caffeLayer, height, width);
        this.numOutput = Integer.parseInt(caffeLayer.paramMap.get("convolution_param.num_output").getVal());
        this.kernelHeight = Integer.parseInt(caffeLayer.paramMap.get("convolution_param.kernel_size").getVal());
        this.kernelWidth = kernelHeight;
        this.stride = Integer.parseInt(caffeLayer.paramMap.get("convolution_param.stride").getVal());
        this.outputHeight /= stride;
        this.outputWidth /= stride;
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
    public String inlineCode() {
        return null;
    }

    @Override
    public void generateCode(PrintStream out, String input, String indent) {

    }
}
