package org.ncsu.dnn.tf;

import org.ncsu.dnn.caffe.CaffeModel;

import java.io.*;
import java.util.ResourceBundle;

public class CodeGenerator {
    public static final ResourceBundle SNIPPETS;
    public static final String INDENT_STRING;
    public static final String PY_WITH_SCOPE;
    static {
        SNIPPETS = ResourceBundle.getBundle("org.ncsu.dnn.tf.template.snippets");
        INDENT_STRING = SNIPPETS.getString("indent");
        PY_WITH_SCOPE = SNIPPETS.getString("py.with.scope");
    }
    public static void appendFile(PrintStream out, File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for (String line = reader.readLine(); null != line; line = reader.readLine()) {
                out.println(line);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateWithScope(PrintStream out, String indentation, String func, String scope) {
        out.printf(PY_WITH_SCOPE, indentation, func, scope);
        return indentation + CodeGenerator.INDENT_STRING;
    }

    public static void main(String[] args) throws FileNotFoundException {
        File header = new File(CodeGenerator.class.getResource("template/simpleHeader.txt").getFile());
        File footer = new File(CodeGenerator.class.getResource("template/simpleFooter.txt").getFile());
        File test = new File("./output/test.py");
        CaffeModel caffeModel = CaffeModel.createFromFile(new File(args[0]));
        if (null == caffeModel) throw new FileNotFoundException("Caffe model file not found!");
        TFModel tfModel = new TFModel(caffeModel);
        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(test)));
        appendFile(out, header);
        out.println();
        tfModel.generateCode(System.out, "");
        out.println();
        appendFile(out, footer);
        out.close();
    }
}
