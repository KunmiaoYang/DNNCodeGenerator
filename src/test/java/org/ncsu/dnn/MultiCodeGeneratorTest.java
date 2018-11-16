package org.ncsu.dnn;

import junit.framework.TestCase;

import java.io.FileNotFoundException;

public class MultiCodeGeneratorTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testInceptionV1() {
        String protoTextFile = MultiCodeGeneratorTest.class
                .getResource("caffe/inception_v1.prototxt").getFile();
        String outputFile = "output/inception_v1.multiplexing.py";
        String[] args = {protoTextFile, outputFile};
        try {
            MultiCodeGenerator.main(args);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void testInceptionV2() {
        String protoTextFile = MultiCodeGeneratorTest.class
                .getResource("caffe/inception_v2.prototxt").getFile();
        String outputFile = "output/inception_v2.multiplexing.py";
        String[] args = {protoTextFile, outputFile};
        try {
            MultiCodeGenerator.main(args);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void testInceptionV3() {
        String protoTextFile = MultiCodeGeneratorTest.class
                .getResource("caffe/inception_v3.prototxt").getFile();
        String outputFile = "output/inception_v3.multiplexing.py";
        String[] args = {protoTextFile, outputFile};
        try {
            MultiCodeGenerator.main(args);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void testInceptionV4() {
        String protoTextFile = MultiCodeGeneratorTest.class
                .getResource("caffe/inception_v4.prototxt").getFile();
        String outputFile = "output/inception_v4.multiplexing.py";
        String[] args = {protoTextFile, outputFile};
        try {
            MultiCodeGenerator.main(args);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}