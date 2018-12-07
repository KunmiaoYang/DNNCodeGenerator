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
    static final String KEY_START_BRANCH = "branch";

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
    boolean canPrune, startBranch;
    abstract void inlineCode(PrintStream out, Map<String, String> context);

    void addSqueezeLayer() {
        // Do nothing
    }

    TFLayer(Param param) {
        this.input = param.getOrDefault(KEY_INPUT, DEFAULT_INPUT);
        this.output = param.getOrDefault(KEY_OUTPUT, DEFAULT_OUTPUT);
        this.name = param.get(KEY_NAME);
        if (null == this.name) this.name = param.caffeLayer.getName();
        this.outputShape = param.shape.clone();
        this.concatName = param.get(KEY_CONCAT_NAME);
        this.canPrune = param.branch >= 0;
        this.startBranch = param.param.containsKey(KEY_START_BRANCH);

        // Add branch layer to scope if it is not in the concat scope
        if (param.branch >= 0 && null != concatName &&
            !getRootScope().equals(concatName) && !name.contains("/")) {
            this.name = concatName + "/" + BRANCH_SCOPE_PREFIX + param.branch + "/" + this.name;
        }

        // In case these parameters are incorrectly passed to other layer
        param.param.remove(KEY_NAME);
        param.param.remove(KEY_START_BRANCH);
    }

    void generateCode(PrintStream out, Map<String, String> context) {
        String parentScope = getParentScope();
        if (context.get(KEY_SCOPE_PATH).equals("")) {
            context.put(KEY_INDENT, context.get(KEY_INDENT_BASE));
            out.printf(SNIPPET_INIT, context.get(KEY_INDENT_BASE), getRootScope());

            if (null != this.concatName && context.containsKey(KEY_MULTIPLEX)) {
                generateMultiplex(out, context);
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
            changeScope(out, context, pathCommon.length() == 0? "": pathCommon.substring(1));
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
        out.printf(MULTIPLEX_SELECT_INPUT, indent, indent, END_POINT, indent);
    }

    @Override
    public String toString() {
        return this.name + " " + Arrays.toString(this.outputShape);
    }

    static int calcSize(int inputSize, int kernel, int stride, int pad) {
        return (inputSize + 2*pad - kernel)/stride + 1;
    }
}
