package absyn;

public class NilExp extends Exp {

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}