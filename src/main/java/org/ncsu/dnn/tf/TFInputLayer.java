package org.ncsu.dnn.tf;

import java.io.PrintStream;
import java.util.Map;

public class TFInputLayer extends TFLayer {
    TFInputLayer(Param param) {
        super(param);
        this.output = this.input;
    }

    @Override
    void inlineCode(PrintStream out, Map<String, String> context) {
    }
}
