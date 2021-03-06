package org.ncsu.dnn.caffe;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

public class CaffeModelTest extends TestCase {
    public void testCaffeModel() {
        File protoTextFile = new File(CaffeModelTest.class.getResource("inception_v1.prototxt").getFile());
        Scanner scanner;
        try {
            scanner = new Scanner(protoTextFile);
            Parser parser = new Parser(scanner.getTokenList().iterator());
            CaffeModel caffeModel = new CaffeModel(parser.getRoot());
            System.out.println(caffeModel.getName());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}