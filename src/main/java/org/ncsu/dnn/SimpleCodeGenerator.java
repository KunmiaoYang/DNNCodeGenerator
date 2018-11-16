package org.ncsu.dnn;

import org.ncsu.dnn.caffe.CaffeModel;
import org.ncsu.dnn.tf.TFModel;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static org.ncsu.dnn.tf.TFModel.KEY_FUNC;
import static org.ncsu.dnn.tf.TFModel.KEY_INDENT;

public class SimpleCodeGenerator {
    public static final ResourceBundle SNIPPETS;
    static final String SNIPPET_CHANGE_IMAGE_SIZE;
    static {
        SNIPPETS = ResourceBundle.getBundle("org.ncsu.dnn.tf.template.snippets");
        SNIPPET_CHANGE_IMAGE_SIZE = SNIPPETS.getString("model.changeImageSize");
    }
    static void appendFile(PrintStream out, InputStream file, Map<String, String> context) {
        String indent = context.get(KEY_INDENT);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file))) {
            for (String line = reader.readLine(); null != line; line = reader.readLine()) {
                out.print(indent);
                out.println(line);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        File caffeModelFile = new File(args[0]);
        String modelName = caffeModelFile.getName();
        if (modelName.contains("."))
            modelName = modelName.substring(0, modelName.lastIndexOf('.'));

        CaffeModel caffeModel = CaffeModel.createFromFile(caffeModelFile);
        if (null == caffeModel) throw new FileNotFoundException("Caffe model file not found!");
        if (null == caffeModel.getName()) caffeModel.setName(modelName);
        TFModel tfModel = new TFModel(caffeModel);

        InputStream header = SimpleCodeGenerator.class.getResourceAsStream("tf/template/simpleHeader.py");
        InputStream footer = SimpleCodeGenerator.class.getResourceAsStream("tf/template/simpleFooter.py");

        File outputFile = new File(args.length > 1? args[1]: ("./" + modelName + "_simple.py"));
        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));

        Map<String, String> context = new HashMap<>();
        context.put(KEY_INDENT, "");
        context.put(KEY_FUNC, modelName);

        appendFile(out, header, context);
        out.println();

        tfModel.generateFuncDef(out, context);
        tfModel.generateCode(out, context);
        out.println();
        out.printf(SNIPPET_CHANGE_IMAGE_SIZE, modelName, tfModel.inputShape[1]);

        context.put(KEY_INDENT, "");
        appendFile(out, footer, context);
        out.close();
    }
}
