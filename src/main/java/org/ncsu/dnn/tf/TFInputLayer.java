package org.ncsu.dnn.tf;

import java.io.PrintStream;

public class TFInputLayer extends TFLayer {
    TFInputLayer(Param param) {
        super(param);
        this.output = this.input;
    }

    @Override
    String inlineCode(PrintStream out, String indent, String scope) {
        return null;
    }
}
