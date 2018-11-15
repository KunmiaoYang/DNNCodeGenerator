package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeLayer;
import org.ncsu.dnn.caffe.CaffeModel;

import java.io.PrintStream;
import java.util.*;

import static org.ncsu.dnn.SimpleCodeGenerator.SNIPPETS;
import static org.ncsu.dnn.tf.TFConcatLayer.BRANCH_PREFIX;
import static org.ncsu.dnn.tf.TFLayer.*;

public class TFModel {
    static final String INDENT_STRING = "  ";   // Indent with 2 spaces
    static final String BRANCH_SCOPE_PREFIX = "Branch_";
    private static final String MODEL_FUNCTION_SIGNATURE = SNIPPETS.getString("model.function.signature");
    private static final String MODEL_FUNCTION_RETURN = SNIPPETS.getString("model.function.return");
    private static final String PY_WITH_SCOPE = SNIPPETS.getString("py.with.scope");
    static final String TF_VARIABLE_SCOPE = "tf.variable_scope";
    private static final String TF_VARIABLE_SCOPE_PARAMETERS = "scope, \"Model\", reuse=reuse";
    private static final String SLIM_ARG_SCOPE = "slim.arg_scope";
    private static final String SLIM_ARG_SCOPE_PARAMETERS = "default_arg_scope(is_training)";
    private static final String INIT_POINTS = "end_points = {}\r\n";
    private static final String NAME_INPUT = "inputs";
    public static final String KEY_MULTIPLEX = "multiplex";
    public static final String KEY_FUNC = "func";
    public static final String KEY_INDENT = "indent";
    static final String KEY_INDENT_STRING = "indentString";
    private String name;
    Map<String, TFLayer> layers;
    private TFLayer lastLayer;
    public int[] inputShape;
    private int[] outputShape;
    int branchIndex;

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
        this.branchIndex = 0;
        Deque<Param> q = new ArrayDeque<>();
        Param param = new Param(this);
        param.layerMap = caffeModel.getLayerMap();
        param.shape = this.inputShape.clone();
        param.put(KEY_INPUT, NAME_INPUT);
        param.caffeLayer = caffeModel.getInputLayer();
        param.layerFactory = new TFLayerFactory(this);
        q.push(param);
        while (!q.isEmpty()) {
            param = q.pop();
//            System.out.println(param.getName());
            if (null == param.caffeLayer) return;
            if (param.visited.contains(param.caffeLayer.getName()) || !checkBottom(param)) continue;
            param.visited.add(param.caffeLayer.getName());
            TFLayer layer = param.layerFactory.create(param);
            if (null != layer) {
                this.layers.put(param.caffeLayer.getName(), layer);
                param.shape = layer.outputShape;
                param.put(KEY_INPUT, layer.output);
//                layer.name = param.getName();
                this.lastLayer = layer;
            }

            int nextCount = param.caffeLayer.next.size();
            if (nextCount == 1) {
                param.caffeLayer = param.caffeLayer.next.get(0);
                if (layers.containsKey(param.caffeLayer.getName())) continue;
                q.push(param);
            } else if (nextCount > 1){
                // Use a temporary stack to reverse the next layers before push them into q
                // If we directly push them reversely into q, the branch index would be reversed,
                // so I need to give them branch index before I reverse them.Therefore I need to
                // store them after give them index and then reverse them. Thus I used this stack.
                Deque<Param> stack = new ArrayDeque<>(nextCount);
                for (CaffeLayer nextLayer: param.caffeLayer.next) {
                    CaffeLayer nextConcat = nextLayer.getNextConcat();
                    Param branchParam = new Param(param);
                    if (null != nextConcat) {
                        if (branchIndex == 0) branchParam.put(KEY_START_BRANCH, "True");
                        branchParam.put(KEY_CONCAT_NAME, nextConcat.getName());
                    }
                    branchParam.caffeLayer = nextLayer;
                    branchParam.branch = branchIndex;
                    branchParam.put(KEY_OUTPUT, BRANCH_PREFIX + branchIndex++);
                    if (layers.containsKey(branchParam.caffeLayer.getName())) continue;
                    stack.push(branchParam);
                }
                while (!stack.isEmpty()) q.push(stack.pop());
            }
        }
    }

    private boolean checkBottom(Param param) {
        if (null == param.caffeLayer.bottom) return true;
        for (CaffeLayer prev: param.caffeLayer.bottom) {
            if (!layers.containsKey(prev.getName())) return false;
        }
        return true;
    }

    public void generateFuncDef(PrintStream out, Map<String, String> context) {
        String indent = context.get(KEY_INDENT);
        String funcName = context.get(KEY_FUNC);
        out.print(indent);
        out.printf(MODEL_FUNCTION_SIGNATURE, funcName, this.outputShape[0], this.name);
        context.put(KEY_INDENT, indent + INDENT_STRING);
    }

    public void generateCode(PrintStream out, Map<String, String> context) {
        String indentation = context.get(KEY_INDENT);

        out.println();
        String insideIndent = generateWithScope(out, indentation, TF_VARIABLE_SCOPE, TF_VARIABLE_SCOPE_PARAMETERS);
        insideIndent = generateWithScope(out, insideIndent, SLIM_ARG_SCOPE, SLIM_ARG_SCOPE_PARAMETERS);

        out.println();
        out.println(insideIndent + INIT_POINTS);

        context.put(KEY_INDENT_BASE, insideIndent);
        context.put(KEY_INDENT_STRING, INDENT_STRING);
        context.put(KEY_SCOPE_PATH, "");
        for (TFLayer layer: this.layers.values()) {
            layer.generateCode(out, context);
//            out.println();
        }
        out.print(indentation);
        out.printf(MODEL_FUNCTION_RETURN, this.lastLayer.output, this.name);
    }

    static String generateWithScope(PrintStream out, String indentation, String func, String scope) {
        out.printf(PY_WITH_SCOPE, indentation, func, scope);
        return indentation + INDENT_STRING;
    }

}
