package org.ncsu.dnn.caffe;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

public class ScannerTest extends TestCase {
    public void testScanner() {
        try {
            Path path = FileSystems.getDefault().getPath("src","test", "caffeModel", "inception_v1.prototxt").toAbsolutePath();
            System.out.println(path);
            Scanner scanner = new Scanner(path.toFile());
//            for (Token token: scanner.getTokenList())
//                System.out.print(token.getVal());
            Assert.assertTrue(scanner.isSuccess());
            List<Token> list = scanner.getTokenList();
            int p = 0, count = 0;
            for (Token token: list) {
                p++;
                if (TokenName.EOF == token.getTokenName()) {
                    count++;
                    break;
                }
            }
            Assert.assertEquals(list.size(), p);
            Assert.assertEquals(1, count);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}