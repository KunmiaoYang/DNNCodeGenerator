package org.ncsu.dnn.caffe;

import java.util.*;

import static org.ncsu.dnn.caffe.CaffeLayerType.*;

public class CaffeLayer {
    private String name;
    public CaffeLayerType type;
    public List<CaffeLayer> next;
    List<CaffeLayer> bottom;
    public List<CaffeLayer> group;
    public CaffeLayer top;
    public Map<String, CaffeLayer> layerMap;
    public Map<String, Token> paramMap;

    public CaffeLayer() {
        this.next = new ArrayList<>();
        this.group = new ArrayList<>();
        this.layerMap = new LinkedHashMap<>();
    }

    public CaffeLayer(String name, CaffeLayerType type) {
        this();
        this.name = name;
        this.type = type;
        this.top = this;
        this.paramMap = null;
    }

    public CaffeLayer(ASTNode node) {
        this();
        this.name = node.getFirstValue(CaffeModel.KEY_NAME);
        this.type = getType(node.getFirstValue(CaffeModel.KEY_TYPE));
        this.paramMap = new HashMap<>();
        parseParameters(node);
    }

    private CaffeLayerType getType(String val) {
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

    private void parseParameters(ASTNode root) {
        for (Map.Entry<String, List<ASTNode>> entry: root.children.entrySet()) {
            String prefix = entry.getKey() + '.';
            for (ASTNode node: entry.getValue()) {
                if (null == node.val) {
                    parseParamNode(node, prefix);
                }
            }
        }
    }

    private void parseParamNode(ASTNode node, String prefix) {
        for (Map.Entry<String, List<ASTNode>> entry: node.children.entrySet()) {
            assert !entry.getValue().isEmpty(): "ASTNode list in children value should not be empty!";
            ASTNode child = entry.getValue().get(0);
            if (null != child.val) {
                this.paramMap.put(prefix + entry.getKey(), child.val);
            } else {
                parseParamNode(child, prefix + entry.getKey() + '.');
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getRootName() {
        return name.split("/")[0];
    }

    CaffeLayerType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Layer{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
