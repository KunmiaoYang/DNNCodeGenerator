package org.ncsu.dnn;

import org.ncsu.dnn.caffe.CaffeModel;
import org.ncsu.dnn.tf.TFModel;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.ncsu.dnn.SimpleCodeGenerator.*;
import static org.ncsu.dnn.tf.TFModel.*;

public class MultiCodeGenerator {
    public static void main(String[] args) throws FileNotFoundException {
        File caffeModelFile = new File(args[0]);
        String modelName = caffeModelFile.getName();
        if (modelName.contains("."))
            modelName = modelName.substring(0, modelName.lastIndexOf('.'));

        CaffeModel caffeModel = CaffeModel.createFromFile(caffeModelFile);
        if (null == caffeModel) throw new FileNotFoundException("Caffe model file not found!");
        if (null == caffeModel.getName()) caffeModel.setName(modelName);
        TFModel tfModel = new TFModel(caffeModel);

        InputStream header = MultiCodeGenerator.class.getResourceAsStream("tf/template/simpleHeader.py");
        InputStream footer = MultiCodeGenerator.class.getResourceAsStream("tf/template/simpleFooter.py");
        InputStream multiFunc = MultiCodeGenerator.class.getResourceAsStream("tf/template/multiplexingFunc.py");

        File outputFile = new File(args.length > 1? args[1]: ("./" + modelName + "_multiplexing.py"));
        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));

        Map<String, String> context = new HashMap<>();
        context.put(KEY_INDENT, "");
        context.put(KEY_FUNC, modelName);
        context.put(KEY_MULTIPLEX, "True");

        appendFile(out, header, context);
        out.println();

        tfModel.generateFuncDef(out, context);
        appendFile(out, multiFunc, context);
        tfModel.generateCode(out, context);
        out.println();
        out.printf(SNIPPET_CHANGE_IMAGE_SIZE, modelName, tfModel.inputShape[1]);

        context.put(KEY_INDENT, "");
        appendFile(out, footer, context);
        out.close();
    }
}
