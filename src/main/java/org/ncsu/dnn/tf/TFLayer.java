package org.ncsu.dnn.tf;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;

import static org.ncsu.dnn.tf.TFModel.KEY_INDENT_STRING;
import static org.ncsu.dnn.tf.TFModel.TF_VARIABLE_SCOPE;

public abstract class TFLayer {
    static final String KEY_NAME = "name";
    static final String KEY_INPUT = "input";
    static final String KEY_OUTPUT = "output";
    static final String KEY_INDENT = "indent";
    static final String KEY_INDENT_BASE = "indentBase";
    static final String KEY_SCOPE_STRING = "scopeString";
    static final String KEY_SCOPE_PATH = "scopePath";
    private static final String SNIPPET_INIT = SimpleCodeGenerator.SNIPPETS.getString("layer.snippet.init");
    private static final String SNIPPET_ADD = SimpleCodeGenerator.SNIPPETS.getString("layer.snippet.add");
    static final String END_POINT = "end_point";
    private static final String DEFAULT_INPUT = "net";
    static final String DEFAULT_OUTPUT = "net";
    static TFLayer lastOuputNumber;
    String name;
    protected String input, output;
    int[] outputShape;
    abstract void inlineCode(PrintStream out, Map<String, String> context);

    TFLayer(Param param) {
        this.input = param.getOrDefault(KEY_INPUT, DEFAULT_INPUT);
        this.output = param.getOrDefault(KEY_OUTPUT, DEFAULT_OUTPUT);
        this.name = param.getOrDefault(KEY_NAME, param.caffeLayer.getName());
//        if (this.name.contains("/")) {
//            this.name = this.name.substring(this.name.lastIndexOf('/') + 1);
//        }
        this.outputShape = param.shape.clone();
        param.param.remove(KEY_NAME); // In case the name is incorrectly passed to other layer
    }

    void generateCode(PrintStream out, Map<String, String> context) {
        String parentScope = getParaentScope();
        if (context.get(KEY_SCOPE_PATH).equals("")) {
            context.put(KEY_INDENT, context.get(KEY_INDENT_BASE));
            out.printf(SNIPPET_INIT, context.get(KEY_INDENT_BASE), this.name.contains("/")?
                    this.name.substring(0, this.name.indexOf('/')): this.name);
            changeScope(out, context, parentScope);
            context.put(KEY_SCOPE_STRING, END_POINT);
        } else {
            changeScope(out, context, parentScope);
            context.put(KEY_SCOPE_STRING, this.name.substring(this.name.lastIndexOf('/') + 1));
        }
        this.inlineCode(out, context);
        if (context.get(KEY_SCOPE_PATH).equals(""))
            out.printf(SNIPPET_ADD, context.get(KEY_INDENT_BASE), this.output);
    }

    String getParaentScope() {
        return this.name.contains("/")? this.name.substring(0, this.name.lastIndexOf('/')): "";
    }

    String changeScope(PrintStream out, Map<String, String> context, String target) {
        String cur = context.get(KEY_SCOPE_PATH);
        if (target.equals(cur)) {
            return target;
        } else if (target.startsWith(cur)) {
            String indent = context.get(KEY_INDENT);
            String[] pathTarget = target.split("/");
            int i = "".equals(cur)? 0: cur.split("/").length;
            for (; i < pathTarget.length; i++) {
                if (cur.equals("") && 0 == i) pathTarget[0] = END_POINT;
                else pathTarget[i] = "'" + pathTarget[i] + "'";
                TFModel.generateWithScope(out, indent, TF_VARIABLE_SCOPE, pathTarget[i]);
                indent += context.get(KEY_INDENT_STRING);
            }
        } else if (!cur.startsWith(target)) {
            String[] pathCur = cur.split("/");
            String[] pathTarget = target.split("/");
            StringBuilder pathCommon = new StringBuilder();
            for (int i = 0; i < pathCur.length && i < pathTarget.length && pathCur[i].equals(pathTarget[i]); i++) {
                pathCommon.append('/').append(pathCur[i]);
            }
            changeScope(out, context, pathCommon.substring(1));
            return changeScope(out, context, target);
        }
        context.put(KEY_INDENT, getIndent(context, target));
        context.put(KEY_SCOPE_PATH, target);
        return target;
    }

    String getIndent(Map<String, String> context, String path) {
        String indent = context.get(KEY_INDENT_BASE);
        String indentString = context.get(KEY_INDENT_STRING);
        if (path.equals("")) return indent;
        else indent += indentString;
        for (char c: path.toCharArray()) {
            if (c == '/') indent += indentString;
        }
        return indent;
    }

    @Override
    public String toString() {
        return this.name + " " + Arrays.toString(this.outputShape);
    }
}
