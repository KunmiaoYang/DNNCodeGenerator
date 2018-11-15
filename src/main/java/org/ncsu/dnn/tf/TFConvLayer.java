package org.ncsu.dnn.tf;

import org.ncsu.dnn.SimpleCodeGenerator;
import org.ncsu.dnn.caffe.CaffeLayer;
import org.ncsu.dnn.caffe.Token;

import java.io.PrintStream;
import java.util.Map;

import static org.ncsu.dnn.tf.TFModel.KEY_INDENT;
import static org.ncsu.dnn.tf.TFModel.KEY_MULTIPLEX;

public class TFConvLayer extends TFLayer {
    static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.conv.inline");
    static final String SELECT_DEPTH = SimpleCodeGenerator.SNIPPETS.getString("layer.conv.selectdepth");
    static final String OPTION_NO_ACTIVATION = ", activation_fn=None";
    static final String OPTION_NO_NORMALIZER = ", normalizer_fn=None";
    private int kernelHeight, kernelWidth;
    private int stride;
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
    public void inlineCode(PrintStream out, Map<String, String> context) {
        String option = stride > 1? ", stride=" + stride: "";
        if (!hasActivation) option += OPTION_NO_ACTIVATION;
        if (!hasNormal) option += OPTION_NO_NORMALIZER;
        String outputClasses = String.valueOf(outputShape[0]);
        if (canPrune && context.containsKey(KEY_MULTIPLEX))
            outputClasses = String.format(SELECT_DEPTH, outputShape[0]);
        if (this == lastOuputNumber) outputClasses = "num_classes";
        out.printf(INLINE, context.get(KEY_INDENT), output, input, outputClasses, kernelHeight, kernelWidth,
                option, context.get(KEY_SCOPE_STRING));
    }
}
