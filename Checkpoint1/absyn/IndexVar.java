package absyn;

public class IndexVar extends Var {

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}