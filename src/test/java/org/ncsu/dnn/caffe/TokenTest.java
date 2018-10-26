package org.ncsu.dnn.caffe;

import junit.framework.Assert;
import junit.framework.TestCase;

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
        Assert.assertFalse(Token.isInteger("123.0"));
        Assert.assertFalse(Token.isInteger("123E10"));
        Assert.assertFalse(Token.isInteger("0-123"));
        Assert.assertFalse(Token.isInteger("123+345"));
    }

    public void testIsFloat() {
        Assert.assertTrue(Token.isFloat("0."));
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
    }

    public void testParseToken() {
        Token token;

        token = Token.parseToken("{");
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenName.LEFT_BRACE, token.getTokenName());
        Assert.assertEquals("{", token.getVal());

        token = Token.parseToken("}");
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenName.RIGHT_BRACE, token.getTokenName());
        Assert.assertEquals("}", token.getVal());

        token = Token.parseToken(":");
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenName.COLON, token.getTokenName());
        Assert.assertEquals(":", token.getVal());

        token = Token.parseToken("0");
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenName.INTEGER, token.getTokenName());
        Assert.assertEquals("0", token.getVal());

        token = Token.parseToken("123456789012345678901234567890");
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenName.INTEGER, token.getTokenName());
        Assert.assertEquals("123456789012345678901234567890", token.getVal());

        token = Token.parseToken("0.");
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenName.FLOAT, token.getTokenName());
        Assert.assertEquals("0.", token.getVal());

        token = Token.parseToken("10E12");
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenName.FLOAT, token.getTokenName());
        Assert.assertEquals("10E12", token.getVal());

        token = Token.parseToken("true");
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenName.BOOLEAN, token.getTokenName());
        Assert.assertEquals("true", token.getVal());

        token = Token.parseToken("false");
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenName.BOOLEAN, token.getTokenName());
        Assert.assertEquals("false", token.getVal());

        token = Token.parseToken("\"abc\"");
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenName.STRING, token.getTokenName());
        Assert.assertEquals("\"abc\"", token.getVal());

        token = Token.parseToken("\"a\\\"bc\"");
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenName.STRING, token.getTokenName());
        Assert.assertEquals("\"a\\\"bc\"", token.getVal());

        token = Token.parseToken("_var0");
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenName.NAME, token.getTokenName());
        Assert.assertEquals("_var0", token.getVal());

        Assert.assertNull(Token.parseToken("123var"));
        Assert.assertNull(Token.parseToken("123E"));
        Assert.assertNull(Token.parseToken("var:"));
        Assert.assertNull(Token.parseToken("var{"));
        Assert.assertNull(Token.parseToken("123}"));
        Assert.assertNull(Token.parseToken(":123"));
        Assert.assertNull(Token.parseToken("{var"));
    }
}
