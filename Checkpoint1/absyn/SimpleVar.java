package absyn;

public class SimpleVar extends Var {

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}