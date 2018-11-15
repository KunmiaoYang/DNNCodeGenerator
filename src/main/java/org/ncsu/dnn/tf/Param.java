package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.ncsu.dnn.tf.TFLayer.KEY_NAME;

public class Param {
    TFModel model;
    TFLayerFactory layerFactory;
    CaffeLayer caffeLayer;
    Map<String, String> param;
    Map<String, CaffeLayer> layerMap;
    Set<String> visited;
    int[] shape;
    int branch;

    public Param(TFModel model) {
        this.param = new HashMap<>();
        this.visited = new HashSet<>();
        this.model = model;
        this.branch = -1;
    }

    Param(Param input) {
        this.shape = input.shape.clone();
        this.param = new HashMap<>(input.param);
        this.layerMap = new HashMap<>(input.layerMap);
        this.model = input.model;
        this.layerFactory = input.layerFactory;
        this.visited = input.visited;
        this.branch = input.branch;
    }

    String get(String key) {
        return param.get(key);
    }
    String getOrDefault(String key, String val) {
        return param.getOrDefault(key, val);
    }
    String put(String key, String val) {
        return param.put(key, val);
    }
    String getName() {
        return param.getOrDefault(KEY_NAME, caffeLayer.getName());
    }
}
