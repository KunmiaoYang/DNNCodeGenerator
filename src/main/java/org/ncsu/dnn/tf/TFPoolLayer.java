package org.ncsu.dnn.tf;

import org.ncsu.dnn.SimpleCodeGenerator;
import org.ncsu.dnn.caffe.CaffeLayer;
import org.ncsu.dnn.caffe.ParseException;
import org.ncsu.dnn.caffe.Token;

import java.io.PrintStream;
import java.util.Map;

import static org.ncsu.dnn.caffe.CaffeLayerType.Dropout;
import static org.ncsu.dnn.tf.TFModel.KEY_INDENT;

public class TFPoolLayer extends TFLayer {
    private static final String INLINE = SimpleCodeGenerator.SNIPPETS.getString("layer.pool.inline");
    public static final int TYPE_MAX = 0;
    public static final int TYPE_AVE = 1;
    int type;
    int kernelHeight, kernelWidth;
    int stride;
    TFDropOutLayer dropOutLayer;

    public TFPoolLayer(Param param) {
        super(param);
        Token token = param.caffeLayer.paramMap.get("pooling_param.kernel_size");
        if (null != token) {
            this.kernelHeight = Integer.parseInt(token.getVal());
            this.kernelWidth = kernelHeight;
        } else if ((token = param.caffeLayer.paramMap.get("pooling_param.global_pooling")) != null && token.getVal().equals("true")) {
            this.kernelHeight = param.shape[1];
            this.kernelWidth = param.shape[2];
        } else {
            throw new ParseException("Invalid pooling layer");
        }
        token = param.caffeLayer.paramMap.get("pooling_param.stride");
        this.stride = 1;
        if (null != token) {
            this.stride = Integer.parseInt(token.getVal());
            this.outputShape[1] /= stride;
            this.outputShape[2] /= stride;
        }
        String poolType = param.caffeLayer.paramMap.get("pooling_param.pool").getVal();
        if ("MAX".equals(poolType)) {
            this.type = TYPE_MAX;
        } else if ("AVE".equals(poolType)) {
            this.type = TYPE_AVE;
            this.outputShape[1] = 1;
            this.outputShape[2] = 1;
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
}
