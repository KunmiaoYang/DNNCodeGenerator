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
            out.printf(SNIPPET_INIT, context.get(KEY_INDENT), this.name);
            changeScope(out, context, parentScope);
            context.put(KEY_SCOPE_STRING, END_POINT);
            this.inlineCode(out, context);
            out.printf(SNIPPET_ADD, context.get(KEY_INDENT), this.output);
        } else {
            changeScope(out, context, parentScope);
            context.put(KEY_SCOPE_STRING, this.name.contains("/")?
                    this.name.substring(this.name.lastIndexOf('/') + 1): this.name);
            this.inlineCode(out, context);
        }
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
            String[] scopes = target.substring(cur.length()).split("/");
            for (int i = 0; i < scopes.length; i++) {
                if (cur.equals("") && 0 == i) scopes[0] = END_POINT;
                else scopes[i] = "'" + scopes[i] + "'";
                TFModel.generateWithScope(out, indent, TF_VARIABLE_SCOPE, scopes[i]);
                indent += context.get(KEY_INDENT_BASE);
            }
        } else if (!cur.startsWith(target)) {
            int i = 0, j = 0;
            for (; i < cur.length() && j < target.length() && cur.charAt(i) == target.charAt(j); i++, j++) {}
            changeScope(out, context, cur.substring(0, i));
            return changeScope(out, context, target);
        }
        context.put(KEY_INDENT, getIndent(context, target));
        context.put(KEY_SCOPE_PATH, target);
        return target;
    }

    String getIndent(Map<String, String> context, String path) {
        String indent = context.get(KEY_INDENT_BASE);
        String indentString = context.get(KEY_INDENT_STRING);
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
