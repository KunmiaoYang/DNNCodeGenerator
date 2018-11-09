package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;
import org.ncsu.dnn.caffe.ParseException;
import org.ncsu.dnn.caffe.Token;

import java.io.PrintStream;

public class TFPoolLayer extends TFLayer {
    private static final String INLINE = CodeGenerator.SNIPPETS.getString("layer.pool.inline");
    public static final int TYPE_MAX = 0;
    public static final int TYPE_AVE = 1;
    int type;
    int kernelHeight, kernelWidth;
    int stride;

    public TFPoolLayer(CaffeLayer caffeLayer, int[] shape) {
        super(caffeLayer, shape);
        Token token = caffeLayer.paramMap.get("pooling_param.kernel_size");
        if (null != token) {
            this.kernelHeight = Integer.parseInt(token.getVal());
            this.kernelWidth = kernelHeight;
        } else if ((token = caffeLayer.paramMap.get("pooling_param.global_pooling")) != null && token.getVal().equals("true")) {
            this.kernelHeight = shape[1];
            this.kernelWidth = shape[2];
        } else {
            throw new ParseException("Invalid pooling layer");
        }
        token = caffeLayer.paramMap.get("pooling_param.stride");
        this.stride = 1;
        if (null != token) {
            this.stride = Integer.parseInt(token.getVal());
            this.outputShape[1] /= stride;
            this.outputShape[2] /= stride;
        }
        String poolType = caffeLayer.paramMap.get("pooling_param.pool").getVal();
        if ("MAX".equals(poolType)) {
            this.type = TYPE_MAX;
        } else if ("AVE".equals(poolType)) {
            this.type = TYPE_AVE;
            this.outputShape[1] = 1;
            this.outputShape[2] = 1;
        }
    }

    @Override
    public String inlineCode(PrintStream out, String indent, String scope) {
        String poolType = "";
        switch (this.type) {
            case TYPE_MAX: poolType = "max"; break;
            case TYPE_AVE: poolType = "avg"; break;
        }
        out.printf(INLINE, indent, output, poolType, input, kernelHeight, kernelWidth,
                stride > 1? ", stride=" + stride: "", scope);
        return indent;
    }
}
