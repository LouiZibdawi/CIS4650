package absyn;

public class CompoundExp extends Exp {


    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }

}