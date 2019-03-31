import absyn.*;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public class AssemblyCodeCreator implements AbsynVisitor {
    final static int SPACES = 4;


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

    private void indent( int level ) {
        for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
    }

    public void visit( ExpList expList, int level ) {
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit( AssignExp exp, int level ) {
        indent( level );
        System.out.println( "AssignExp: ");
        level++;
        exp.lhs.accept( this, level );
        exp.rhs.accept( this, level );
    }

    public void visit( IfExp exp, int level ) {
        indent( level );
        System.out.println( "IfExp: " );
        level++;
        exp.test.accept( this, level );
        exp.thenpart.accept( this, level );
        if (exp.elsepart != null )
            exp.elsepart.accept( this, level );
    }

    public void visit( IntExp exp, int level ) {
        indent( level );
        System.out.println( "IntExp: " + exp.value);
    }

    public void visit( VarExp exp, int level ) {
        indent( level );
        System.out.println( "VarExp: ");
        level++;
        exp.name.accept(this, level);
    }

    public void visit( NilExp exp, int level ) {
        indent( level );
        System.out.println( "NilExp: ");
    }

    public void visit( CallExp exp, int level ) {
        indent( level );
        System.out.println( "CallExp: " + exp.func);
        level++;
        ExpList args = exp.args;
        while (args != null) {
            args.head.accept(this, level);
            args = args.tail;
        }
    }

    public void visit( WhileExp exp, int level) {
        indent (level);
        System.out.println( "WhileExp: ");
        level++;
        exp.test.accept(this, level);
        exp.body.accept(this, level);
    }

    public void visit( ReturnExp exp, int level) {
        indent (level);
        System.out.println( "ReturnExp: ");
        level++;
        if (exp.exp != null)
            exp.exp.accept(this, level);
    }

    public void visit ( CompoundExp exp, int level){
        indent (level);
        System.out.println( "CompoundExp: ");
        level++;
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

    public void visit ( FunctionDec exp, int level){
        indent (level);
        System.out.print( "FunctionDec: " + exp.func + " - ");
        if (exp.result.typ == 0)
            System.out.println("INT");
        else if (exp.result.typ == 1)
            System.out.println("VOID");
        level++;
        VarDecList parms = exp.params;
        while (parms != null) {
            parms.head.accept(this, level);
            parms = parms.tail;
        }
        if (exp.body != null){ // check if body is null: handles function prototypes
            exp.body.accept(this, level);
        }
    }

    public void visit ( SimpleDec exp, int level){
        indent (level);
        System.out.print( "SimpleDec: " + exp.name + " - ");
        if (exp.typ.typ == 0)
            System.out.println("INT");
        else if (exp.typ.typ == 1)
            System.out.println("VOID");
    }

    public void visit ( ArrayDec exp, int level){
        indent (level);
        System.out.print( "ArrayDec: " + exp.name + "[");
        if (exp.size != null)
            System.out.print("" + exp.size.value);
        System.out.print("] - ");
        if (exp.typ.typ == 0)
            System.out.println("INT");
        else if (exp.typ.typ == 1) // is this necessary? a "void" array?
            System.out.println("VOID");
    }

    public void visit( DecList expList, int level ) {
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit( VarDecList expList, int level ) {
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit( IndexVar exp, int level ) {
        indent (level);
        System.out.println( "IndexVar: " + exp.name);
        level++;
        exp.index.accept(this, level);
    }

    public void visit( SimpleVar exp, int level ) {
        indent (level);
        System.out.println( "SimpleVar: " + exp.name);
    }

    public void visit( NameTy exp, int level ) {
        indent (level);
        System.out.print( "NameTy: ");
        if (exp.typ == 0)
            System.out.println("INT");
        else if (exp.typ == 1)
            System.out.println("VOID");
    }

    public void visit( OpExp exp, int level ) {
        indent( level );
        System.out.print( "OpExp:" );
        switch( exp.op ) {
            case OpExp.COMPARE:
                System.out.println( " == " );
                break;
            case OpExp.PLUS:
                System.out.println( " + " );
                break;
            case OpExp.MINUS:
                System.out.println( " - " );
                break;
            case OpExp.TIMES:
                System.out.println( " * " );
                break;
            case OpExp.OVER:
                System.out.println( " / " );
                break;
            case OpExp.EQ:
                System.out.println( " = " );
                break;
            case OpExp.LT:
                System.out.println( " < " );
                break;
            case OpExp.GT:
                System.out.println( " > " );
                break;
            case OpExp.GTE:
                System.out.println( " >= " );
                break;
            case OpExp.LTE:
                System.out.println( " <= " );
                break;
            case OpExp.NE:
                System.out.println( " != " );
                break;
            default:
                System.out.println( "Unrecognized operator at line " + exp.row + " and column " + exp.col);
        }
        level++;
        exp.left.accept( this, level );
        exp.right.accept( this, level );
    }


    // taken from the lecture slides
    public void emitBackup (int location){
        if (location > highEmitLoc){
            emitComment("BUG in emitBackup");
        }
        emitLoc = location;
    }

    public void emitComment(String comment){
        writeToFile("*" + comment + "\n");
    }

    // taken from the lecture slides
    public void emitRestore(int level){
        emitLoc = highEmitLoc;
    }

    //Called emitRO in Fei's slides
    public void emitRegisterOperation(String operation, int regDestination, int val1, int  val2,  String comment){
        String generatedString = operation + " " + regDestination + ", " + ", " + val1 + ", " + val2 + " " + comment;
        writeToFile(generatedString);
    }

    // taken from the lecture slides
    public void emitRM_Abs(String op, int r, int a, String c){
        String generatedString = emitLoc + ":  " + op + "  " + r + "," + (a-(emitLoc+1)) + "(" + pc + ")";
        System.out.println(generatedString);
        emitLoc = emitLoc + 1;
        if( TraceCode == 1){
            System.out.println(c);
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
