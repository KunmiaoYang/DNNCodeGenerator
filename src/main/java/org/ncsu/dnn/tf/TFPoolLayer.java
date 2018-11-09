package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;
import org.ncsu.dnn.caffe.ParseException;
import org.ncsu.dnn.caffe.Token;

import java.io.PrintStream;

public class TFPoolLayer extends TFLayer {
    public static final int TYPE_MAX = 0;
    public static final int TYPE_AVE = 1;
    int type;
    int kernelHeight, kernelWidth;
    int stride;

    public TFPoolLayer(CaffeLayer caffeLayer, int height, int width) {
        super(caffeLayer, height, width);
        Token token = caffeLayer.paramMap.get("pooling_param.kernel_size");
        if (null != token) {
            this.kernelHeight = Integer.parseInt(token.getVal());
            this.kernelWidth = kernelHeight;
        } else if ((token = caffeLayer.paramMap.get("pooling_param.global_pooling")) != null && token.getVal().equals("true")) {
            this.kernelHeight = height;
            this.kernelWidth = width;
        } else {
            throw new ParseException("Invalid pooling layer");
        }
        token = caffeLayer.paramMap.get("pooling_param.stride");
        this.stride = 1;
        if (null != token) {
            this.stride = Integer.parseInt(token.getVal());
            this.outputHeight /= stride;
            this.outputWidth /= stride;
        }
        String poolType = caffeLayer.paramMap.get("pooling_param.pool").getVal();
        if ("MAX".equals(poolType)) {
            this.type = TYPE_MAX;
        } else if ("AVE".equals(poolType)) {
            this.type = TYPE_AVE;
            this.outputHeight = 1;
            this.outputWidth = 1;
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
