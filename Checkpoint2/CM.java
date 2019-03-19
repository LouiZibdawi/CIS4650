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
   
class CM {
    public static final boolean SHOW_TREE = true;
    public static final boolean SHOW_TABLE = true;
    public static void main(String argv[]) {
    /* Start the parser */
        try {
            parser p = new parser(new Lexer(new FileReader(argv[0])));
            Absyn result = (Absyn)(p.parse().value);
            if (SHOW_TREE) {
                System.out.println("\nThe abstract syntax tree is:");
                ShowTreeVisitor visitor = new ShowTreeVisitor();
                result.accept(visitor, 0);
            }
            if (SHOW_TABLE) {
                System.out.println("\nEntering the global scope: ");
                SemanticAnalyzer sAnal = new SemanticAnalyzer();
                result.accept(sAnal, 1);
                sAnal.printMap(sAnal.symTable.get(0).entrySet().iterator(), 1);
                System.out.println("Leaving the global scope");
            }
        } catch (Exception e) {
            /* do cleanup here -- possibly rethrow e */
            e.printStackTrace();
        }
    }
}


