package org.ncsu.dnn.tf;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;

import static org.ncsu.dnn.SimpleCodeGenerator.SNIPPETS;
import static org.ncsu.dnn.tf.TFModel.*;

public abstract class TFLayer {
    static final String KEY_NAME = "name";
    static final String KEY_INPUT = "input";
    static final String KEY_OUTPUT = "output";
    static final String KEY_INDENT_BASE = "indentBase";
    static final String KEY_SCOPE_STRING = "scopeString";
    static final String KEY_SCOPE_PATH = "scopePath";
    static final String KEY_CONCAT_NAME = "concatName";

    private static final String SNIPPET_INIT = SNIPPETS.getString("layer.snippet.init");
    private static final String SNIPPET_ADD = SNIPPETS.getString("layer.snippet.add");
    private static final String MULTIPLEX_SELECT_INPUT = SNIPPETS.getString("multiplex.selectinput");
    static final String END_POINT = "end_point";
    private static final String DEFAULT_INPUT = "net";
    static final String DEFAULT_OUTPUT = "net";
    static TFLayer lastOuputNumber;
    String name, concatName;
    protected String input, output;
    int[] outputShape;
    boolean canPrune;
    abstract void inlineCode(PrintStream out, Map<String, String> context);

    TFLayer(Param param) {
        this.input = param.getOrDefault(KEY_INPUT, DEFAULT_INPUT);
        this.output = param.getOrDefault(KEY_OUTPUT, DEFAULT_OUTPUT);
        this.name = param.getOrDefault(KEY_NAME, param.caffeLayer.getName());
        this.outputShape = param.shape.clone();
        this.concatName = param.get(KEY_CONCAT_NAME);
        this.canPrune = false;

        // In case these parameters are incorrectly passed to other layer
        param.param.remove(KEY_NAME);
        param.param.remove(KEY_CONCAT_NAME);
    }

    void generateCode(PrintStream out, Map<String, String> context) {
        String parentScope = getParentScope();
        if (context.get(KEY_SCOPE_PATH).equals("")) {
            context.put(KEY_INDENT, context.get(KEY_INDENT_BASE));
            if (null != this.concatName && context.containsKey(KEY_MULTIPLEX)) {
                context.put(KEY_CONCAT_NAME, this.concatName);
                generateMultiplex(out, context);
            } else {
                out.printf(SNIPPET_INIT, context.get(KEY_INDENT_BASE), getRootScope());
            }
        }

        changeScope(out, context, parentScope);

        if (context.get(KEY_SCOPE_PATH).equals("")) {
            context.put(KEY_SCOPE_STRING, END_POINT);
        } else {
            context.put(KEY_SCOPE_STRING, addQuotes(this.name.substring(this.name.lastIndexOf('/') + 1)));
        }
        this.inlineCode(out, context);

        if (context.get(KEY_SCOPE_PATH).equals(""))
            out.printf(SNIPPET_ADD, context.get(KEY_INDENT), this.output);
    }

    String getRootScope() {
        return this.name.contains("/")? this.name.substring(0, this.name.indexOf('/')): this.name;
    }

    String getParentScope() {
        return this.name.contains("/")? this.name.substring(0, this.name.lastIndexOf('/')): "";
    }

    String getRelativeScope(String parent, String scope) {
        if (parent.equals("")) return scope;
        return scope.substring(parent.length() + 1);
    }

    String changeScope(PrintStream out, Map<String, String> context, String target) {
        String cur = context.get(KEY_SCOPE_PATH);
        if (target.equals(cur)) {
            return target;
        } else if (target.startsWith(cur)) {
            String indent = context.get(KEY_INDENT);
            String[] pathTarget = getRelativeScope(cur, target).split("/");
            for (int i = 0; i < pathTarget.length; i++) {
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

    String addQuotes(String s) {
        return "'" + s + "'";
    }

    public void generateMultiplex(PrintStream out, Map<String, String> context) {
        String indent = context.get(KEY_INDENT);
        String concatName = context.get(KEY_CONCAT_NAME);
        if (concatName.equals(this.getRootScope())) {
            out.printf(SNIPPET_INIT, context.get(KEY_INDENT_BASE), getRootScope());
            out.printf(MULTIPLEX_SELECT_INPUT, indent, indent, END_POINT, indent);
        } else {
            out.printf(MULTIPLEX_SELECT_INPUT, indent, indent, addQuotes(concatName), indent);
            out.printf(SNIPPET_INIT, context.get(KEY_INDENT_BASE), getRootScope());
        }
    }

    @Override
    public String toString() {
        return this.name + " " + Arrays.toString(this.outputShape);
    }
}
