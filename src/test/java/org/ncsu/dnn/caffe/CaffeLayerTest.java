package org.ncsu.dnn.caffe;

import junit.framework.Assert;
import junit.framework.TestCase;

import static org.ncsu.dnn.caffe.CaffeModel.*;
import static org.ncsu.dnn.caffe.CaffeLayerType.*;
import static org.ncsu.dnn.caffe.TokenName.*;

public class CaffeLayerTest extends TestCase {
    public void testLayer() {
        ASTNode node = new ASTNode(), nameNode = new ASTNode(), typeNode = new ASTNode();
        nameNode.addValue(new Token(NAME, "name"));
        typeNode.addValue(new Token(STRING, "\"Convolution\""));
        node.addChild(KEY_NAME, nameNode);
        node.addChild(KEY_TYPE, typeNode);
        CaffeLayer layer = new CaffeLayer(node);
        Assert.assertEquals(Convolution, layer.getType());

        typeNode.addValue(new Token(STRING, "\"Pooling\""));
        layer = new CaffeLayer(node);
        Assert.assertEquals(Pooling, layer.getType());
    }
}