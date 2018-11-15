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

    public List<ASTNode> get(String key) {
        return children.get(key);
    }

    public ASTNode getFirst(String key) {
        List<ASTNode> nodes = get(key);
        return (null == nodes || nodes.isEmpty())? null: nodes.get(0);
    }

    public String getFirstValue(String key) {
        ASTNode node = getFirst(key);
        return null == node? null: node.val.getVal();
    }
}
