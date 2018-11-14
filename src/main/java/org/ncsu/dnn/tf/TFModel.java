package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;
import org.ncsu.dnn.caffe.CaffeModel;

import java.io.PrintStream;
import java.util.*;

import static org.ncsu.dnn.tf.SimpleCodeGenerator.*;
import static org.ncsu.dnn.tf.TFConcatLayer.BRANCH_PREFIX;
import static org.ncsu.dnn.tf.TFLayer.KEY_INPUT;
import static org.ncsu.dnn.tf.TFLayer.KEY_NAME;
import static org.ncsu.dnn.tf.TFLayer.KEY_OUTPUT;

public class TFModel {
    private static final String MODEL_FUNCTION_SIGNATURE = SNIPPETS.getString("model.function.signature");
    private static final String MODEL_FUNCTION_RETURN = SNIPPETS.getString("model.function.return");
    static final String TF_VARIABLE_SCOPE = "tf.variable_scope";
    private static final String TF_VARIABLE_SCOPE_PARAMETERS = "scope, \"Model\", reuse=reuse";
    private static final String SLIM_ARG_SCOPE = "slim.arg_scope";
    private static final String SLIM_ARG_SCOPE_PARAMETERS = "default_arg_scope(is_training)";
    private static final String INIT_POINTS = "end_points = {}\r\n";
    private static final String NAME_INPUT = "inputs";
    private String name;
    Map<String, TFLayer> layers;
    private TFLayer lastLayer;
    int[] inputShape;
    private int[] outputShape;

    public TFModel() {
        this.layers = new LinkedHashMap<>();
    }
    public TFModel(CaffeModel caffeModel) {
        this();
        this.name = caffeModel.getName();
        this.inputShape = Arrays.copyOfRange(caffeModel.getInputShape(), 1, 4);
        parseCaffeModel(caffeModel);
        if (this.layers.isEmpty()) return;

        this.outputShape = lastLayer.outputShape;
    }
    private void parseCaffeModel(CaffeModel caffeModel) {
        Deque<Param> q = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Param param = new Param(this);
        param.layerMap = caffeModel.getLayerMap();
        param.shape = this.inputShape.clone();
        param.put(KEY_INPUT, NAME_INPUT);
        param.caffeLayer = caffeModel.getInputLayer();
        param.layerFactory = new TFLayerFactory(this);
        q.offerLast(param);
        while (!q.isEmpty()) {
            param = q.pollFirst();
            System.out.println(param.getName());
            if (null == param.caffeLayer) return;
            visited.add(param.getName());
            TFLayer layer = param.layerFactory.create(param);
            if (null != layer) {
                this.layers.put(layer.name, layer);
                param.shape = layer.outputShape;
                param.put(KEY_INPUT, layer.output);
                layer.name = param.getName();
                this.lastLayer = layer;
            }
            int nextCount = param.caffeLayer.next.size();
            if (nextCount == 1) {
                param.caffeLayer = param.caffeLayer.next.get(0);
                if (!layers.containsKey(param.caffeLayer.getName()))
                    q.offerLast(param);
            } else if (nextCount > 1){
                for (int i = 0; i < nextCount; i++) {
                    Param branchParam = new Param(param);
                    branchParam.caffeLayer = param.caffeLayer.next.get(i);
                    branchParam.put(KEY_OUTPUT, BRANCH_PREFIX + i);
                    if (!layers.containsKey(branchParam.caffeLayer.getName()))
                        q.offerLast(branchParam);
                }
            }
        }
    }

    void addLayer(Deque<String> q, Param param, Set<String> visited, TFLayerFactory layerFactory) {
        String layerName = q.pollFirst();
        param.caffeLayer = param.layerMap.get(layerName);
        if (null == param.caffeLayer) return;
        visited.add(layerName);
        TFLayer layer = layerFactory.create(param);
        if (null != layer) {
            this.layers.put(layer.name, layer);
            param.shape = layer.outputShape;
            param.put(KEY_INPUT, layer.output);
            layer.name = layerName;
            this.lastLayer = layer;
        }
        for (CaffeLayer next: param.caffeLayer.next) {
            if (visited.contains(next.getName())) return;
            q.add(next.getName());
        }
    }

    @Deprecated
    void addBranch(Deque<String> q, Param param) {
        int i, size = q.size();
        Param subParam = new Param(param);
        for (i = 0; i < size; i++) {
            String layerName = q.pollFirst();
            subParam.caffeLayer = param.layerMap.get(layerName);
            String nextName = subParam.caffeLayer.getName();
            String[] path = nextName.split("/");
            String branchName = path.length > 1? path[1]: BRANCH_PREFIX + i;
            subParam.put(KEY_NAME, branchName);
            TFScopeLayer branchLayer = new TFScopeLayer(subParam);
            this.layers.put(branchName, branchLayer);
        }
    }

    public void generateCode(PrintStream out, String indentation, String funcName) {
        out.print(indentation);
        out.printf(MODEL_FUNCTION_SIGNATURE, funcName, this.outputShape[0], this.name);
        indentation += INDENT_STRING;

        out.println();
        String insideIndent = generateWithScope(out, indentation, TF_VARIABLE_SCOPE, TF_VARIABLE_SCOPE_PARAMETERS);
        insideIndent = generateWithScope(out, insideIndent, SLIM_ARG_SCOPE, SLIM_ARG_SCOPE_PARAMETERS);

        out.println();
        out.println(insideIndent + INIT_POINTS);

        for (TFLayer layer: this.layers.values()) {
            layer.generateCode(out, insideIndent);
            out.println();
        }
        out.print(indentation);
        out.printf(MODEL_FUNCTION_RETURN, this.lastLayer.output, this.name);
    }
}
