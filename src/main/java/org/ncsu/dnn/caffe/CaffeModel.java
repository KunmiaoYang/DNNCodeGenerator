package org.ncsu.dnn.caffe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.ncsu.dnn.caffe.LayerType.*;

public class CaffeModel {
    static final String KEY_NAME = "name";
    static final String KEY_TYPE = "type";
    private static final String KEY_INPUT = "input";
    private static final String KEY_INPUT_SHAPE = "input_shape";
    private static final String KEY_DIM = "dim";
    private static final String KEY_BOTTOM = "bottom";
    private static final String KEY_TOP = "top";
    private static final String KEY_LAYER = "layer";
    private static final String EXCEPTION_DUPLICATE_NAME = "Layer name duplicated!";

    private String name;
    private String input;
    private int[] inputShape;
    private Map<String, Layer> layerMap;

    private CaffeModel(String name, String input) {
        this.name = name;
        this.input = input;
        this.inputShape = null;
        this.layerMap = new LinkedHashMap<>();
        this.layerMap.put(input, new Layer(input, Input));
    }

    public CaffeModel(ASTNode root) {
        this(root.getFirstValue(KEY_NAME), root.getFirstValue(KEY_INPUT));
        parseShape(root.getFirst(KEY_INPUT_SHAPE));
        parseLayers(root.get(KEY_LAYER));
    }

    private void parseShape(ASTNode inputShapeNode) {
        if (null != inputShapeNode) {
            List<ASTNode> dims = inputShapeNode.get(KEY_DIM);
            this.inputShape = new int[dims.size()];
            int i = 0;
            for (ASTNode dim: dims) {
                this.inputShape[i++] = Integer.parseInt(dim.val.getVal());
            }
        }
    }

    private void parseLayers(List<ASTNode> nodeList) {
        if (null == nodeList) return;

        // Initialize the layers and store them in the map
        for (ASTNode node: nodeList) {
            addLayer(new Layer(node));
//            this.layerMap.put(node.getFirstValue(KEY_NAME), new Layer(node));
        }

        // Connect layers
        for (ASTNode node: nodeList) {
            String name = node.getFirstValue(KEY_NAME);
            Layer layer = this.layerMap.get(name);
            layer.bottom = createLayerList(node.get(KEY_BOTTOM));
            layer.top = createLayerList(node.get(KEY_TOP));
        }
    }

    private List<Layer> createLayerList(List<ASTNode> nodeList) {
        List<Layer> list = new ArrayList<>(nodeList.size());
        for (ASTNode node: nodeList)
            list.add(this.layerMap.get(node.val.getVal()));
        return list;
    }

    private void addLayer(Layer layer) {
        String name = layer.getName();
        int p = name.lastIndexOf('/');
        if (p > -1) {
            String parentName = name.substring(0, p);
            Layer parent = this.layerMap.get(parentName);
            if (null == parent) {
                parent = new Layer(parentName, Branch);
                addLayer(parent);
            }
            parent.layerMap.put(name, layer);
        }
        Layer existingLayer = this.layerMap.get(name);
        if (null != existingLayer) {
            if (existingLayer.getType() != Branch) throw new ParseException(EXCEPTION_DUPLICATE_NAME);
            layer.layerMap.putAll(existingLayer.layerMap);
        }
        this.layerMap.put(name, layer);
    }

    public String getName() {
        return name;
    }

    public String getInput() {
        return input;
    }

    public int[] getInputShape() {
        return inputShape;
    }
}
