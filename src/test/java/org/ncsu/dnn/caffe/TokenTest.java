package org.ncsu.dnn.caffe;

import junit.framework.Assert;
import junit.framework.TestCase;

import static org.ncsu.dnn.caffe.Token.isSpecial;
import static org.ncsu.dnn.caffe.TokenName.*;

public class TokenTest extends TestCase {
    public void testIsString() {
        Assert.assertTrue(Token.isString("\"abc\""));
        Assert.assertTrue(Token.isString("\"\""));
        Assert.assertTrue(Token.isString("\"a\\\"bc\""));
        Assert.assertTrue(Token.isString("\"true\""));

        Assert.assertFalse(Token.isString("abc\""));
        Assert.assertFalse(Token.isString("\"abc"));
        Assert.assertFalse(Token.isString("abc\"\""));
        Assert.assertFalse(Token.isString("\"\"abc"));
        Assert.assertFalse(Token.isString("\"\"\""));
        Assert.assertFalse(Token.isString("\"\"\"\""));
        Assert.assertFalse(Token.isString("\"abc\"\"\""));
    }

    public void testIsBoolean() {
        Assert.assertTrue(Token.isBoolean("true"));
        Assert.assertTrue(Token.isBoolean("false"));

        Assert.assertFalse(Token.isBoolean("True"));
        Assert.assertFalse(Token.isBoolean("TRUE"));
        Assert.assertFalse(Token.isBoolean("False"));
        Assert.assertFalse(Token.isBoolean("fAlSe"));

        Assert.assertFalse(Token.isBoolean("truse"));
        Assert.assertFalse(Token.isBoolean("fale"));
        Assert.assertFalse(Token.isBoolean("1"));
        Assert.assertFalse(Token.isBoolean("0"));
        Assert.assertFalse(Token.isBoolean("\"true\""));
        Assert.assertFalse(Token.isBoolean("\"false\""));
    }

    public void testIsInteger() {
        Assert.assertTrue(Token.isInteger("0"));
        Assert.assertTrue(Token.isInteger("123"));
        Assert.assertTrue(Token.isInteger("+123"));
        Assert.assertTrue(Token.isInteger("-123"));
        Assert.assertTrue(Token.isInteger("000123"));
        Assert.assertTrue(Token.isInteger("123456789012345678901234567890123456789012345678901234567890"));

        Assert.assertFalse(Token.isInteger("0."));
        Assert.assertFalse(Token.isInteger(".0"));
        Assert.assertFalse(Token.isInteger("123.0"));
        Assert.assertFalse(Token.isInteger("123E10"));
        Assert.assertFalse(Token.isInteger("0-123"));
        Assert.assertFalse(Token.isInteger("123+345"));
    }

    public void testIsFloat() {
        Assert.assertTrue(Token.isFloat("0."));
        Assert.assertTrue(Token.isFloat(".0"));
        Assert.assertTrue(Token.isFloat("0.00"));
        Assert.assertTrue(Token.isFloat("00.00"));
        Assert.assertTrue(Token.isFloat("123.0"));
        Assert.assertTrue(Token.isFloat("-123.4"));
        Assert.assertTrue(Token.isFloat("123.4E10"));
        Assert.assertTrue(Token.isFloat("+123.4E10"));
        Assert.assertTrue(Token.isFloat("123.4E-10"));
        Assert.assertTrue(Token.isFloat("123.4E+10"));
        Assert.assertTrue(Token.isFloat("-123.E10"));
        Assert.assertTrue(Token.isFloat("123E10"));
        Assert.assertTrue(Token.isFloat("123.4e10"));

        Assert.assertFalse(Token.isFloat("123.4F10"));
        Assert.assertFalse(Token.isFloat("--123.E10"));
        Assert.assertFalse(Token.isFloat("-+123.E10"));
        Assert.assertFalse(Token.isFloat("123.4E10E10"));
    }

    public void testIsName() {
        Assert.assertTrue(Token.isName("abc"));
        Assert.assertTrue(Token.isName("ABC"));
        Assert.assertTrue(Token.isName("_abc"));
        Assert.assertTrue(Token.isName("abc0"));
        Assert.assertTrue(Token.isName("abc.efg"));
        Assert.assertTrue(Token.isName("a.e.f.g"));
        Assert.assertTrue(Token.isName("_a._e0._f.g"));

        Assert.assertFalse(Token.isName("0abc"));
        Assert.assertFalse(Token.isName(".abc"));
        Assert.assertFalse(Token.isName("+abc"));
        Assert.assertFalse(Token.isName("a-bc"));
        Assert.assertFalse(Token.isName("abc*"));
        Assert.assertFalse(Token.isName("123"));
        Assert.assertFalse(Token.isName("abc."));
        Assert.assertFalse(Token.isName("a..bc"));
        Assert.assertFalse(Token.isName("a.0c"));
        Assert.assertFalse(Token.isName(" abc"));
        Assert.assertFalse(Token.isName("abc "));
        Assert.assertFalse(Token.isName("ab c"));
    }

