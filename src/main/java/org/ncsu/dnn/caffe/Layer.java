package org.ncsu.dnn.caffe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.ncsu.dnn.caffe.LayerType.*;

public class Layer {
    private String name;
    private LayerType type;
    List<Layer> bottom;
    List<Layer> top;
    Map<String, Layer> layerMap;

    public Layer() {
        this.layerMap = new LinkedHashMap<>();
    }

    public Layer(String name, LayerType type) {
        this();
        this.name = name;
        this.type = type;
    }

    public Layer(ASTNode node) {
        this();
        this.name = node.getFirstValue(CaffeModel.KEY_NAME);
        this.type = getType(node.getFirstValue(CaffeModel.KEY_TYPE));
    }

    private LayerType getType(String val) {
        if (val.equalsIgnoreCase("Convolution")) return Convolution;
        if (val.equalsIgnoreCase("BatchNorm")) return BatchNorm;
        if (val.equalsIgnoreCase("Scale")) return Scale;
        if (val.equalsIgnoreCase("ReLU")) return ReLU;
        if (val.equalsIgnoreCase("Pooling")) return Pooling;
        if (val.equalsIgnoreCase("Concat")) return Concat;
        if (val.equalsIgnoreCase("Dropout")) return Dropout;
        if (val.equalsIgnoreCase("Reshape")) return Reshape;
        if (val.equalsIgnoreCase("Softmax")) return Softmax;
        return Invalid;
    }

    public String getName() {
        return name;
    }

    LayerType getType() {
        return type;
    }
}
