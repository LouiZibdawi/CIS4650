import absyn.*;
import java.io.*;
import java.util.*;

public class AssemblyCodeCreator implements AbsynVisitor {
    final static int SPACES = 4;

    public static LinkedList<HashMap<String, SymItem>> symTable;
    private static String tempParams = "";
    private static String currFunc = "";


    public static int emitLoc = 0;
    public static int highEmitLoc = 0;
    public static int pc = 7;
    public static int gp = 6;
    public static int fp = 5;
    public static int ac = 0;
    public static int globalOffset = 0;
    public static int entry = 0;
    public static int TraceCode = 0;
    public String filename = "tempFile.txt";

    private void indent(int level) {
        for( int i = 0; i < level * SPACES; i++ ) System.out.print(" ");
    }

    public void visit(ExpList expList, int level) {
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit(AssignExp exp, int level) {
        exp.lhs.accept(this, level);
        exp.rhs.accept(this, level);
    }

    public void visit(IfExp exp, int level) {
        indent(level);
        emitComment("Entering a new if block: ");

        exp.test.accept(this, level);
        exp.thenpart.accept(this, level);

        level--;
        indent(level);
        emitComment("Leaving the if block");

        if (exp.elsepart != null && !(exp.elsepart instanceof NilExp)) {
            indent(level);
            emitComment("Entering a new else block: ");
            level++;

            exp.elsepart.accept(this, level);

            level--;
            indent(level);
            emitComment("Leaving the else block");
        }
    }

    public void visit(IntExp exp, int level) {

    }

    public void visit(VarExp exp, int level) {
        exp.name.accept(this, level);
    }

    public void visit(NilExp exp, int level) {

    }

    public void visit(CallExp exp, int level) {
        ExpList args = exp.args;
        while (args != null) {
            args.head.accept(this, level);
            args = args.tail;
        }
    }

    public void visit(WhileExp exp, int level) {
        indent(level);
        emitComment("Entering a new while block: ");
        level++;

        exp.test.accept(this, level);
        exp.body.accept(this, level);

        level--;
        indent(level);
        emitComment("Leaving the while block");
    }

    public void visit(ReturnExp exp, int level) {
        if (exp.exp != null) {
            exp.exp.accept(this, level);
        }
    }

    public void visit(CompoundExp exp, int level){
        VarDecList decs = exp.decs;
        while (decs != null) {
            decs.head.accept(this, level);
            decs = decs.tail;
        }
        ExpList exps = exp.exps;
        while (exps != null) {
            exps.head.accept(this, level);
            exps = exps.tail;
        }
    }

    public void visit(FunctionDec exp, int level){
        SymItem sym = new SymItem(exp.func, exp.result.typ, level, "");
        if (exp.body == null) { // Check if it is a prototype
            sym.level = -1; // Set the level to -1 to identify it as a function prototype
            level++;

            VarDecList parms = exp.params;
            tempParams = ""; // clear before using
            while (parms != null) {
                parms.head.accept(this, level);
                parms = parms.tail;
            }
            sym.params += tempParams;

            level--;
        } else { // if it is a function definition
                indent(level);
                emitComment("Entering the scope for function " + exp.func + ": ");
                currFunc = exp.func;
                level++;

                VarDecList parms = exp.params;
                tempParams = ""; // clear before using
                while (parms != null) {
                    parms.head.accept(this, level);
                    parms = parms.tail;
                }
                sym.params = tempParams;

                exp.body.accept(this, level);
                level--;
                indent(level);
                emitComment("Leaving the function scope");
                currFunc = "";
        }
    }

    public void visit(SimpleDec exp, int level){
        SymItem sym = new SymItem(exp.name, exp.typ.typ, level, "");
    }

    public void visit(ArrayDec exp, int level){
    }

    public void visit(DecList expList, int level) {
        emitComment("Standard prelude: ");
        emitRegisterManipulation("LD", gp, 0, ac, "load gp with maxaddress");
        emitRegisterManipulation("LDA", fp, 0, gp, "copy to gp to fp");
        emitRegisterManipulation("ST", 0, 0, 0, "clear location 0");


        int savedLoc = emitSkip(1);

        /* Generate input function */
        emitComment("Jump around i/o routines here");
        emitComment("code for input routine");
        emitRegisterManipulation("ST", 0, -1, fp, "store return");
        emitRegisterOperation("IN", 0, 0, 0, "input");
        emitRegisterManipulation("LD", pc, -1, fp, "return to caller");

        /* Generate output function */
        emitComment("code for output routine");
        emitRegisterManipulation("ST", 0, -1, fp, "store return");
        emitRegisterManipulation("LD", 0, -2, fp, "load output value");
        emitRegisterOperation("OUT", 0, 0, 0, "output");
        emitRegisterManipulation("LD", 7, -1, fp, "return to caller");
        int savedLoc2 = emitSkip(0);


    /* Set emitLoc to previously stored value
    Jump around I/O functions*/
        emitBackup(savedLoc);
        emitRM_Abs("LDA", pc, savedLoc2, "jump around i/o code");
        emitRestore();
        emitComment("End of standard prelude");

        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit(VarDecList expList, int level) {
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit(IndexVar exp, int level) {
        exp.index.accept(this, level);
    }

    public void visit(SimpleVar exp, int level) {
    }

    public void visit(NameTy exp, int level) {

    }

    public void visit(OpExp exp, int level) {
        emitComment("OpExp Found");
        switch (exp.op){
            case OpExp.PLUS:
                emitRegisterOperation("ADD",ac,1,ac, "Operation +");
                break;
            case OpExp.MINUS:
                emitRegisterOperation("SUB",ac,1,ac, "Operation -");
                break;
            case OpExp.TIMES:
                emitRegisterOperation("MULS",ac,1,ac, "Operation *");
                break;
            case OpExp.OVER:
                emitRegisterOperation("DIVS",ac,1,ac, "Operation /");
                break;
            case OpExp.EQ:
                emitRegisterOperation("EQU", ac, 1, ac, "Operation =" );
                break;
            case OpExp.LT:
                emitRegisterOperation("SUB", ac, 1, ac, "Set up comparison" );
                emitRegisterManipulation("JLT", ac, 2, ac, "Jump if less than" );
                emitRegisterManipulation("LDC", ac, 0, 0, "Not equal case, fall through assembly logic");
                emitRegisterManipulation("LDA", pc, 1, pc, "jump 'past' the true case");
                emitRegisterManipulation("LDC", ac, 1, 0, "true case");
                break;
            case OpExp.GT:
                emitRegisterOperation("SUB", ac, 1, ac, "Set up comparison" );
                emitRegisterManipulation("JGT", ac, 2, ac, "Jump if greater than" );
                emitRegisterManipulation("LDC", ac, 0, 0, "Not equal case, fall through assembly logic");
                emitRegisterManipulation("LDA", pc, 1, pc, "jump 'past' the true case");
                emitRegisterManipulation("LDC", ac, 1, 0, "true case");
                break;
            case OpExp.GTE:
                emitRegisterOperation("SUB", ac, 1, ac, "Set up comparison" );
                emitRegisterManipulation("JGE", ac, 2, ac, "Jump if greater or equal" );
                emitRegisterManipulation("LDC", ac, 0, 0, "Not equal case, fall through assembly logic");
                emitRegisterManipulation("LDA", pc, 1, pc, "jump 'past' the true case");
                emitRegisterManipulation("LDC", ac, 1, 0, "true case");
                break;
            case OpExp.LTE:
                emitRegisterOperation("SUB", ac, 1, ac, "Set up comparison" );
                emitRegisterManipulation("JLE", ac, 2, ac, "Jump if less than or equal" );
                emitRegisterManipulation("LDC", ac, 0, 0, "Not equal case, fall through assembly logic");
                emitRegisterManipulation("LDA", pc, 1, pc, "jump 'past' the true case");
                emitRegisterManipulation("LDC", ac, 1, 0, "true case");
                break;
            case OpExp.NE:
                emitRegisterOperation("SUB", ac, 1, ac, "Set up comparison" );
                emitRegisterManipulation("JNE", ac, 2, ac, "Jump if not equal" );
                emitRegisterManipulation("LDC", ac, 0, 0, "Not equal case, fall through assembly logic");
                emitRegisterManipulation("LDA", pc, 1, pc, "jump 'past' the true case");
                emitRegisterManipulation("LDC", ac, 1, 0, "true case");
                break;
            case OpExp.COMPARE:
                emitRegisterOperation("SUB", ac, 1, ac, "Set up comparison" );
                emitRegisterManipulation("JEQ", ac, 2, ac, "Jump if equal" );
                emitRegisterManipulation("LDC", ac, 0, 0, "Not equal case, fall through assembly logic");
                emitRegisterManipulation("LDA", pc, 1, pc, "jump 'past' the true case");
                emitRegisterManipulation("LDC", ac, 1, 0, "true case");
                break;
            default:
                System.err.println("Error: unsupported operation found");
        }

        exp.left.accept(this, level);
        exp.right.accept(this, level);
    }

    public void printMap(Iterator i, int level) {
        while (i.hasNext()) {
            SymItem symbol = (SymItem) ((Map.Entry) i.next()).getValue();
            indent(level);
            System.out.print(symbol.name + ": ");
            if (!symbol.params.isEmpty()) {
                String[] tokens = symbol.params.split(" ");
                System.out.print("( ");
                for (String s : tokens) {
                    if (s.equals("0"))
                        emitComment("int ");
                    else if (s.equals("1"))
                        emitComment("void ");
                }
                System.out.print(") -> ");
            }
            if (symbol.type == 0)
                emitComment("int");
            else if (symbol.type == 1)
                emitComment("void");
        }
    }

    // taken from the lecture slides
    public void emitBackup (int location){
        if (location > highEmitLoc){
            emitComment("BUG in emitBackup");
        }
        emitLoc = location;
    }

    public void emitComment(String comment){
        writeToFile("* " + comment + "\n");
    }

    // taken from the lecture slides
    public void emitRestore(){
        emitLoc = highEmitLoc;
    }

    //Called emitRO in Fei's slides
    public void emitRegisterOperation(String operation, int regDestination, int val1, int  val2,  String comment){
        String generatedString = "  " + emitLoc + ":  " + operation + "\t" + regDestination + "," + val1 + "," + val2 + " \t" + comment + "\n";
        emitLoc= emitLoc + 1;
        writeToFile(generatedString);
    }
    //Called emitRM in Fei's slides
    public void emitRegisterManipulation(String operation, int regDestination, int offset, int val1, String comment){
        String generatedString = "  " + emitLoc + ":  " + operation + "\t" + regDestination + "," + offset + ",(" + val1 + ") \t" + comment + "\n";
        writeToFile(generatedString);
        emitLoc = emitLoc + 1;
        if (highEmitLoc < emitLoc){
            highEmitLoc = emitLoc;
        }
    }

    // taken from the lecture slides
    public void emitRM_Abs(String op, int r, int a, String c){
        String generatedString = "  " + emitLoc + ":  " + op + "\t" + r + "," + (a-(emitLoc+1)) + "(" + pc + ")\n";
        writeToFile(generatedString);
        emitLoc = emitLoc + 1;
        if( TraceCode == 1){
            writeToFile(c);
        }
        if( highEmitLoc< emitLoc){
            highEmitLoc= emitLoc;
        }
    }

    // taken from the lecture slides
    //calculates skip distance based on input, highEmitLoc, and the highEmitLoc
    public int emitSkip (int distance){
        int i = emitLoc;
        emitLoc += distance;
        if (highEmitLoc < emitLoc){
            highEmitLoc = emitLoc;
        }
        return i;
    }

    //generic file writer to pipe output to a file
    public void writeToFile(String toWrite){
        PrintWriter outFP = null;
        try{
            outFP = new PrintWriter(new FileOutputStream(this.filename, true));
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        outFP.printf(toWrite);
        outFP.close();
    }

}
