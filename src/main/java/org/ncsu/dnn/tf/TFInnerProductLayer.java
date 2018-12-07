package org.ncsu.dnn.tf;

import org.ncsu.dnn.SimpleCodeGenerator;
import org.ncsu.dnn.caffe.CaffeLayer;

import java.io.PrintStream;
import java.util.Map;

import static org.ncsu.dnn.tf.TFConvLayer.OPTION_NO_ACTIVATION;
import static org.ncsu.dnn.tf.TFConvLayer.OPTION_NO_NORMALIZER;
import static org.ncsu.dnn.tf.TFModel.*;
import static org.ncsu.dnn.tf.TFSqueezeLayer.DEFAULT_SQUEEZE_NAME;

public class TFInnerProductLayer extends TFLayer {
    static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.innerProduct.inline");
    private int kernelHeight, kernelWidth;
    private boolean hasNormal, hasActivation;
    TFSqueezeLayer squeezeLayer;
    Param localParam;
    TFInnerProductLayer(Param param) {
        super(param);
        lastOuputNumber = this;
        this.kernelHeight = param.shape[SHAPE_H];
        this.kernelWidth = param.shape[SHAPE_W];
        this.outputShape[SHAPE_H] = 1;
        this.outputShape[SHAPE_W] = 1;
        this.outputShape[SHAPE_C] = Integer.parseInt(param.caffeLayer.paramMap.get("inner_product_param.num_output").getVal());
        this.hasNormal = false;
        this.hasActivation = false;

        param.shape = this.outputShape;

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
        this.localParam = new Param(param);
        this.squeezeLayer = null;
    }

    @Override
    void addSqueezeLayer() {
        localParam.put(KEY_NAME, DEFAULT_SQUEEZE_NAME);
        this.squeezeLayer = new TFSqueezeLayer(localParam);
        this.squeezeLayer.name = DEFAULT_SQUEEZE_NAME;
        this.outputShape = squeezeLayer.outputShape;
        this.output = squeezeLayer.output;
    }

    @Override
    void inlineCode(PrintStream out, Map<String, String> context) {
        String option = "";
        if (!hasActivation) option += OPTION_NO_ACTIVATION;
        if (!hasNormal) option += OPTION_NO_NORMALIZER;
        String outputClasses = this == lastOuputNumber ? "num_classes": String.valueOf(outputShape[SHAPE_C]);
        String indent = context.get(KEY_INDENT);
        out.printf(INLINE, indent, output, input,
                outputClasses, kernelHeight, kernelWidth, option);

        if (null != squeezeLayer) squeezeLayer.inlineCode(out,context);

        String parentScope = super.getParentScope();
        context.put(KEY_SCOPE_PATH, parentScope);
        context.put(KEY_INDENT, getIndent(context, parentScope));
    }

    @Override
    String getParentScope() {
        return this.name;
    }
}
