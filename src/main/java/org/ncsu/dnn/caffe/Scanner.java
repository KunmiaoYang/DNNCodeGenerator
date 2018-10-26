package org.ncsu.dnn.caffe;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.ncsu.dnn.caffe.TokenName.*;

public class Scanner {
    private BufferedReader reader;
    private List<Token> tokenList;
    private boolean success;

    public Scanner(BufferedReader reader) throws IOException {
        this.reader = reader;
        this.tokenList = new ArrayList<>();
        this.success = false;

        try {
            this.success = scan();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
    }

    public Scanner(File file) throws IOException {
        this(new BufferedReader(new FileReader(file)));
    }

    private boolean scan() throws IOException {
        String line;
        int left, right, n;
        Token prev = null, cur;
        TokenName tokenName;
        char c;
        while ((line = reader.readLine()) != null) {
            if (line.equals("")) continue;
            for (left = 0, right = 1, n = line.length(); left < n; right++) {
                cur = Token.parseToken(line.substring(left, right));
                if (null != cur) {
                    if (right == n) {
                        tokenList.add(cur);
                        break;
                    }
                    prev = cur;
                } else if (right == n) {
                    if (null != prev) {
                        tokenList.add(prev);
                        left += prev.getVal().length();
                        right = left;
                        prev = null;
                    } else {
                        return false;
                    }
                } else if (null != prev) {
                    tokenName = prev.getTokenName();
                    c = line.charAt(right - 1);
                    if (SPACE == tokenName || COLON == tokenName || STRING == tokenName ||
                            LEFT_BRACE == tokenName || RIGHT_BRACE == tokenName || Character.isWhitespace(c) ||
                            '\"' == c || ':' == c || '{' == c || '}' == c) {
                        tokenList.add(prev);
                        left += prev.getVal().length();
                        right = left;
                        prev = null;
                    }
                }
            }
            tokenList.add(new Token(SPACE, "\r\n"));
        }
        this.tokenList.add(Token.EOF);
        return true;
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    public boolean isSuccess() {
        return success;
    }
}
