package absyn;

public class CallExp extends Exp{


    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}