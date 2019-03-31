package absyn;

public class FunctionDec extends Dec {
    public NameTy result;
    public String func;
    public VarDecList params;
    public CompoundExp body;
    public int funcAddress;

    public FunctionDec(int pos, NameTy result, String func, VarDecList params, CompoundExp body) {
        this.pos = pos;
        this.result = result;
        this.func = func;
        this.params = params;
        this.body = body;
        this.funcAddress = 0; //absolute value of the function address; redefined in the code generation
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}