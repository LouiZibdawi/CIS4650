import java.io.*;
import java.util.*;
import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {
    final static int SPACES = 4;
    public static LinkedList<HashMap<String, SymItem>> symTable;
    private static String tempParams = "";

    public SemanticAnalyzer() { // done
        this.symTable = new LinkedList<HashMap<String, SymItem>>();
        this.symTable.add(new HashMap<String, SymItem>());
    }

    private void indent( int level ) { // done
        for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
    }

    public void visit( ExpList expList, int level ) { // done
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit( AssignExp exp, int level ) {
//        level++;
        exp.lhs.accept( this, level );
        exp.rhs.accept( this, level );
    }

    public void visit( IfExp exp, int level ) { // done
        indent(level);
        System.out.println("Entering a new if block: ");
        this.symTable.addFirst(new HashMap<String, SymItem>());
        level++;

        exp.test.accept( this, level );
        exp.thenpart.accept( this, level );

        printMap(this.symTable.get(0).entrySet().iterator(), level);
        level--;
        this.symTable.removeFirst();
        indent(level);
        System.out.println("Leaving the if block");

        if (exp.elsepart != null && !(exp.elsepart instanceof NilExp)) {
            indent(level);
            System.out.println("Entering a new else block: ");
            this.symTable.addFirst(new HashMap<String, SymItem>());
            level++;

            exp.elsepart.accept(this, level);

            printMap(this.symTable.get(0).entrySet().iterator(), level);
            level--;
            this.symTable.removeFirst();
            indent(level);
            System.out.println("Leaving the else block");
        }
    }

    public void visit( IntExp exp, int level ) {

    }

    public void visit( VarExp exp, int level ) {
//        level++;
        exp.name.accept(this, level);
    }

    public void visit( NilExp exp, int level ) {

    }

    public void visit( CallExp exp, int level ) { // TODO: check before using to see if it exists, see discord notes for other changes
//        level++;
        ExpList args = exp.args;
        while (args != null) {
            args.head.accept(this, level);
            args = args.tail;
        }
    }

    public void visit( WhileExp exp, int level) { // done
        indent(level);
        System.out.println("Entering a new while block: ");
        this.symTable.addFirst(new HashMap<String, SymItem>());
        level++;

        exp.test.accept(this, level);
        exp.body.accept(this, level);

        printMap(this.symTable.get(0).entrySet().iterator(), level);
        level--;
        this.symTable.removeFirst();
        indent(level);
        System.out.println("Leaving the while block");
    }

    public void visit( ReturnExp exp, int level) {
//        level++;
        if (exp.exp != null)
            exp.exp.accept(this, level);
    }

    public void visit ( CompoundExp exp, int level){
//        level++;
        VarDecList decs = exp.decs;
        while (decs != null) {
            decs.head.accept(this, level);
            decs = decs.tail;
        }
        ExpList exps = exp.exps;
        while (exps != null) {
            exps.head.accept(this, level);
            exps = exps.tail;
        }
    }

    public void visit ( FunctionDec exp, int level){ // done
        SymItem sym = new SymItem(exp.func, exp.result.typ, level, "");

        indent(level);
        System.out.println("Entering the scope for function " + exp.func + ": ");
        this.symTable.addFirst(new HashMap<String, SymItem>());
        level++;

        VarDecList parms = exp.params;
        tempParams = ""; // clear before using
        while (parms != null) {
            parms.head.accept(this, level);
            parms = parms.tail;
        }
        sym.params = tempParams;
        tempParams = ""; // clear after storing in symbol item
        if (!this.symTable.get(1).containsKey(exp.func))
            this.symTable.get(1).put(exp.func, sym);
        else
            System.err.printf("Error: %s has already been declared\n", exp.func);

        exp.body.accept(this, level);

        printMap(this.symTable.get(0).entrySet().iterator(), level);
        level--;
        this.symTable.removeFirst();
        indent(level);
        System.out.println("Leaving the function scope");
    }

    public void visit ( SimpleDec exp, int level){ // done
        SymItem sym = new SymItem(exp.name, exp.typ.typ, level, "");
        if (!this.symTable.get(0).containsKey(exp.name)) {
            this.symTable.get(0).put(exp.name, sym);
            tempParams += exp.typ.typ + " ";
        } else
            System.err.printf("Error: %s has already been declared\n", exp.name);
    }

    public void visit ( ArrayDec exp, int level){ // done
        String name = "";
        name += exp.name + "[";
        if (exp.size != null)
            name += exp.size.value + "";
        name += "]";
        SymItem sym = new SymItem(name, exp.typ.typ, level, "");
        if (!this.symTable.get(0).containsKey(exp.name)) {
            this.symTable.get(0).put(exp.name, sym);
            tempParams += exp.typ.typ + " ";
        } else
            System.err.printf("Error: %s has already been declared\n", exp.name);
    }

    public void visit( DecList expList, int level ) { // done
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit( VarDecList expList, int level ) { // done
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit( IndexVar exp, int level ) { // done
//        level++;
        if (symExists(exp.name))
            exp.index.accept(this, level);
        else
            System.err.printf("Error: Undefined variable %s\n", exp.name);
    }

    public void visit( SimpleVar exp, int level ) { // done
        if (!symExists(exp.name))
            System.err.printf("Error: Undefined variable %s\n", exp.name);
    }

    public void visit( NameTy exp, int level ) {

    }

    public void visit( OpExp exp, int level ) {
//        level++;
        exp.left.accept( this, level );
        exp.right.accept( this, level );
    }

    public void printMap(Iterator i, int level) { // done
        while (i.hasNext()) {
            SymItem symbol = (SymItem) ((Map.Entry) i.next()).getValue();
            indent(level);
            System.out.print(symbol.name + ": ");
            if (!symbol.params.isEmpty()) {
                String[] tokens = symbol.params.split(" ");
                System.out.print("( ");
                for (String s : tokens) {
                    if (s.equals("0"))
                        System.out.print("int ");
                    else if (s.equals("1"))
                        System.out.print("void ");
                }
                System.out.print(") -> ");
            }
            if (symbol.type == 0)
                System.out.println("int");
            else if (symbol.type == 1)
                System.out.println("void");
        }
    }

    public boolean symExists(String name) {
        if (this.symTable.size() != 0) {
            for (int i = 0; i < this.symTable.size(); i++) {
                if (this.symTable.get(i).containsKey(name))
                    return true;
            }
        }
        return false;
    }
}
