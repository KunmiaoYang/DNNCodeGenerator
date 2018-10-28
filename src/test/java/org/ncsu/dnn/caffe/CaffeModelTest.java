package org.ncsu.dnn.caffe;

import junit.framework.TestCase;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CaffeModelTest extends TestCase {
    public void testCaffeModel() {
        Path path = FileSystems.getDefault().getPath("src","test", "caffeModel", "inception_v1.prototxt").toAbsolutePath();
        Scanner scanner;
        try {
            scanner = new Scanner(path.toFile());
            Parser parser = new Parser(scanner.getTokenList().iterator());
            CaffeModel caffeModel = new CaffeModel(parser.getRoot());
            System.out.println(caffeModel.getName());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}