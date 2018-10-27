package org.ncsu.dnn.caffe;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.ncsu.dnn.caffe.TokenName.*;

public class Token {
    private static final String REGEX_STRING = "^\"([^\"]|\\\\\")*\"$";
    private static final String REGEX_NAME = "^[A-Za-z_]\\w*(\\.[A-Za-z_]\\w*)*";
    private static final String REGEX_SPACE = "^\\s+$";
    private static final String REGEX_NO_SPACE = "[^\\s]+";
    private static final Set<String> SET_SPECIAL = new HashSet<>(Arrays.asList("MAX", "AVE"));
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
            return new Token(SPACE, str);

        if (":".equals(str))
            return new Token(COLON, str);

        if ("{".equals(str))
            return new Token(LEFT_BRACE, str);

        if ("}".equals(str))
            return new Token(RIGHT_BRACE, str);

        if (isString(str))
            return new Token(STRING, str);

        if (isBoolean(str))
            return new Token(BOOLEAN, str);

        if (isSpecial(str))
            return new Token(SPECIAL, str);

        if (isInteger(str))
            return new Token(INTEGER, str);

        if (isFloat(str))
            return new Token(FLOAT, str);

        if (isName(str))
            return new Token(NAME, str);

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

    static boolean isSpecial(String s) {
        return SET_SPECIAL.contains(s);
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenName=" + tokenName +
                ", val='" + val + '\'' +
                '}';
    }

    static boolean isSpace(String s) {
        return s.matches(REGEX_SPACE);
    }
}
