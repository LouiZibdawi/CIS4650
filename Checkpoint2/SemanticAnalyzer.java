import java.io.*;
import java.util.*;
import absyn.*;

// *TODO: check for int in array index
// TODO x2: check both sides of assignment or operation is an int
// TODO: if test expr should be of type int, if function, function type should be int
// -TODO: matching function params when calling
// -TODO: need symExists check inside of simple/index var?

// 0 for int, 1 for void

public class SemanticAnalyzer implements AbsynVisitor {
    final static int SPACES = 4;
    public static LinkedList<HashMap<String, SymItem>> symTable;
    private static String tempParams = "";
    private static String currFunc = "";

    public SemanticAnalyzer() { // done
        this.symTable = new LinkedList<HashMap<String, SymItem>>();
        this.symTable.add(new HashMap<String, SymItem>());
    }

    private void indent(int level) { // done
        for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
    }

    public void visit(ExpList expList, int level) { // done
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit(AssignExp exp, int level) {
//        level++;
        exp.lhs.accept( this, level );
        exp.rhs.accept( this, level );
    }

    public void visit(IfExp exp, int level) { // done
        indent(level);
        System.out.println("Entering a new if block: ");
        this.symTable.addFirst(new HashMap<String, SymItem>());
        level++;

        exp.test.accept( this, level );
        exp.thenpart.accept( this, level );

        printMap(this.symTable.getFirst().entrySet().iterator(), level);
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

            printMap(this.symTable.getFirst().entrySet().iterator(), level);
            level--;
            this.symTable.removeFirst();
            indent(level);
            System.out.println("Leaving the else block");
        }
    }

    public void visit(IntExp exp, int level) {

    }

    public void visit(VarExp exp, int level) {
//        level++;
        exp.name.accept(this, level);
    }

    public void visit(NilExp exp, int level) {

    }

    public void visit(CallExp exp, int level) { // done
        if (this.symTable.getLast().containsKey(exp.func)) {
            if (((SymItem) this.symTable.getLast().get(exp.func)).level == -1)
                ((SymItem) this.symTable.getLast().get(exp.func)).level = -2; // mark as used prototype
            ExpList args = exp.args;
            while (args != null) {
                args.head.accept(this, level);
                args = args.tail;
            }
        } else
            System.err.printf("Error: Undeclared function: %s\n", exp.func);
    }

    public void visit(WhileExp exp, int level) { // done
        indent(level);
        System.out.println("Entering a new while block: ");
        this.symTable.addFirst(new HashMap<String, SymItem>());
        level++;

        exp.test.accept(this, level);
        exp.body.accept(this, level);

        printMap(this.symTable.getFirst().entrySet().iterator(), level);
        level--;
        this.symTable.removeFirst();
        indent(level);
        System.out.println("Leaving the while block");
    }

    public void visit(ReturnExp exp, int level) { // done
        if (exp.exp == null) {
            if (this.symTable.getLast().get(currFunc).type == 0)
                System.err.printf("Error: mismatched return type with function type\n");
        } else if (exp.exp != null) {
            if (exp.exp instanceof IntExp) {
                if (this.symTable.getLast().get(currFunc).type == 1)
                    System.err.printf("Error: mismatched return type with function type\n");
            } else if (exp.exp instanceof VarExp) {
                if (((VarExp) exp.exp).name instanceof SimpleVar) {
                    String var = ((SimpleVar) ((VarExp) exp.exp).name).name;
                    if (symExists(var)) {
                        if (this.symTable.getLast().get(currFunc).type != findType(var))
                            System.err.printf("Error: mismatched return type with function type\n");
                    } else
                        System.err.printf("Error: Undeclared return variable: %s\n", var);
                } else if (((VarExp) exp.exp).name instanceof IndexVar) {
                    String var = ((IndexVar) ((VarExp) exp.exp).name).name;
                    if (symExists(var)) {
                        if (this.symTable.getLast().get(currFunc).type != findType(var))
                            System.err.printf("Error: mismatched return type with function type\n");
                    } else
                        System.err.printf("Error: Undeclared return array: %s\n", var);
                }
            } else if (exp.exp instanceof CallExp) {
                String var = ((CallExp) exp.exp).func;
                if (symExists(var)) {
                    if (this.symTable.getLast().get(currFunc).type != findType(var))
                        System.err.printf("Error: mismatched return type with function type\n");
                } else
                    System.err.printf("Error: Undeclared return function: %s\n", var);
            } else
                exp.exp.accept(this, level);
        }
    }

