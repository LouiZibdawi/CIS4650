package absyn;

public class SimpleDec extends VarDec {

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }

}