package org.ncsu.dnn.caffe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.ncsu.dnn.caffe.CaffeLayerType.*;

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
    private Map<String, CaffeLayer> layerMap;

    private CaffeModel(String name, String input) {
        this.name = name;
        this.input = input;
        this.inputShape = null;
        this.layerMap = new LinkedHashMap<>();
        this.layerMap.put(input, new CaffeLayer(input, Input));
    }

    public CaffeModel(ASTNode root) {
        this(root.getFirstValue(KEY_NAME), root.getFirstValue(KEY_INPUT));
        parseShape(root.getFirst(KEY_INPUT_SHAPE));
        parseLayers(root.get(KEY_LAYER));
    }

    public static CaffeModel createFromFile(File file) {
        Scanner scanner;
        try {
            scanner = new Scanner(file);
            Parser parser = new Parser(scanner.getTokenList().iterator());
            return new CaffeModel(parser.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            String top = node.getFirstValue(KEY_TOP);
            if (!this.layerMap.containsKey(top)) {
                addLayer(new CaffeLayer(top, Group));
            }
            addLayer(new CaffeLayer(node));
//            this.layerMap.put(node.getFirstValue(KEY_NAME), new Layer(node));
        }

        // Post-processing
        for (ASTNode node: nodeList) {
            String name = node.getFirstValue(KEY_NAME);
            CaffeLayer layer = this.layerMap.get(name);
            layer.top = this.layerMap.get(node.getFirstValue(KEY_TOP));
            layer.top.group.add(layer);
            layer.bottom = createLayerList(node.get(KEY_BOTTOM));
            for (CaffeLayer prev: layer.bottom) {
                if (prev != layer.top) prev.next.add(layer.top);
            }
        }
        for (Map.Entry<String, CaffeLayer> entry: layerMap.entrySet()) {
            CaffeLayer layer = entry.getValue();
            if (layer.type == Group && !layer.group.isEmpty()) {
                String key = entry.getKey();
                String newKey = layer.group.get(0).getRootName();
                if (key != newKey) {
                    layerMap.put(newKey, layer);
                }
            }
        }
    }

    private List<CaffeLayer> createLayerList(List<ASTNode> nodeList) {
        List<CaffeLayer> list = new ArrayList<>(nodeList.size());
        for (ASTNode node: nodeList)
            list.add(this.layerMap.get(node.val.getVal()));
        return list;
    }

    private void addLayer(CaffeLayer layer) {
        String name = layer.getName();
        int p = name.lastIndexOf('/');
        if (p > -1) {
            String parentName = name.substring(0, p);
            CaffeLayer parent = this.layerMap.get(parentName);
            if (null == parent) {
                parent = new CaffeLayer(parentName, Scope);
                addLayer(parent);
            }
            parent.layerMap.put(name, layer);
        }
        CaffeLayer existingLayer = this.layerMap.get(name);
        if (null != existingLayer) {
            if (existingLayer.getType() != Group && existingLayer.getType() != Scope) {
                throw new ParseException(EXCEPTION_DUPLICATE_NAME);
            }
            layer.layerMap.putAll(existingLayer.layerMap);
        }
        this.layerMap.put(name, layer);
    }

    public Map<String, CaffeLayer> getLayerMap() {
        return this.layerMap;
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

    public CaffeLayer getInputLayer() {
        return layerMap.get(input);
    }
}
