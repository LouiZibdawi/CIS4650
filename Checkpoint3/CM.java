/*
  Created by: Fei Song
  File Name: Main.java
  To Build: 
  After the scanner, tiny.flex, and the parser, tiny.cup, have been created.
    javac Main.java
  
  To Run: 
    java -classpath /usr/share/java/cup.jar:. Main gcd.tiny

  where gcd.tiny is an test input file for the tiny language.
*/
   
import java.io.*;
import absyn.*;
import java.util.*;

class CM {
    public static boolean SHOW_TREE = false;
    public static boolean SHOW_TABLE = false;
    public static boolean CREATE_ASSEMBLY = false;
    public static String filename = "tempFile.tm";

    private static void printHelpMessage(){
        System.out.println("Required arguments: a .cm file");
        System.out.println("At least one flag must be supplied:");
        System.out.println("\t -a: Shows the abstract syntax tree that is generated.");
        System.out.println("\t -s: Shows the semantic analysis and symbol table that is generated.");
        System.out.println("\t -c: Generates assembly output to be run on the tm simulator.");
        System.out.println("\t -h: Display this help message.");
    }

    //generic file writer to pipe output to a file
    public static void writeToFile(String toWrite, String file) {
        PrintWriter outFP = null;
        try {
            outFP = new PrintWriter(new FileOutputStream(file, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        outFP.printf(toWrite);
        outFP.close();
    }

    public static void main(String argv[]) {
        /* Start the parser */
        int foundFlag = 0;
        int foundFile = 0;
        try {
            for (String arg : argv) {
                if (arg.equals("-a")) {
                    SHOW_TREE = true;
                    foundFlag = 1;
                } else if (arg.equals("-s")) {
                    SHOW_TREE = true;
                    SHOW_TABLE = true;
                    foundFlag = 1;
                } else if (arg.equals("-c")) {
                    SHOW_TREE = true;
                    SHOW_TABLE = true;
                    CREATE_ASSEMBLY = true;
                    foundFlag = 1;
                } else if (arg.equals("-h")) {
                    printHelpMessage();
                    System.exit(0);
                } else if (arg.endsWith(".cm")) {
                    filename = arg;
                    foundFile = 1;
                } else {
                    System.err.println("Error: Invalid argument encountered. See correct usage.");
                    printHelpMessage();
                    System.exit(0);
                }
            }

            if (foundFile == 0 || foundFlag == 0) {
                printHelpMessage();
                System.exit(0);
            }

            parser p = new parser(new Lexer(new FileReader(filename)));
            Absyn result = (Absyn) (p.parse().value);
            if (SHOW_TREE) {
                writeToFile("The abstract syntax tree is: \n", filename.substring(0, filename.indexOf(".")) + ".abs");
                ShowTreeVisitor visitor = new ShowTreeVisitor(filename);
                result.accept(visitor, 0);
            }
            if (SHOW_TABLE) {
                writeToFile("Entering the global scope: \n", filename.substring(0, filename.indexOf(".")) + ".sym");
                SemanticAnalyzer sAnal = new SemanticAnalyzer(filename);
                result.accept(sAnal, 1);
                sAnal.printMap(sAnal.symTable.getLast().entrySet().iterator(), 1);
                writeToFile("Leaving the global scope", filename.substring(0, filename.indexOf(".")) + ".sym");
                sAnal.printUndefined(sAnal.symTable.getLast().entrySet().iterator());
            }
            if (CREATE_ASSEMBLY) {
                AssemblyCodeCreator codeGenerator = new AssemblyCodeCreator(filename);
                result.accept(codeGenerator, 1);
            }
        } catch (Exception e) {
            /* do cleanup here -- possibly rethrow e */
            e.printStackTrace();
        }
    }
}


