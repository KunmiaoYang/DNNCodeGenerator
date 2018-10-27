package org.ncsu.dnn.caffe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ASTNode {
    Token val;
    Map<String, List<ASTNode>> children;

    public ASTNode() {
        this.val = null;
        this.children = new HashMap<>();
    }

    public void addValue(Token val) {
        this.val = val;
    }

    public void addChild(String key, ASTNode child) {
        List<ASTNode> childList = children.computeIfAbsent(key, k -> new ArrayList<>());
        childList.add(child);
    }
}
