package org.ncsu.dnn.caffe;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class ParserTest extends TestCase {
    public void testParser() {
        Path path = FileSystems.getDefault().getPath("src","test", "caffeModel", "inception_v1.prototxt").toAbsolutePath();
        System.out.println(path);
        try {
            Scanner scanner = new Scanner(path.toFile());
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