package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeModel;

import java.io.*;
import java.util.ResourceBundle;

public class SimpleCodeGenerator {
    static final ResourceBundle SNIPPETS;
    static final String INDENT_STRING = "  ";   // Indent with 2 spaces
    private static final String SNIPPET_CHANGE_IMAGE_SIZE;
    static {
        SNIPPETS = ResourceBundle.getBundle("org.ncsu.dnn.tf.template.snippets");
        SNIPPET_CHANGE_IMAGE_SIZE = SNIPPETS.getString("model.changeImageSize");
    }
    private static void appendFile(PrintStream out, File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for (String line = reader.readLine(); null != line; line = reader.readLine()) {
                out.println(line);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        File header = new File(SimpleCodeGenerator.class.getResource("template/simpleHeader.txt").getFile());
        File footer = new File(SimpleCodeGenerator.class.getResource("template/simpleFooter.txt").getFile());
        File test = new File("./output/test_V3.py");
        CaffeModel caffeModel = CaffeModel.createFromFile(new File(args[0]));
        if (null == caffeModel) throw new FileNotFoundException("Caffe model file not found!");
        TFModel tfModel = new TFModel(caffeModel);
        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(test)));
        appendFile(out, header);
        out.println();
        String funcName = args.length > 1? args[1]: "inception_v1";
        tfModel.generateCode(out, "", funcName);
        out.println();
        out.printf(SNIPPET_CHANGE_IMAGE_SIZE, funcName, tfModel.inputShape[1]);

        appendFile(out, footer);
        out.close();
    }
}
