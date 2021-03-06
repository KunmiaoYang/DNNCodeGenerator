package org.ncsu.dnn.tf;

import org.ncsu.dnn.SimpleCodeGenerator;
import org.ncsu.dnn.caffe.CaffeLayer;
import org.ncsu.dnn.caffe.Token;

import java.io.PrintStream;
import java.util.Map;

import static org.ncsu.dnn.tf.TFModel.*;

public class TFConvLayer extends TFLayer {
    static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.conv.inline");
    static final String SELECT_DEPTH = SimpleCodeGenerator.SNIPPETS.getString("layer.conv.selectdepth");
    static final String OPTION_NO_ACTIVATION = ", activation_fn=None";
    static final String OPTION_NO_NORMALIZER = ", normalizer_fn=None";
    private int kernelHeight, kernelWidth;
    private int stride, padHeight, padWidth;
    private boolean hasNormal, hasActivation;

    TFConvLayer(Param param) {
        super(param);
        lastOuputNumber = this;
        Token kernelSize = param.caffeLayer.paramMap.get("convolution_param.kernel_size");
        if (kernelSize != null) {
            this.kernelHeight = Integer.parseInt(kernelSize.getVal());
            this.kernelWidth = kernelHeight;
        } else {
            this.kernelHeight = Integer.parseInt(param.caffeLayer.paramMap.get("convolution_param.kernel_h").getVal());
            this.kernelWidth = Integer.parseInt(param.caffeLayer.paramMap.get("convolution_param.kernel_w").getVal());
        }

        // Solve the problem that no stride parameter found in caffe model, the stride is 1 by default
        Token strideToken = param.caffeLayer.paramMap.get("convolution_param.stride");
        this.stride = strideToken == null ? 1: Integer.parseInt(strideToken.getVal());

        // Parse padding
        this.padHeight = 0;
        this.padWidth = 0;
        if (param.caffeLayer.paramMap.containsKey("convolution_param.pad")) {
            this.padHeight = Integer.parseInt(param.caffeLayer.paramMap.get("convolution_param.pad").getVal());
            this.padWidth = this.padHeight;
        }
        if (param.caffeLayer.paramMap.containsKey("convolution_param.pad_h")) {
            this.padHeight = Integer.parseInt(param.caffeLayer.paramMap.get("convolution_param.pad_h").getVal());
        }
        if (param.caffeLayer.paramMap.containsKey("convolution_param.pad_w")) {
            this.padWidth = Integer.parseInt(param.caffeLayer.paramMap.get("convolution_param.pad_w").getVal());
        }

        this.outputShape[SHAPE_C] = Integer.parseInt(param.caffeLayer.paramMap.get("convolution_param.num_output").getVal());
        this.outputShape[SHAPE_H] = calcSize(outputShape[SHAPE_H], this.kernelHeight, this.stride, this.padHeight);
        this.outputShape[SHAPE_W] = calcSize(outputShape[SHAPE_W], this.kernelWidth, this.stride, this.padWidth);
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
    public void inlineCode(PrintStream out, Map<String, String> context) {
        String option = stride > 1? ", stride=" + stride: "";
        if (!hasActivation) option += OPTION_NO_ACTIVATION;
        if (!hasNormal) option += OPTION_NO_NORMALIZER;
        String outputClasses = String.valueOf(outputShape[SHAPE_C]);
        if (canPrune && context.containsKey(KEY_MULTIPLEX))
            outputClasses = String.format(SELECT_DEPTH, outputShape[SHAPE_C]);
        if (this == lastOuputNumber) outputClasses = "num_classes";
        out.printf(INLINE, context.get(KEY_INDENT), output, input, outputClasses, kernelHeight, kernelWidth,
                option, context.get(KEY_SCOPE_STRING));
    }

    private int calcSize(int inputSize, int kernel, int stride, int pad) {
        return (inputSize + 2*pad - kernel)/stride + 1;
    }
}
