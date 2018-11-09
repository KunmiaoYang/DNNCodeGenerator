package org.ncsu.dnn.tf;

import junit.framework.TestCase;
import org.ncsu.dnn.caffe.*;

import java.io.File;
import java.io.IOException;

public class TFModelTest extends TestCase {
    public void testTFModel() {
        File protoTextFile = new File(CaffeModelTest.class.getResource("inception_v1.prototxt").getFile());
        Scanner scanner;
        try {
            scanner = new Scanner(protoTextFile);
            Parser parser = new Parser(scanner.getTokenList().iterator());
            CaffeModel caffeModel = new CaffeModel(parser.getRoot());
            TFModel tfModel = new TFModel(caffeModel);
            System.out.println(caffeModel.getName());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}