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
        Assert.assertTrue(Token.isBoolean("True"));
        Assert.assertTrue(Token.isBoolean("TRUE"));
        Assert.assertTrue(Token.isBoolean("false"));
        Assert.assertTrue(Token.isBoolean("False"));
        Assert.assertTrue(Token.isBoolean("fAlSe"));

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

    public void testIsReal() {
        Assert.assertTrue(Token.isReal("0."));
        Assert.assertTrue(Token.isReal("0.00"));
        Assert.assertTrue(Token.isReal("00.00"));
        Assert.assertTrue(Token.isReal("123.0"));
        Assert.assertTrue(Token.isReal("-123.4"));
        Assert.assertTrue(Token.isReal("123.4E10"));
        Assert.assertTrue(Token.isReal("+123.4E10"));
        Assert.assertTrue(Token.isReal("123.4E-10"));
        Assert.assertTrue(Token.isReal("123.4E+10"));
        Assert.assertTrue(Token.isReal("-123.E10"));
        Assert.assertTrue(Token.isReal("123E10"));
        Assert.assertTrue(Token.isReal("123.4e10"));

        Assert.assertFalse(Token.isReal("123.4F10"));
        Assert.assertFalse(Token.isReal("--123.E10"));
        Assert.assertFalse(Token.isReal("-+123.E10"));
        Assert.assertFalse(Token.isReal("123.4E10E10"));
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
}