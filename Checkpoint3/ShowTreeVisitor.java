import absyn.*;
import java.util.*;
import java.io.*;
public class ShowTreeVisitor implements AbsynVisitor {
    final static int SPACES = 4;
    public static String filename = "tempFile.tm";

    private void indent( int level ) {
        for( int i = 0; i < level * SPACES; i++ ) writeToFile(" ");
    }

    public ShowTreeVisitor(String inputFile) {
        this.filename = inputFile.substring(0, inputFile.indexOf(".")) + ".abs";
    }

    public void visit( ExpList expList, int level ) {
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit( AssignExp exp, int level ) {
        indent( level );
        writeToFile("AssignExp:\n");
        level++;
        exp.lhs.accept( this, level );
        exp.rhs.accept( this, level );
    }

    public void visit( IfExp exp, int level ) {
        indent( level );
        writeToFile("IfExp: \n");
        level++;
        exp.test.accept( this, level );
        exp.thenpart.accept( this, level );
        if (exp.elsepart != null )
            exp.elsepart.accept( this, level );
    }

    public void visit( IntExp exp, int level ) {
        indent( level );
        writeToFile("IntExp: " + exp.value + "\n");
    }

    public void visit( VarExp exp, int level ) {
        indent( level );
        writeToFile("VarExp: \n");
        level++;
        exp.name.accept(this, level);
    }

    public void visit( NilExp exp, int level ) {
        indent( level );
        writeToFile("NilExp: \n");
    }

    public void visit( CallExp exp, int level ) {
        indent( level );
        writeToFile("CallExp: " + exp.func + " \n");
        level++;
        ExpList args = exp.args;
        while (args != null) {
            args.head.accept(this, level);
            args = args.tail;
        }
    }

    public void visit( WhileExp exp, int level) {
        indent (level);
        writeToFile("WhileExp:\n");
        level++;
        exp.test.accept(this, level);
        exp.body.accept(this, level);
    }

    public void visit( ReturnExp exp, int level) {
        indent (level);
        writeToFile("ReturnExp: \n");
        level++;
        if (exp.exp != null)
            exp.exp.accept(this, level);
    }

    public void visit ( CompoundExp exp, int level){
        indent (level);
        writeToFile("CompoundExp: \n");
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
        writeToFile("FunctionDec: " + exp.func + " - ");
        if (exp.result.typ == 0)
            writeToFile("INT\n");
        else if (exp.result.typ == 1)
            writeToFile("VOID\n");
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
        writeToFile("SimpleDec: " + exp.name + " - ");
        if (exp.typ.typ == 0)
            writeToFile("INT\n");
        else if (exp.typ.typ == 1)
            writeToFile("VOID\n");
    }

    public void visit ( ArrayDec exp, int level){
        indent (level);
        writeToFile("ArrayDec: " + exp.name + "[");
        if (exp.size != null)
            writeToFile("" + exp.size.value);
        writeToFile("] - ");
        if (exp.typ.typ == 0)
            writeToFile("INT\n");
        else if (exp.typ.typ == 1) // is this necessary? a "void" array?
            writeToFile("VOID\n");
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
        writeToFile("IndexVar: " + exp.name + "\n");
        level++;
        exp.index.accept(this, level);
    }

    public void visit( SimpleVar exp, int level ) {
        indent (level);
        writeToFile("SimpleVar: " + exp.name + "\n");
    }

    public void visit( NameTy exp, int level ) {
        indent (level);
        writeToFile("NameTy: ");
        if (exp.typ == 0)
            writeToFile("INT\n");
        else if (exp.typ == 1)
            writeToFile("VOID\n");
    }

    public void visit( OpExp exp, int level ) {
        indent( level );
        writeToFile("OpExp:");
        switch( exp.op ) {
            case OpExp.COMPARE:
                writeToFile(" == \n");
                break;
            case OpExp.PLUS:
                writeToFile(" + \n");
                break;
            case OpExp.MINUS:
                writeToFile(" - \n");
                break;
            case OpExp.TIMES:
                writeToFile(" * \n");
                break;
            case OpExp.OVER:
                writeToFile(" / \n");
                break;
            case OpExp.EQ:
                writeToFile(" = \n");
                break;
            case OpExp.LT:
                writeToFile(" < \n");
                break;
            case OpExp.GT:
                writeToFile(" > \n");
                break;
            case OpExp.GTE:
                writeToFile(" >= \n");
                break;
            case OpExp.LTE:
                writeToFile(" <= \n");
                break;
            case OpExp.NE:
                writeToFile(" != \n");
                break;
            default:
                writeToFile("Unrecognized operator at line " + exp.row + " and column " + exp.col + "\n");
        }
        level++;
        exp.left.accept( this, level );
        exp.right.accept( this, level );
    }

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