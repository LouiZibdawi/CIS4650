package absyn;

public class OpExp extends Exp {
    public final static int PLUS    = 0;
    public final static int MINUS   = 1;
    public final static int TIMES   = 2;
    public final static int OVER    = 3;
    public final static int EQ      = 4;
    public final static int LT      = 5;
    public final static int GT      = 6;
    public final static int GTE     = 7;
    public final static int LTE     = 8;
    public final static int NE      = 10;
    public final static int COMPARE = 9; //This was the old == value
    public Exp left;
    public int op;
    public Exp right;

    public OpExp( int pos, Exp left, int op, Exp right ) {
        this.pos = pos;
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public void accept( AbsynVisitor visitor, int level ) {
    visitor.visit( this, level );
  }
}
