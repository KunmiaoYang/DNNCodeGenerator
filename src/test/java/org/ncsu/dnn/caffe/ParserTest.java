package org.ncsu.dnn.caffe;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

public class ParserTest extends TestCase {
    public void testParser() {
        File protoTextFile = new File(CaffeModelTest.class.getResource("inception_v1.prototxt").getFile());
        try {
            Scanner scanner = new Scanner(protoTextFile);
            Parser parser = new Parser(scanner.getTokenList().iterator());
            ASTNode root = parser.getRoot();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}