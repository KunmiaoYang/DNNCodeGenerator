******************** Install ********************
1. Java 1.7 or higher:
    Installation instruction: https://www.java.com/en/download/help/download_options.xml

2. Maven 3.0.5 or higher:
    Installation instruction: https://maven.apache.org/install.html

******************** Compile ********************
1. Compile source code to class files:
    At the root of this project, use the following command to compile:
    mvn compile

2. Generate jar package:
    To generate jar package, you need to compile the code first, or the package would be
    empty jar file.
    At the root of this project, use the following command to generate:
    mvn jar:jar
    The jar file would be generated under the target

******************** Test ********************

******************** Execute ********************
To execute the program you need to generate the jar package first. Then you need to prepare
the prototxt file.
1. Simple:
    use the following command to run:
    java -cp <DNNCodeGenerator.jar file path> org.ncsu.dnn.SimpleCodeGenerator <input file path> [output file path]

2. Multiplexing:
    use the following command to run:
    java -cp <DNNCodeGenerator.jar file path> org.ncsu.dnn.MultiCodeGenerator <input file path> [output file path]

Notations:
<DNNCodeGenerator.jar file path>:   the jar file path generated.
<input file path>:                  the input prototxt file path
[output file path]:                 (optional) the output python file path. If not provided,
                                    the program would use the input file name with an
                                    additional suffix (_simple or _multiplexing), and it
                                    would be generated at run path.
******************** Output ********************
******************** Limitation ********************
