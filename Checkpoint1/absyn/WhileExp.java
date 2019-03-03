package absyn;


public class WhileExp extends Exp{

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }

}