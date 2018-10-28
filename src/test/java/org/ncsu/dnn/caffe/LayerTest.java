package org.ncsu.dnn.caffe;

import junit.framework.Assert;
import junit.framework.TestCase;

import static org.ncsu.dnn.caffe.CaffeModel.*;
import static org.ncsu.dnn.caffe.LayerType.*;
import static org.ncsu.dnn.caffe.TokenName.*;

public class LayerTest extends TestCase {
    public void testLayer() {
        ASTNode node = new ASTNode(), nameNode = new ASTNode(), typeNode = new ASTNode();
        nameNode.addValue(new Token(NAME, "name"));
        typeNode.addValue(new Token(STRING, "\"Convolution\""));
        node.addChild(KEY_NAME, nameNode);
        node.addChild(KEY_TYPE, typeNode);
        Layer layer = new Layer(node);
        Assert.assertEquals(Convolution, layer.getType());

        typeNode.addValue(new Token(STRING, "\"Pooling\""));
        layer = new Layer(node);
        Assert.assertEquals(Pooling, layer.getType());
    }
}