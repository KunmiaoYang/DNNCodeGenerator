package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;
import org.ncsu.dnn.caffe.CaffeModel;

import java.io.PrintStream;
import java.util.*;

import static org.ncsu.dnn.tf.CodeGenerator.*;
import static org.ncsu.dnn.tf.TFLayer.KEY_INPUT;
import static org.ncsu.dnn.tf.TFLayer.KEY_NAME;

public class TFModel {
    public static final String MODEL_FUNCTION_SIGNATURE = SNIPPETS.getString("model.function.signature");
    public static final String TF_VARIABLE_SCOPE = "tf.variable_scope";
    public static final String TF_VARIABLE_SCOPE_PARAMETERS = "scope, \"Model\", reuse=reuse";
    public static final String SLIM_ARG_SCOPE = "slim.arg_scope";
    public static final String SLIM_ARG_SCOPE_PARAMETERS = "default_arg_scope(is_training)";
    public static final String INIT_POINTS = "end_points = {}\r\n";
    static final String NAME_INPUT = "inputs";
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
    }
    private void parseCaffeModel(CaffeModel caffeModel) {
        Map<String, CaffeLayer> layerMap = caffeModel.getLayerMap();
        CaffeLayer caffeLayer;
        Deque<String> q = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        q.offerLast(caffeModel.getInput());
        TFLayerFactory layerFactory = new TFLayerFactory();
        int[] shape = this.inputShape.clone();
        Map<String, String> param = new HashMap<>();
        param.put(KEY_INPUT, NAME_INPUT);
        while (!q.isEmpty()) {
            String layerName = q.pollFirst();
            caffeLayer = layerMap.get(layerName);
            if (null == caffeLayer) continue;
            visited.add(layerName);
            TFLayer layer = layerFactory.create(caffeLayer, shape, param);
            if (null != layer) {
                this.layerList.add(layer);
                shape = layer.outputShape;
                param.put(KEY_INPUT, layer.output);
                layer.name = layerName;
            }
            Set<String> nextLayers = new HashSet<>();
            for (CaffeLayer next: caffeLayer.next) {
                String nextRoot = next.getRootName();
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
