package org.ncsu.dnn.caffe;

import java.util.Iterator;

import static org.ncsu.dnn.caffe.TokenName.*;

public class Parser {
    private static final String EXCEPTION_NO_EOF_TOKEN = "No EOF token found!";
    private static final String EXCEPTION_PARSE = "Failed to parse ";

    private Iterator<Token> it;
    private Token cur;

    public Parser(Iterator<Token> it) {
        this.it = it;
        nextToken();
        parse();
    }

    private void parse() {
        switch (cur.getTokenName()) {
            case NAME:
            case EOF:
                parseList();
                return;
        }
        throw new ParseException(EXCEPTION_PARSE + "Goal: " + cur);
    }

    private void parseList() {
        switch (cur.getTokenName()) {
            case NAME:
                nextToken();
                parseObject();
                parseList();
            case RIGHT_BRACE:
            case EOF:
                return;
        }
        throw new ParseException(EXCEPTION_PARSE + "List: " + cur);
    }

    private void parseObject() {
        switch (cur.getTokenName()) {
            case LEFT_BRACE:
                nextToken();
                parseList();
                if (cur.getTokenName() != RIGHT_BRACE) break;
                nextToken();
                return;
            case COLON:
                nextToken();
                parseValue();
                return;
        }
        throw new ParseException(EXCEPTION_PARSE + "Object: " + cur);
    }

    private void parseValue() {
        switch (cur.getTokenName()) {
            case INTEGER:
            case FLOAT:
            case BOOLEAN:
            case STRING:
            case SPECIAL:
                nextToken();
                return;
        }
        throw new ParseException(EXCEPTION_PARSE + "Value: " + cur);
    }

    private void nextToken() {
        assert cur.getTokenName() != EOF: "Attempt to get next token when iterator already reached the end!";
        if (!it.hasNext()) {
            throw new ParseException(EXCEPTION_NO_EOF_TOKEN);
        }
        while (it.hasNext()) {
            cur = it.next();
            if (cur.getTokenName() != SPACE) break;
        }
    }
}
