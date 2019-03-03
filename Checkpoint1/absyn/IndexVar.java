package absyn;

public class IndexVar extends Var {

    public String name;

    public IndexVar(int pos, String name)
    {
        this.pos  = pos;
        this.name = name;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}