package org.ncsu.dnn.tf;

import org.ncsu.dnn.SimpleCodeGenerator;
import org.ncsu.dnn.caffe.CaffeLayer;
import org.ncsu.dnn.caffe.ParseException;
import org.ncsu.dnn.caffe.Token;

import java.io.PrintStream;
import java.util.Map;

import static org.ncsu.dnn.caffe.CaffeLayerType.Dropout;
import static org.ncsu.dnn.tf.TFModel.KEY_INDENT;
import static org.ncsu.dnn.tf.TFModel.SHAPE_H;
import static org.ncsu.dnn.tf.TFModel.SHAPE_W;

public class TFPoolLayer extends TFLayer {
    private static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.pool.inline");
    public static final int TYPE_MAX = 0;
    public static final int TYPE_AVE = 1;
    int type;
    int kernelHeight, kernelWidth;
    int stride, padHeight, padWidth;
    TFDropOutLayer dropOutLayer;

    public TFPoolLayer(Param param) {
        super(param);
        this.stride = 1;
        Token token = param.caffeLayer.paramMap.get("pooling_param.kernel_size");
        if (null != token) {
            this.kernelHeight = Integer.parseInt(token.getVal());
            this.kernelWidth = kernelHeight;

            // Parse stride
            token = param.caffeLayer.paramMap.get("pooling_param.stride");
            if (null != token) this.stride = Integer.parseInt(token.getVal());

            // Parse padding
            this.padHeight = 0;
            this.padWidth = 0;
            if (param.caffeLayer.paramMap.containsKey("pooling_param.pad")) {
                this.padHeight = Integer.parseInt(param.caffeLayer.paramMap.get("pooling_param.pad").getVal());
                this.padWidth = this.padHeight;
            }
            if (param.caffeLayer.paramMap.containsKey("pooling_param.pad_h")) {
                this.padHeight = Integer.parseInt(param.caffeLayer.paramMap.get("pooling_param.pad_h").getVal());
            }
            if (param.caffeLayer.paramMap.containsKey("pooling_param.pad_w")) {
                this.padWidth = Integer.parseInt(param.caffeLayer.paramMap.get("pooling_param.pad_w").getVal());
            }

            // Calculate output shape
            this.outputShape[SHAPE_H] = calcSize(outputShape[SHAPE_H], this.kernelHeight, this.stride, this.padHeight);
            this.outputShape[SHAPE_W] = calcSize(outputShape[SHAPE_W], this.kernelWidth, this.stride, this.padWidth);
        } else if ((token = param.caffeLayer.paramMap.get("pooling_param.global_pooling")) != null && token.getVal().equals("true")) {
            this.kernelHeight = param.shape[SHAPE_H];
            this.kernelWidth = param.shape[SHAPE_W];
            this.outputShape[SHAPE_H] = 1;
            this.outputShape[SHAPE_W] = 1;
        } else {
            throw new ParseException("Invalid pooling layer");
        }

        String poolType = param.caffeLayer.paramMap.get("pooling_param.pool").getVal();
        if ("MAX".equals(poolType)) {
            this.type = TYPE_MAX;
        } else if ("AVE".equals(poolType)) {
            this.type = TYPE_AVE;
        }
        this.dropOutLayer = null;
        for (CaffeLayer caffeLayer: param.caffeLayer.group) {
            if (caffeLayer.type == Dropout) {
                Param subParam = new Param(param);
                subParam.caffeLayer = caffeLayer;
                this.dropOutLayer = (TFDropOutLayer) param.layerFactory.create(subParam);
            }
        }
    }

    @Override
    public void inlineCode(PrintStream out, Map<String, String> context) {
        String poolType = "";
        switch (this.type) {
            case TYPE_MAX: poolType = "max"; break;
            case TYPE_AVE: poolType = "avg"; break;
        }
        out.printf(INLINE, context.get(KEY_INDENT), output, poolType, input,
                kernelHeight, kernelWidth, ", stride=" + stride,
                context.get(KEY_SCOPE_STRING));
        if (null != dropOutLayer) {
            context.put(KEY_SCOPE_STRING, dropOutLayer.name);
            dropOutLayer.inlineCode(out, context);
        }
    }

    private int calcSize(int inputSize, int kernel, int stride, int pad) {
        return (int) (Math.ceil((inputSize + 2.0*pad - kernel)/stride) + 1);
    }
}
