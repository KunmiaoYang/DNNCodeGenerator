package org.ncsu.dnn;

import junit.framework.TestCase;

import java.io.FileNotFoundException;

public class SimpleCodeGeneratorTest extends TestCase {
    public void testInceptionV1() {
        String protoTextFile = SimpleCodeGeneratorTest.class
                .getResource("caffe/inception_v1.prototxt").getFile();
        String outputFile = "output/inception_v1.simple.py";
        String[] args = {protoTextFile, outputFile};
        try {
            SimpleCodeGenerator.main(args);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void testInceptionV2() {
        String protoTextFile = SimpleCodeGeneratorTest.class
                .getResource("caffe/inception_v2.prototxt").getFile();
        String outputFile = "output/inception_v2.simple.py";
        String[] args = {protoTextFile, outputFile};
        try {
            SimpleCodeGenerator.main(args);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void testInceptionV3() {
        String protoTextFile = SimpleCodeGeneratorTest.class
                .getResource("caffe/inception_v3.prototxt").getFile();
        String outputFile = "output/inception_v3.simple.py";
        String[] args = {protoTextFile, outputFile};
        try {
            SimpleCodeGenerator.main(args);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void testInceptionV4() {
        String protoTextFile = SimpleCodeGeneratorTest.class
                .getResource("caffe/inception_v4.prototxt").getFile();
        String outputFile = "output/inception_v4.simple.py";
        String[] args = {protoTextFile, outputFile};
        try {
            SimpleCodeGenerator.main(args);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void testAlexNet() {
        String protoTextFile = SimpleCodeGeneratorTest.class
                .getResource("caffe/alexnet.prototxt").getFile();
        String outputFile = "output/alexnet.simple.py";
        String[] args = {protoTextFile, outputFile};
        try {
            SimpleCodeGenerator.main(args);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}