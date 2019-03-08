package absyn;

public class NameTy extends Absyn{
    public final static int INT = 0;
    public final static int VOID = 1;

    public int typ;

    public NameTy(int pos, int typ) {
        this.pos = pos;
        this.typ = typ;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}