package org.ncsu.dnn.caffe;

import java.util.Iterator;

import static org.ncsu.dnn.caffe.TokenName.*;

public class Parser {
    private static final String EXCEPTION_NO_EOF_TOKEN = "No EOF token found!";
    private static final String EXCEPTION_PARSE = "Failed to parse ";

    private Iterator<Token> it;
    private Token cur;
    private ASTNode root;

    public Parser(Iterator<Token> it) {
        this.root = new ASTNode();
        this.it = it;
        nextToken();
        parse(root);
    }

    private void parse(ASTNode node) {
        switch (cur.getTokenName()) {
            case NAME:
            case EOF:
                parseList(node);
                return;
        }
        throw new ParseException(EXCEPTION_PARSE + "Goal: " + cur);
    }

    private void parseList(ASTNode node) {
        switch (cur.getTokenName()) {
            case NAME:
                String name = cur.getVal();
                nextToken();
                node.addChild(name, parseObject());
                parseList(node);
            case RIGHT_BRACE:
            case EOF:
                return;
        }
        throw new ParseException(EXCEPTION_PARSE + "List: " + cur);
    }

    private ASTNode parseObject() {
        switch (cur.getTokenName()) {
            case LEFT_BRACE:
                nextToken();
                ASTNode node = new ASTNode();
                parseList(node);
                if (cur.getTokenName() != RIGHT_BRACE) break;
                nextToken();
                return node;
            case COLON:
                nextToken();
                return parseValue();
        }
        throw new ParseException(EXCEPTION_PARSE + "Object: " + cur);
    }

    private ASTNode parseValue() {
        switch (cur.getTokenName()) {
            case INTEGER:
            case FLOAT:
            case BOOLEAN:
            case STRING:
            case SPECIAL:
                ASTNode node = new ASTNode();
                node.addValue(cur);
                nextToken();
                return node;
        }
        throw new ParseException(EXCEPTION_PARSE + "Value: " + cur);
    }

    private void nextToken() {
        assert null == cur || cur.getTokenName() != EOF: "Attempt to get next token when iterator already reached the end!";
        if (!it.hasNext()) {
            throw new ParseException(EXCEPTION_NO_EOF_TOKEN);
        }
        while (it.hasNext()) {
            cur = it.next();
            if (cur.getTokenName() != SPACE) break;
        }
    }

    public ASTNode getRoot() {
        return root;
    }
}
