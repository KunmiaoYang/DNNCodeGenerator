package org.ncsu.dnn.caffe;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Token {
    private static final String REGEX_STRING = "^\"([^\"]|\\\\\")*\"$";
    private static final String REGEX_NAME = "^[A-Za-z_]\\w*(\\.[A-Za-z_]\\w*)*";
    private static final String REGEX_SPACE = "^\\s+$";
    private static final String REGEX_NO_SPACE = "[^\\s]+";
    public static final Token EOF = new Token(TokenName.EOF, "");

    private TokenName tokenName;
    private String val;

    public Token(TokenName tokenName, String val) {
        this.tokenName = tokenName;
        this.val = val;
    }

    public TokenName getTokenName() {
        return tokenName;
    }

    public String getVal() {
        return val;
    }

    static Token parseToken(String str) {
        if (isSpace(str))
            return new Token(TokenName.SPACE, str);

        if (":".equals(str))
            return new Token(TokenName.COLON, str);

        if ("{".equals(str))
            return new Token(TokenName.LEFT_BRACE, str);

        if ("}".equals(str))
            return new Token(TokenName.RIGHT_BRACE, str);

        if (isString(str))
            return new Token(TokenName.STRING, str);

        if (isBoolean(str))
            return new Token(TokenName.BOOLEAN, str);

        if (isInteger(str))
            return new Token(TokenName.INTEGER, str);

        if (isFloat(str))
            return new Token(TokenName.FLOAT, str);

        if (isName(str))
            return new Token(TokenName.NAME, str);

        return null;
    }

    static boolean isString(String s) {
        return s.matches(REGEX_STRING);
    }

    static boolean isBoolean(String s) {
        return s.equals("true") || s.equals("false");
    }

    static boolean isInteger(String s) {
        try {
            BigInteger bigInteger = new BigInteger(s);
        } catch (Exception e) {
            return false;
        }
        return true;
//        return s.matches(REGEX_NO_SPACE) && new Scanner(s).hasNextBigInteger();
    }

    static boolean isFloat(String s) {
        try {
            BigDecimal bigDecimal = new BigDecimal(s);
        } catch (Exception e) {
            return false;
        }
        return true;
//        return s.matches(REGEX_NO_SPACE) && new Scanner(s).hasNextBigDecimal();
    }

    static boolean isName(String s) {
        return s.matches(REGEX_NAME);
    }

    static boolean isSpace(String s) {
        return s.matches(REGEX_SPACE);
    }
}
