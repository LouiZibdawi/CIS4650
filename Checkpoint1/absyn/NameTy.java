package absyn;

public class NameTy extends Absyn{
    public final static int INT = 0;
    public final static int VOID = 0;


    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}