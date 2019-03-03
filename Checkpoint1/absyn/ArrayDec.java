package absyn;

public class ArrayDec extends VarDec {

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }

}