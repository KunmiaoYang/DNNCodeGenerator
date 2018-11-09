package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;
import org.ncsu.dnn.caffe.CaffeModel;

import java.util.*;

public class TFModel {
    String name;
    String isTraining;
    String reuse;
    List<TFLayer> layerList;
    int[] inputShape;
    int[] outputShape;

    public TFModel() {
        this.layerList = new ArrayList<>();
    }
    public TFModel(CaffeModel caffeModel) {
        this();
        this.name = caffeModel.getName();
        this.isTraining = "True";
        this.reuse = "None";
        this.inputShape = Arrays.copyOfRange(caffeModel.getInputShape(), 1, 4);
        parseCaffeModel(caffeModel);
    }
    private void parseCaffeModel(CaffeModel caffeModel) {
        Map<String, CaffeLayer> layerMap = caffeModel.getLayerMap();
        CaffeLayer caffeLayer = layerMap.get(caffeModel.getInput());
        if (null == caffeLayer) return;
        Deque<CaffeLayer> q = new ArrayDeque<>();
        Set<CaffeLayer> visited = new HashSet<>();
        q.offerLast(caffeLayer);
        TFLayerFactory layerFactory = new TFLayerFactory();
        int[] shape = this.inputShape.clone();
        while (!q.isEmpty()) {
            caffeLayer = q.pollFirst();
            visited.add(caffeLayer);
            TFLayer layer = layerFactory.create(caffeLayer, shape);
            if (null != layer) {
                this.layerList.add(layer);
                shape = layer.outputShape;
            }
            Set<CaffeLayer> nextLayers = new HashSet<>();
            for (CaffeLayer next: caffeLayer.next) {
                CaffeLayer nextRoot = layerMap.get(next.top.getRootName());
                if (visited.contains(nextRoot)) continue;
                nextLayers.add(nextRoot);
            }
            q.addAll(nextLayers);
        }
        this.outputShape = shape;
    }
}
