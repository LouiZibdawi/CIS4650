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
    private static final int pc = 7;
    private static final int gp = 6;
    private static final int fp = 5;
    private static final int ac = 0;
    private static final int retFO = -1; // unused
    private static final int initFO = -2; // unused
    public static int globalOffset = 0;
    public static int offset = 0;
    public static int entry = 0;
    public static int TraceCode = 1;
    public static String filename = "tempFile.tm";

    private void indent(int level) {
        for( int i = 0; i < level * SPACES; i++ ) System.out.print(" ");
    }

    public AssemblyCodeCreator(String inputFile) {
        this.symTable = new LinkedList<HashMap<String, SymItem>>();
        this.symTable.add(new HashMap<String, SymItem>());
        this.filename = inputFile.substring(0, inputFile.indexOf(".")) + ".tm";
        emitComment("C-Minus Compilation to TM Code");
        emitComment("File: " + this.filename);
    }


    public void visit(ExpList expList, int level) {
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit(AssignExp exp, int level) {
        emitComment("-> op");
        exp.lhs.accept(this, 0);
        emitRegisterMemory("ST", ac, offset--, fp, "op: push left");
        exp.rhs.accept(this, level);
        emitRegisterMemory("LD", 1, ++offset, fp, "op: load left");
        emitRegisterMemory("ST", ac, ac, 1, "assign: store value");
        emitComment("<- op");
    }

    public void visit(IfExp exp, int level) {
        indent(level);
//        emitComment("Entering a new if block: ");
        this.symTable.addFirst(new HashMap<String, SymItem>());
        level++;

        exp.test.accept(this, level);
        exp.thenpart.accept(this, level);

        printMap(this.symTable.getFirst().entrySet().iterator(), level);
        level--;
        this.symTable.removeFirst();
        indent(level);
//        emitComment("Leaving the if block");

        if (exp.elsepart != null && !(exp.elsepart instanceof NilExp)) {
            indent(level);
//            emitComment("Entering a new else block: ");
            this.symTable.addFirst(new HashMap<String, SymItem>());
            level++;

            exp.elsepart.accept(this, level);

            printMap(this.symTable.getFirst().entrySet().iterator(), level);
            level--;
            this.symTable.removeFirst();
            indent(level);
//            emitComment("Leaving the else block");
        }
    }

    public void visit(IntExp exp, int level) {
        emitComment("-> constant");
        emitRegisterMemory("LDC", ac, exp.value, ac, "load const");
        emitComment("<- constant");
    }

    public void visit(VarExp exp, int level) {
        exp.name.accept(this, 1);
    }

    public void visit(NilExp exp, int level) {

    }

    public void visit(CallExp exp, int level) {
        emitComment("-> call of function: " + exp.func);
        ExpList args = exp.args;
        while (args != null) {
            args.head.accept(this, level);
            args = args.tail;
            emitRegisterMemory("ST", ac, offset, fp, "store arg val"); // 57:     ST  0,-6(5) 	store arg val ???
        }
        emitRegisterMemory("ST", fp, offset, fp, "push ofp");
        emitRegisterMemory("LDA", fp, offset, fp, "push frame");
        emitRegisterMemory("LDA", ac, 1, pc, "load ac with ret ptr");
        if (exp.func.equals("input")) emitRM_Abs("LDA", pc, 4, "jump to fun loc"); // doesn't work
        else if (exp.func.equals("output")) emitRM_Abs("LDA", pc, 7, "jump to fun loc");
        else emitRM_Abs("LDA", pc, entry, "jump to fun loc");
        emitRegisterMemory("LD", fp, ac, fp, "pop frame");
        if (this.symTable.getLast().containsKey(exp.func)) {
            if (((SymItem) this.symTable.getLast().get(exp.func)).level == -1)
                ((SymItem) this.symTable.getLast().get(exp.func)).level = -2; // mark as used prototype
        }
        emitComment("<- call");
    }

    public void visit(WhileExp exp, int level) {
//        indent(level);
        this.symTable.addFirst(new HashMap<String, SymItem>());
        level++;

        emitComment("-> while");
        emitComment("while: jump after body comes back here");
        exp.test.accept(this, level);
        emitComment("while: jump to end belongs here");
        int savedLoc = emitSkip(1); // continue here
        exp.body.accept(this, level);
        int savedLoc2 = emitSkip(0);
        emitBackup(savedLoc);
        emitRM_Abs("LDA", pc, savedLoc2, "while: absolute jmp to test");
        emitRestore();
        emitRegisterMemory("JEQ", ac, emitLoc--, pc, "while: jmp to end");
        emitComment("<- while");

//        printMap(this.symTable.getFirst().entrySet().iterator(), level);
        level--;
        this.symTable.removeFirst();
//        indent(level);
    }

    public void visit(ReturnExp exp, int level) {
        emitComment("-> return");
        if (exp.exp != null) {
            exp.exp.accept(this, level);
        }
        emitComment("<- return");
    }

    public void visit(CompoundExp exp, int level){
        emitComment("-> compound statement");
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
        emitComment("<- compound statement");
    }

    public void visit(FunctionDec exp, int level){
        offset = -2;
        SymItem sym = new SymItem(exp.func, exp.result.typ, level, "");
        if (exp.body == null) { // Check if it is a prototype
            sym.level = -1; // Set the level to -1 to identify it as a function prototype
            this.symTable.addFirst(new HashMap<String, SymItem>()); // temp storage for params
            level++;

            VarDecList parms = exp.params;
            tempParams = ""; // clear before using
            while (parms != null) {
                parms.head.accept(this, level);
                parms = parms.tail;
            }
            sym.params += tempParams;

            level--;
            this.symTable.removeFirst(); // delete temp storage; don't need to keep
            if (!this.symTable.getLast().containsKey(exp.func))
                this.symTable.getLast().put(exp.func, sym);
            else
                System.err.printf("Error: Already declared function: %s at line %d\n", exp.func, exp.pos+1);
        } else { // if it is a function definition
            if (!this.symTable.getLast().containsKey(exp.func) ||
                    (this.symTable.getLast().containsKey(exp.func) && ((SymItem) this.symTable.getLast().get(exp.func)).level < 0)) {
                indent(level);

                emitComment("processing function: " + exp.func);
                emitComment("jump around function body here");
                //TODO store value

                int tempLocation = emitSkip(1);
                emitRegisterMemory("ST", fp, -1, fp, "store return");
                entry = emitLoc-1;

                currFunc = exp.func;
                this.symTable.addFirst(new HashMap<String, SymItem>());
                level++;

                VarDecList parms = exp.params;
                tempParams = ""; // clear before using
                while (parms != null) {
                    parms.head.accept(this, level);
                    parms = parms.tail;
                }
                sym.params = tempParams;
                this.symTable.getLast().put(exp.func, sym);

                exp.body.accept(this, level);
                printMap(this.symTable.getFirst().entrySet().iterator(), level);
                level--;
                this.symTable.removeFirst();
                indent(level);
                emitComment("Leaving the function scope");
                currFunc = "";
            } else
                System.err.printf("Error: Already declared function: %s at line %d\n", exp.func, exp.pos+1);
        }
    }

    public void visit(SimpleDec exp, int level){
        SymItem sym = new SymItem(exp.name, exp.typ.typ, level, "", offset--);
        if (!this.symTable.getFirst().containsKey(exp.name)) {
            this.symTable.getFirst().put(exp.name, sym);
            tempParams += exp.typ.typ + " ";
            emitComment("processing local var: " + exp.name);
        }
    }

    public void visit(ArrayDec exp, int level) {
        String name = "";
        name += exp.name + "[";
        if (exp.size != null)
            name += exp.size.value + "";
        name += "]";
        SymItem sym = new SymItem(name, exp.typ.typ, level, "", offset--);
        if (!this.symTable.getFirst().containsKey(exp.name)) {
            this.symTable.getFirst().put(exp.name, sym);
            tempParams += exp.typ.typ + " ";
            emitComment("processing local var: " + exp.name);
        }
    }

    public void visit(DecList expList, int level) {
        emitComment("Standard prelude: ");
        emitRegisterMemory("LD", gp, 0, ac, "load gp with maxaddress");
        emitRegisterMemory("LDA", fp, 0, gp, "copy to gp to fp");
        emitRegisterMemory("ST", 0, 0, 0, "clear location 0");
        int savedLoc = emitSkip(1);

        /* Generate input function */
        emitComment("Jump around i/o routines here");
        emitComment("code for input routine");
        emitRegisterMemory("ST", 0, -1, fp, "store return");
        emitRegisterOnly("IN", 0, 0, 0, "input");
        emitRegisterMemory("LD", pc, -1, fp, "return to caller");

        /* Generate output function */
        emitComment("code for output routine");
        emitRegisterMemory("ST", 0, -1, fp, "store return");
        emitRegisterMemory("LD", 0, -2, fp, "load output value");
        emitRegisterOnly("OUT", 0, 0, 0, "output");
        emitRegisterMemory("LD", 7, -1, fp, "return to caller");
        int savedLoc2 = emitSkip(0);

        /* Set emitLoc to previously stored value
        Jump around I/O functions*/
        emitBackup(savedLoc);
        emitRM_Abs("LDA", pc, savedLoc2, "jump around i/o code");
        emitRestore();
        emitComment("End of standard prelude.");

        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }

        // emitRegisterMemory("ST", fp, globalOffset+ofpFO, fp, "push ofp"); // 64:     ST  5,0(5) 	push ofp?
        emitRegisterMemory("LDA", fp, globalOffset, fp, "push frame" );
        emitRegisterMemory("LDA", ac, 1, pc, "load ac with ret ptr" );
        emitRM_Abs("LDA", pc, entry, "jump to main loc" );
        emitRegisterMemory("LD", fp, 0, fp, "pop frame" );
        emitComment("End of execution.");
        emitRegisterOnly("HALT", 0, 0, 0, "" );

    }

    public void visit(VarDecList expList, int level) {
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit(IndexVar exp, int isParam) { // TODO: check if param or not and proceed accordingly
        emitComment("-> subs");
        exp.index.accept(this, isParam);
        emitComment("<- subs");
    }

    public void visit(SimpleVar exp, int isParam) {
        int tempOffset = 10000000;
        if (symExists(exp.name)) tempOffset = findOffset(exp.name);
        emitComment("-> id");
        emitComment("looking up id: " + exp.name);
        if (isParam == 0) emitRegisterMemory("LDA", ac, tempOffset, fp, "load id address");
        else emitRegisterMemory("LD", ac, tempOffset, fp, "load id value");
        emitComment("<- id");
    }

    public void visit(NameTy exp, int level) {

    }

    public void visit(OpExp exp, int level) {
        emitComment("-> op");
        exp.left.accept(this, level);
        emitRegisterMemory("ST", ac, offset--, fp, "op: push left");
        exp.right.accept(this, level);
        emitRegisterMemory("LD", 1, ++offset, fp, "op: load left");
        switch (exp.op){
            case OpExp.PLUS:
                emitRegisterOnly("ADD",ac,1,ac, "op +");
                break;
            case OpExp.MINUS:
                emitRegisterOnly("SUB",ac,1,ac, "op -");
                break;
            case OpExp.TIMES:
                emitRegisterOnly("MUL",ac,1,ac, "op *");
                break;
            case OpExp.OVER:
                emitRegisterOnly("DIV",ac,1,ac, "op /");
                break;
//            case OpExp.EQ:
//                emitRegisterOnly("EQU", ac, 1, ac, "op =" );
//                break;
            case OpExp.LT:
                emitRegisterOnly("SUB", ac, 1, ac, "op <");
                emitRegisterMemory("JLT", ac, 2, pc, "br if true");
                emitRegisterMemory("LDC", ac, ac, ac, "false case");
                emitRegisterMemory("LDA", pc, 1, pc, "unconditional jmp");
                emitRegisterMemory("LDC", ac, 1, ac, "true case");
                break;
            case OpExp.GT:
                emitRegisterOnly("SUB", ac, 1, ac, "op >");
                emitRegisterMemory("JGT", ac, 2, pc, "br if true");
                emitRegisterMemory("LDC", ac, ac, ac, "false case");
                emitRegisterMemory("LDA", pc, 1, pc, "unconditional jmp");
                emitRegisterMemory("LDC", ac, 1, ac, "true case");
                break;
            case OpExp.GTE:
                emitRegisterOnly("SUB", ac, 1, ac, "op >=");
                emitRegisterMemory("JGE", ac, 2, pc, "br if true");
                emitRegisterMemory("LDC", ac, ac, ac, "false case");
                emitRegisterMemory("LDA", pc, 1, pc, "unconditional jmp");
                emitRegisterMemory("LDC", ac, 1, ac, "true case");
                break;
            case OpExp.LTE:
                emitRegisterOnly("SUB", ac, 1, ac, "op <=");
                emitRegisterMemory("JLE", ac, 2, pc, "br if true");
                emitRegisterMemory("LDC", ac, ac, ac, "false case");
                emitRegisterMemory("LDA", pc, 1, pc, "unconditional jmp");
                emitRegisterMemory("LDC", ac, 1, ac, "true case");
                break;
            case OpExp.NE:
                emitRegisterOnly("SUB", ac, 1, ac, "op !=");
                emitRegisterMemory("JNE", ac, 2, pc, "br if true");
                emitRegisterMemory("LDC", ac, ac, ac, "false case");
                emitRegisterMemory("LDA", pc, 1, pc, "unconditional jmp");
                emitRegisterMemory("LDC", ac, 1, ac, "true case");
                break;
            case OpExp.COMPARE:
                emitRegisterOnly("SUB", ac, 1, ac, "op ==");
                emitRegisterMemory("JEQ", ac, 2, pc, "br if true");
                emitRegisterMemory("LDC", ac, ac, ac, "false case");
                emitRegisterMemory("LDA", pc, 1, pc, "unconditional jmp");
                emitRegisterMemory("LDC", ac, 1, ac, "true case");
                break;
            default:
                System.err.println("Error: unsupported operation found");
        }
        emitComment("<- op");
    }


    public void printMap(Iterator i, int level) {
        while (i.hasNext()) {
            SymItem symbol = (SymItem) ((Map.Entry) i.next()).getValue();
            indent(level);
            emitComment("processing local var: " + symbol.name);
            System.out.print(symbol.name + ": ");
            if (!symbol.params.isEmpty()) {
                String[] tokens = symbol.params.split(" ");

                System.out.print("( ");
                for (String s : tokens) {
                    if (s.equals("0"))
                        System.out.print("int ");
                    else if (s.equals("1"))
                        System.out.print("void ");
                }
                System.out.print(") -> ");
            }
            if (symbol.type == 0)
                System.out.println("int");
            else if (symbol.type == 1)
                System.out.println("void");
        }
    }

    public boolean symExists(String name) {
        if (this.symTable.size() != 0) {
            for (int i = 0; i < this.symTable.size(); i++) {
                if (this.symTable.get(i).containsKey(name))
                    return true;
            }
        }
        return false;
    }

    public int findOffset(String name) {
        if (this.symTable.size() != 0) {
            for (int i = 0; i < this.symTable.size(); i++) {
                if (this.symTable.get(i).containsKey(name))
                    return ((SymItem) this.symTable.get(i).get(name)).offset;
            }
        }
        return 10000000;
    }

    // taken from the lecture slides
    public void emitBackup (int location) {
        if (location > highEmitLoc) emitComment("BUG in emitBackup");
        emitLoc = location;
    }

    public void emitComment(String comment) {
        writeToFile("* " + comment + "\n");
    }

    // taken from the lecture slides
    public void emitRestore() {
        emitLoc = highEmitLoc;
    }

    //Called emitRO in Fei's slides
    public void emitRegisterOnly(String operation, int regDestination, int val1, int  val2,  String comment) {
        String generatedString = "\t" + emitLoc + ":\t" + operation + "\t" + regDestination + "," + val1 + "," + val2 + " \t" + comment + "\n";
        writeToFile(generatedString);
        emitLoc++;
    }
    //Called emitRM in Fei's slides
    public void emitRegisterMemory(String operation, int regDestination, int offset, int val1, String comment) {
        String generatedString = "\t" + emitLoc + ":\t" + operation + "\t" + regDestination + "," + offset + "(" + val1 + ") \t" + comment + "\n";
        writeToFile(generatedString);
        emitLoc++;
        if (highEmitLoc < emitLoc) highEmitLoc = emitLoc;
    }

    // taken from the lecture slides
    public void emitRM_Abs(String op, int r, int a, String c) {
        String generatedString = "\t" + emitLoc + ":\t" + op + "\t" + r + "," + (a-(emitLoc+1)) + "(" + pc + ") \t";
        writeToFile(generatedString);
        emitLoc++;
        if ( TraceCode == 1) writeToFile(c + "\n");
        else writeToFile("\n");
        if (highEmitLoc < emitLoc) highEmitLoc = emitLoc;
    }

    // taken from the lecture slides
    //calculates skip distance based on input, highEmitLoc, and the highEmitLoc
    public int emitSkip (int distance) {
        int i = emitLoc;
        emitLoc += distance;
        if (highEmitLoc < emitLoc) highEmitLoc = emitLoc;
        return i;
    }

    //generic file writer to pipe output to a file
    public void writeToFile(String toWrite) {
        PrintWriter outFP = null;
        try {
            outFP = new PrintWriter(new FileOutputStream(this.filename, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        outFP.printf(toWrite);
        outFP.close();
    }

}
