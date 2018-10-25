package org.ncsu.dnn.caffe;

import java.io.IOException;

public class Interactive {
    public static void testIsString() throws IOException {
        while (true) {
            byte[] input = new byte[100];
            int len = System.in.read(input);
            String s = new String(input, 0, len - 1);
            System.out.println(s + ": " + Token.isString(s));
        }
    }
}
