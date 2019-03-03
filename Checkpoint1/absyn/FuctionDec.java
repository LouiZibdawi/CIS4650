package absyn;

public class FunctionDec extends Dec {

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }

}