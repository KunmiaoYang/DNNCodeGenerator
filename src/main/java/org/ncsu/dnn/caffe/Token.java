package org.ncsu.dnn.caffe;

import java.util.Scanner;

public class Token {
    private static final String REGEX_STRING = "^\"([^\"]|\\\\\")*\"$";
    private static final String REGEX_NAME = "^[A-Za-z_]\\w*(\\.[A-Za-z_]\\w*)*";

    static boolean isString(String s) {
        return s.matches(REGEX_STRING);
    }

    static boolean isBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
    }

    static boolean isInteger(String s) {
        return new Scanner(s).hasNextBigInteger();
    }

    static boolean isReal(String s) {
        return new Scanner(s).hasNextBigDecimal();
    }

    static boolean isName(String s) {
        return s.matches(REGEX_NAME);
    }
}
