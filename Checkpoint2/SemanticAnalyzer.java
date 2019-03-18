import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {
    final static int SPACES = 4;

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
        exp.body.accept(this, level);
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
}
