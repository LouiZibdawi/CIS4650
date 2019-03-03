package absyn;

public class ReturnExp extends Exp{

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }

}