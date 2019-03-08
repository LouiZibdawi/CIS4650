package absyn;

public class SimpleDec extends VarDec {
    public NameTy typ;
    public String name;

    public SimpleDec(int pos, NameTy typ, String name) {
        this.pos = pos;
        this.typ = typ;
        this.name = name;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}