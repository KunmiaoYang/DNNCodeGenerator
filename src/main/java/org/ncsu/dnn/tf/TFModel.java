package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;
import org.ncsu.dnn.caffe.CaffeModel;

import java.util.*;

public class TFModel {
    int numClasses;
    String name;
    String isTraining;
    String reuse;
    List<TFLayer> layerList;
    int inputHeight;
    int inputWidth;

    public TFModel() {
        this.layerList = new ArrayList<>();
    }
    public TFModel(CaffeModel caffeModel) {
        this();
        this.name = caffeModel.getName();
        this.isTraining = "True";
        this.reuse = "None";
        int[] inputShape = caffeModel.getInputShape();
        this.inputHeight = inputShape[2];
        this.inputWidth = inputShape[3];
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
        int h = inputHeight, w = inputWidth;
        while (!q.isEmpty()) {
            caffeLayer = q.pollFirst();
            visited.add(caffeLayer);
            TFLayer layer = layerFactory.create(caffeLayer, h, w);
            if (null != layer) {
                this.layerList.add(layer);
                h = layer.outputHeight;
                w = layer.outputWidth;
            }
            Set<CaffeLayer> nextLayers = new HashSet<>();
            for (CaffeLayer next: caffeLayer.next) {
                CaffeLayer nextRoot = layerMap.get(next.top.getRootName());
                if (visited.contains(nextRoot)) continue;
                nextLayers.add(nextRoot);
            }
            q.addAll(nextLayers);
        }
    }
}
