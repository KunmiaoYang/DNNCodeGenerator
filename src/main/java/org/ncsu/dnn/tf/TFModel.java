package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;
import org.ncsu.dnn.caffe.CaffeModel;

import java.io.PrintStream;
import java.util.*;

import static org.ncsu.dnn.tf.CodeGenerator.*;

public class TFModel {
    public static final String MODEL_FUNCTION_SIGNATURE = SNIPPETS.getString("model.function.signature");
    public static final String TF_VARIABLE_SCOPE = "tf.variable_scope";
    public static final String TF_VARIABLE_SCOPE_PARAMETERS = "scope, \"Model\", reuse=reuse";
    public static final String SLIM_ARG_SCOPE = "slim.arg_scope";
    public static final String SLIM_ARG_SCOPE_PARAMETERS = "default_arg_scope(is_training)";
    public static final String INIT_POINTS = "end_points = {}\r\n";
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

        TFLayer layer = layerList.get(0);
        layer.setInput("inputs");
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

    public void generateCode(PrintStream out, String indentation) {
        out.print(indentation);
        out.printf(MODEL_FUNCTION_SIGNATURE, this.outputShape[0], this.name);
        indentation += INDENT_STRING;

        out.println();
        String insideIndent = generateWithScope(out, indentation, TF_VARIABLE_SCOPE, TF_VARIABLE_SCOPE_PARAMETERS);
        insideIndent = generateWithScope(out, insideIndent, SLIM_ARG_SCOPE, SLIM_ARG_SCOPE_PARAMETERS);

        out.println();
        out.println(insideIndent + INIT_POINTS);

        for (TFLayer layer: this.layerList) {
            layer.generateCode(out, insideIndent);
            out.println();
        }
    }
}
