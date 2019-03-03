import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

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
        System.out.println( "AssignExp:" );
        level++;
        exp.lhs.accept( this, level );
        exp.rhs.accept( this, level );
    }

    public void visit( IfExp exp, int level ) {
        indent( level );
        System.out.println( "IfExp:" );
        level++;
        exp.test.accept( this, level );
        exp.thenpart.accept( this, level );
        if (exp.elsepart != null )
            exp.elsepart.accept( this, level );
    }

    public void visit( IntExp exp, int level ) {
        indent( level );
        System.out.println( "IntExp: " + exp.value );
    }



    public void visit( VarExp exp, int level ) {
        indent( level );
        System.out.println( "VarExp: " + exp.name );
    }

    //NEW STUFF AND THINGS TO FIX GO HERE


    public void visit( NilExp exp, int level ) {
        indent( level );
        System.out.println( "NilExp: NO ARGS");
    }

    public void visit( CallExp exp, int level ) {
        indent( level );
        System.out.println( "CallExp: " + exp.func );
    }

    public void visit( WhileExp exp, int level) {
        indent (level);
        System.out.println( "WhileExp: ADD IN THE EXP OBJECTS");
    }

    public void visit( ReturnExp exp, int level) {
        indent (level);
        System.out.println( "ReturnExp: returned");
    }

    public void visit ( CompoundExp exp, int level){
        indent (level);
        System.out.println( "CompoundExp: ADD INT THE EXPLIST?");
    }

    public void visit ( FunctionDec exp, int level){
        indent (level);
        System.out.println( "FunctionDec (missing lists and expressions:" + exp.func);
    }

    public void visit ( SimpleDec exp, int level){
        indent (level);
        System.out.println( "SimpleDec:" + exp.name);
    }

    public void visit ( ArrayDec exp, int level){
        indent (level);
        System.out.println( "ArrayDec:" + exp.name);
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
        System.out.println( "IndexVar:" + exp.name);
    }

    public void visit( SimpleVar exp, int level ) {
        indent (level);
        System.out.println( "SimpleVar:" + exp.name);
    }
    public void visit( NameTy exp, int level ) {
        indent (level);
        System.out.println( "NameTy:" + (exp.typ));
    }

//DEPRECATED CODE
    public void visit( ReadExp exp, int level ) {
        indent( level );
        System.out.println( "ReadExp:" );
        exp.input.accept( this, ++level );
    }

    public void visit( RepeatExp exp, int level ) {
        indent( level );
        System.out.println( "RepeatExp:" );
        level++;
        exp.exps.accept( this, level );
        exp.test.accept( this, level );
    }
    public void visit( WriteExp exp, int level ) {
        indent( level );
        System.out.println( "WriteExp:" );
        exp.output.accept( this, ++level );
    }
    public void visit( OpExp exp, int level ) {
        indent( level );
        System.out.print( "OpExp:" );
        switch( exp.op ) {
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
