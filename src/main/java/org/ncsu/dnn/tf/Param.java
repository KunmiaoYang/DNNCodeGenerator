package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;

import java.util.HashMap;
import java.util.Map;

public class Param {
    TFModel model;
    CaffeLayer caffeLayer;
    Map<String, String> param;
    Map<String, CaffeLayer> layerMap;
    int[] shape;

    public Param(TFModel model) {
        this.param = new HashMap<>();
        this.model = model;
    }

    Param(Param input) {
        this.shape = input.shape.clone();
        this.param = new HashMap<>(input.param);
        this.layerMap = new HashMap<>(input.layerMap);
        this.model = input.model;
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
}
