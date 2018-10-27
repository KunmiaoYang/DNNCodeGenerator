package org.ncsu.dnn.caffe;

import java.util.List;

public class CaffeModel {
    private static final String KEY_NAME = "name";
    private static final String KEY_INPUT = "input";
    private static final String KEY_INPUT_SHAPE = "input_shape";
    private static final String KEY_DIM = "dim";

    private String name;
    private String input;
    private int[] inputShape;

    private CaffeModel(String name, String input) {
        this.name = name;
        this.input = input;
        this.inputShape = null;
    }

    public CaffeModel(ASTNode root) {
        this(root.getFirstValue(KEY_NAME), root.getFirstValue(KEY_INPUT));
        parseShape(root.getFirst(KEY_INPUT_SHAPE));
    }

    private void parseShape(ASTNode inputShapeNode) {
        if (null != inputShapeNode) {
            List<ASTNode> dims = inputShapeNode.get(KEY_DIM);
            this.inputShape = new int[dims.size()];
            int i = 0;
            for (ASTNode dim: dims) {
                this.inputShape[i++] = Integer.parseInt(dim.val.getVal());
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getInput() {
        return input;
    }

    public int[] getInputShape() {
        return inputShape;
    }
}