    public void testParseToken() {
        Token token;

        token = Token.parseToken("{");
        Assert.assertNotNull(token);
        Assert.assertEquals(LEFT_BRACE, token.getTokenName());
        Assert.assertEquals("{", token.getVal());

        token = Token.parseToken("}");
        Assert.assertNotNull(token);
        Assert.assertEquals(RIGHT_BRACE, token.getTokenName());
        Assert.assertEquals("}", token.getVal());

        token = Token.parseToken(":");
        Assert.assertNotNull(token);
        Assert.assertEquals(COLON, token.getTokenName());
        Assert.assertEquals(":", token.getVal());

        token = Token.parseToken("0");
        Assert.assertNotNull(token);
        Assert.assertEquals(INTEGER, token.getTokenName());
        Assert.assertEquals("0", token.getVal());

        token = Token.parseToken("123456789012345678901234567890");
        Assert.assertNotNull(token);
        Assert.assertEquals(INTEGER, token.getTokenName());
        Assert.assertEquals("123456789012345678901234567890", token.getVal());

        token = Token.parseToken("0.");
        Assert.assertNotNull(token);
        Assert.assertEquals(FLOAT, token.getTokenName());
        Assert.assertEquals("0.", token.getVal());

        token = Token.parseToken("10E12");
        Assert.assertNotNull(token);
        Assert.assertEquals(FLOAT, token.getTokenName());
        Assert.assertEquals("10E12", token.getVal());

        token = Token.parseToken("true");
        Assert.assertNotNull(token);
        Assert.assertEquals(BOOLEAN, token.getTokenName());
        Assert.assertEquals("true", token.getVal());

        token = Token.parseToken("false");
        Assert.assertNotNull(token);
        Assert.assertEquals(BOOLEAN, token.getTokenName());
        Assert.assertEquals("false", token.getVal());

        token = Token.parseToken("\"abc\"");
        Assert.assertNotNull(token);
        Assert.assertEquals(STRING, token.getTokenName());
        Assert.assertEquals("abc", token.getVal());

        token = Token.parseToken("\"a\\\"bc\"");
        Assert.assertNotNull(token);
        Assert.assertEquals(STRING, token.getTokenName());
        Assert.assertEquals("a\\\"bc", token.getVal());

        token = Token.parseToken("\"a bc\"");
        Assert.assertNotNull(token);
        Assert.assertEquals(STRING, token.getTokenName());
        Assert.assertEquals("a bc", token.getVal());

        token = Token.parseToken("_var0");
        Assert.assertNotNull(token);
        Assert.assertEquals(NAME, token.getTokenName());
        Assert.assertEquals("_var0", token.getVal());

        token = Token.parseToken("trueName");
        Assert.assertNotNull(token);
        Assert.assertEquals(NAME, token.getTokenName());
        Assert.assertEquals("trueName", token.getVal());

        token = Token.parseToken("MAX");
        Assert.assertNotNull(token);
        Assert.assertEquals(SPECIAL, token.getTokenName());
        Assert.assertEquals("MAX", token.getVal());

        token = Token.parseToken("AVE");
        Assert.assertNotNull(token);
        Assert.assertEquals(SPECIAL, token.getTokenName());
        Assert.assertEquals("AVE", token.getVal());

        Assert.assertNull(Token.parseToken("123var"));
        Assert.assertNull(Token.parseToken("123E"));
        Assert.assertNull(Token.parseToken("12 3"));
        Assert.assertNull(Token.parseToken(" 123"));
        Assert.assertNull(Token.parseToken("123 "));
        Assert.assertNull(Token.parseToken("12E 3"));
        Assert.assertNull(Token.parseToken(" 1.23"));
        Assert.assertNull(Token.parseToken("1.23 "));
        Assert.assertNull(Token.parseToken("var:"));
        Assert.assertNull(Token.parseToken("var{"));
        Assert.assertNull(Token.parseToken("var "));
        Assert.assertNull(Token.parseToken("va r"));
        Assert.assertNull(Token.parseToken("123}"));
        Assert.assertNull(Token.parseToken(":123"));
        Assert.assertNull(Token.parseToken("{var"));
        Assert.assertNull(Token.parseToken(" var"));
    }

    public void testIsSpace() {
        Assert.assertTrue(Token.isSpace(" "));
        Assert.assertTrue(Token.isSpace("  "));
        Assert.assertTrue(Token.isSpace(" \t"));

        Assert.assertFalse(Token.isSpace(" abc"));
        Assert.assertFalse(Token.isSpace("abc "));
        Assert.assertFalse(Token.isSpace("ab c"));
    }

    public void testIsSpecial() {
        Assert.assertTrue(isSpecial("MAX"));
        Assert.assertTrue(isSpecial("AVE"));

        Assert.assertFalse(isSpecial("AVEMAX"));
        Assert.assertFalse(isSpecial("name"));
    }

    public void testGetVal() {
        Assert.assertEquals("abc", new Token(STRING, "\"abc\"").getVal());
        Assert.assertEquals("abc", new Token(NAME, "abc").getVal());
        Assert.assertEquals("MAX", new Token(SPECIAL, "MAX").getVal());
        Assert.assertEquals("123", new Token(INTEGER, "123").getVal());
    }
}