    public void visit(CompoundExp exp, int level){
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

    public void visit(FunctionDec exp, int level){ // done
        SymItem sym = new SymItem(exp.func, exp.result.typ, level, "");
        if (exp.body == null) { // Check if it is a prototype
            sym.level = -1; // Set the level to -1 to identify it as a function prototype
            this.symTable.addFirst(new HashMap<String, SymItem>()); // temp storage for params
            level++;

            VarDecList parms = exp.params;
            tempParams = ""; // clear before using
            while (parms != null) {
                parms.head.accept(this, level);
                parms = parms.tail;
            }
            sym.params += tempParams;

            level--;
            this.symTable.removeFirst(); // delete temp storage; don't need to keep
            if (!this.symTable.getLast().containsKey(exp.func))
                this.symTable.getLast().put(exp.func, sym);
            else
                System.err.printf("Error: %s has already been declared\n", exp.func);
        } else { // if it is a function definition
            if (!this.symTable.getLast().containsKey(exp.func) || (this.symTable.getLast().containsKey(exp.func) && ((SymItem) this.symTable.getLast().get(exp.func)).level == -1)) {
                indent(level);
                System.out.println("Entering the scope for function " + exp.func + ": ");
                currFunc = exp.func;
                this.symTable.addFirst(new HashMap<String, SymItem>());
                level++;

                VarDecList parms = exp.params;
                tempParams = ""; // clear before using
                while (parms != null) {
                    parms.head.accept(this, level);
                    parms = parms.tail;
                }
                sym.params = tempParams;
                this.symTable.getLast().put(exp.func, sym);

                exp.body.accept(this, level);
                printMap(this.symTable.getFirst().entrySet().iterator(), level);
                level--;
                this.symTable.removeFirst();
                indent(level);
                System.out.println("Leaving the function scope");
                currFunc = "";
            } else
                System.err.printf("Error: %s has already been declared\n", exp.func);
        }
    }

    public void visit(SimpleDec exp, int level){ // done
        SymItem sym = new SymItem(exp.name, exp.typ.typ, level, "");
        if (!this.symTable.getFirst().containsKey(exp.name)) {
            this.symTable.getFirst().put(exp.name, sym);
            tempParams += exp.typ.typ + " ";
        } else
            System.err.printf("Error: %s has already been declared\n", exp.name);
    }

    public void visit(ArrayDec exp, int level){ // index could be IntExp, SimpleVar, CallExp, nothing, or OpExp?
        String name = "";
        name += exp.name + "[";
        if (exp.size != null)
            name += exp.size.value + "";
        name += "]";
        SymItem sym = new SymItem(name, exp.typ.typ, level, "");
        if (!this.symTable.getFirst().containsKey(exp.name)) {
            this.symTable.getFirst().put(exp.name, sym);
            tempParams += exp.typ.typ + " ";
        } else
            System.err.printf("Error: %s has already been declared\n", exp.name);
    }

    public void visit(DecList expList, int level) { // done
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit(VarDecList expList, int level) { // done
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit(IndexVar exp, int level) { // index could be IntExp, SimpleVar (CallExp or OpExp?)
        if (symExists(exp.name))
            exp.index.accept(this, level);
        else
            System.err.printf("Error: Undefined variable: %s\n", exp.name);
    }

    public void visit(SimpleVar exp, int level) { // done
        if (!symExists(exp.name))
            System.err.printf("Error: Undefined variable: %s\n", exp.name);
    }

    public void visit(NameTy exp, int level) {

    }

    public void visit(OpExp exp, int level) {
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

    public void printUndefined(Iterator i) {
        while (i.hasNext()) {
            SymItem symbol = (SymItem) ((Map.Entry) i.next()).getValue();
            if (symbol.level == -1)
                System.err.printf("Error: Undefined function prototype: %s\n", symbol.name);
            else if (symbol.level == -2)
                System.err.printf("Error: Used but undefined function prototype: %s\n", symbol.name);
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

    public int findType(String name) {
        if (this.symTable.size() != 0) {
            for (int i = 0; i < this.symTable.size(); i++) {
                if (this.symTable.get(i).containsKey(name))
                    return ((SymItem) this.symTable.get(i).get(name)).type;
            }
        }
        return -1;
    }
}
