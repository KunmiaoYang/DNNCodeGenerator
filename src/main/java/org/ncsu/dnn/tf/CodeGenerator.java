package org.ncsu.dnn.tf;

import java.io.*;

public class CodeGenerator {
    private void appendFile(PrintStream out, File file) {
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
        File header = new File(CodeGenerator.class.getResource("template/simpleHeader.txt").getFile());
        File footer = new File(CodeGenerator.class.getResource("template/simpleFooter.txt").getFile());
        File test = new File("./output/test.py");
        CodeGenerator codeGenerator = new CodeGenerator();
        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(test)));
        codeGenerator.appendFile(out, header);
        codeGenerator.appendFile(out, footer);
        out.close();
    }
}
