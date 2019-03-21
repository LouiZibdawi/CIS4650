import java.io.*;
import java.util.*;
import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {
    final static int SPACES = 4;
    public static LinkedList<HashMap<String, SymItem>> symTable;
    private static String tempParams = "";
    private static String currFunc = "";

    public SemanticAnalyzer() {
        this.symTable = new LinkedList<HashMap<String, SymItem>>();
        this.symTable.add(new HashMap<String, SymItem>());
    }

    private void indent(int level) {
        for( int i = 0; i < level * SPACES; i++ ) System.out.print(" ");
    }

    public void visit(ExpList expList, int level) {
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit(AssignExp exp, int level) {
        exp.lhs.accept(this, level);
        exp.rhs.accept(this, level);
        int lhsType = -1;
        int rhsType = -1;
        String functionString = "";

        if (exp.lhs instanceof SimpleVar) {
            SimpleVar tempLhs = (SimpleVar) exp.lhs;
            if (!(symExists(tempLhs.name))) System.err.printf("Error: Undefined reference to %s at line %d\n", tempLhs.name, tempLhs.pos+1);
            lhsType = findType(tempLhs.name);
            if (exp.rhs instanceof VarExp) {
                if (((VarExp) exp.rhs).name instanceof SimpleVar) functionString = ((SimpleVar) ((VarExp) exp.rhs).name).name;
                else functionString = ((IndexVar) ((VarExp) exp.rhs).name).name;
                rhsType = findType(functionString);
            } else if (exp.rhs instanceof CallExp) {
                CallExp tempRhs = (CallExp) exp.rhs;
                rhsType = findType(tempRhs.func);
            } else // the value on the right is assumed to be an integer and is assigned zero automatically
                rhsType = 0;
            if (lhsType != rhsType)
                System.err.printf("Error: Invalid type mismatch between left and right assignment operands: %s at line %d\n", tempLhs.name, tempLhs.pos+1);
        } else {
            IndexVar tempLhs = (IndexVar) exp.lhs;
            if (!(symExists(tempLhs.name))) System.err.printf("Error: Undefined reference to %s at line %d\n", tempLhs.name, tempLhs.pos+1);
            lhsType = findType(tempLhs.name);
            if (exp.rhs instanceof VarExp) {
                if (((VarExp) exp.rhs).name instanceof SimpleVar) functionString = ((SimpleVar) ((VarExp) exp.rhs).name).name;
                else functionString = ((IndexVar) ((VarExp) exp.rhs).name).name;
                rhsType = findType(functionString);
            } else if (exp.rhs instanceof CallExp) {
                CallExp tempRhs = (CallExp) exp.rhs;
                rhsType = findType(tempRhs.func);
                System.out.println(tempRhs.func);
            } else // the value on the right is assumed to be an integer and is assigned zero automatically
                rhsType = 0;
            if (lhsType != rhsType)
                System.err.printf("Error: Invalid type mismatch between left and right assignment operands: %s at line %d\n", tempLhs.name, tempLhs.pos+1);
        }
    }

    public void visit(IfExp exp, int level) {
        indent(level);
        System.out.println("Entering a new if block: ");
        this.symTable.addFirst(new HashMap<String, SymItem>());
        level++;

        exp.test.accept(this, level);
        exp.thenpart.accept(this, level);

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

        String var = "";
        if (exp.test instanceof VarExp) {
            if (((VarExp) exp.test).name instanceof SimpleVar) var = ((SimpleVar) ((VarExp) exp.test).name).name;
            else if (((VarExp) exp.test).name instanceof IndexVar) var = ((IndexVar) ((VarExp) exp.test).name).name;
            if (symExists(var)) {
                if (findType(var) != 0)
                    System.err.printf("Error: Test expression is not an integer at line %d\n", exp.pos+1);
            } else
                System.err.printf("Error: Undeclared test variable: %s at line %d\n", var, exp.pos+1);
        } else if (exp.test instanceof CallExp) {
            var = ((CallExp) exp.test).func;
            if (symExists(var)) {
                if (findType(var) != 0)
                    System.err.printf("Error: Test expression is not an integer at line %d\n", exp.pos+1);
            } else
                System.err.printf("Error: Undeclared if function: %s at line %d\n", var, exp.pos+1);
        }
    }

    public void visit(IntExp exp, int level) {

    }

    public void visit(VarExp exp, int level) {
        exp.name.accept(this, level);
    }

    public void visit(NilExp exp, int level) {

    }

    public void visit(CallExp exp, int level) {
        ExpList args = exp.args;
        while (args != null) {
            args.head.accept(this, level);
            args = args.tail;
        }
        if (this.symTable.getLast().containsKey(exp.func)) {
            if (((SymItem) this.symTable.getLast().get(exp.func)).level == -1)
                ((SymItem) this.symTable.getLast().get(exp.func)).level = -2; // mark as used prototype
        } else
            System.err.printf("Error: Undeclared call function: %s at line %d\n", exp.func, exp.pos+1);
    }

    public void visit(WhileExp exp, int level) {
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
        
        String var = "";
        if (exp.test instanceof VarExp) {
            if (((VarExp) exp.test).name instanceof SimpleVar) var = ((SimpleVar) ((VarExp) exp.test).name).name;
            else if (((VarExp) exp.test).name instanceof IndexVar) var = ((IndexVar) ((VarExp) exp.test).name).name;
            if (symExists(var)) {
                if (findType(var) != 0)
                    System.err.printf("Error: Test expression is not an integer at line %d\n", exp.pos+1);
            } else
                System.err.printf("Error: Undeclared test variable: %s at line %d\n", var, exp.pos+1);
        } else if (exp.test instanceof CallExp) {
            var = ((CallExp) exp.test).func;
            if (symExists(var)) {
                if (findType(var) != 0)
                    System.err.printf("Error: Test expression is not an integer at line %d\n", exp.pos+1);
            } else
                System.err.printf("Error: Undeclared while function: %s at line %d\n", var, exp.pos+1);
        }
    }

    public void visit(ReturnExp exp, int level) {
        if (exp.exp == null) {
            if (this.symTable.getLast().get(currFunc).type == 0)
                System.err.printf("Error: mismatched return type with function type at line %d\n", exp.pos+1);
        } else if (exp.exp != null) {
            exp.exp.accept(this, level);
            String var = "";
            if (exp.exp instanceof IntExp) {
                if (this.symTable.getLast().get(currFunc).type == 1)
                    System.err.printf("Error: mismatched return type with function type at line %d\n", exp.pos+1);
            } else if (exp.exp instanceof VarExp) {
                if (((VarExp) exp.exp).name instanceof SimpleVar) var = ((SimpleVar) ((VarExp) exp.exp).name).name;
                else if (((VarExp) exp.exp).name instanceof IndexVar) var = ((IndexVar) ((VarExp) exp.exp).name).name;
                if (symExists(var)) {
                    if (this.symTable.getLast().get(currFunc).type != findType(var))
                        System.err.printf("Error: mismatched return type with function type at line %d\n", exp.pos+1);
                } else
                    System.err.printf("Error: Undeclared return variable: %s at line %d\n", var, exp.pos+1);
            } else if (exp.exp instanceof CallExp) {
                var = ((CallExp) exp.exp).func;
                if (symExists(var)) {
                    if (this.symTable.getLast().get(currFunc).type != findType(var))
                        System.err.printf("Error: mismatched return type with function type at line %d\n", exp.pos+1);
                } else
                    System.err.printf("Error: Undeclared return function: %s at line %d\n", var, exp.pos+1);
            }
        }
    }

    public void visit(CompoundExp exp, int level){
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

    public void visit(FunctionDec exp, int level){
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
                System.err.printf("Error: Already declared function: %s at line %d\n", exp.func, exp.pos+1);
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
                System.err.printf("Error: Already declared function: %s at line %d\n", exp.func, exp.pos+1);
        }
    }

    public void visit(SimpleDec exp, int level){
        SymItem sym = new SymItem(exp.name, exp.typ.typ, level, "");
        if (!this.symTable.getFirst().containsKey(exp.name)) {
            this.symTable.getFirst().put(exp.name, sym);
            tempParams += exp.typ.typ + " ";
        } else
            System.err.printf("Error: Already declared variable: %s at line %d\n", exp.name, exp.pos+1);
    }

    public void visit(ArrayDec exp, int level){
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
            System.err.printf("Error: Already declared array variable: %s at line %d\n", exp.name, exp.pos+1);
    }

    public void visit(DecList expList, int level) {
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit(VarDecList expList, int level) {
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit(IndexVar exp, int level) {
        exp.index.accept(this, level);
        if (symExists(exp.name)) {
            String var = "";
            if (exp.index instanceof VarExp) {
                if (((VarExp) exp.index).name instanceof SimpleVar) var = ((SimpleVar) ((VarExp) exp.index).name).name;
                else if (((VarExp) exp.index).name instanceof IndexVar) var = ((IndexVar) ((VarExp) exp.index).name).name;
                if (symExists(var)) {
                    if (findType(var) != 0)
                        System.err.printf("Error: Index value is not an integer at line %d\n", exp.pos+1);
                } else
                    System.err.printf("Error: Undeclared index variable: %s at line %d\n", var, exp.pos+1);
            } else if (exp.index instanceof CallExp) {
                var = ((CallExp) exp.index).func;
                if (symExists(var)) {
                    if (findType(var) != 0)
                        System.err.printf("Error: Index value is not an integer at line %d\n", exp.pos+1);
                } else
                    System.err.printf("Error: Undeclared array index function: %s at line %d\n", var, exp.pos+1);
            }
        } else
            System.err.printf("Error: Undeclared array variable: %s at line %d\n", exp.name, exp.pos+1);
    }

    public void visit(SimpleVar exp, int level) {
        if (!symExists(exp.name))
            System.err.printf("Error: Undeclared variable: %s at line %d\n", exp.name, exp.pos+1);
    }

    public void visit(NameTy exp, int level) {

    }

    public void visit(OpExp exp, int level) {
        exp.left.accept(this, level);
        exp.right.accept(this, level);
        String var = "";
        if (exp.left instanceof VarExp) {
            if (((VarExp) exp.left).name instanceof SimpleVar) var = ((SimpleVar) ((VarExp) exp.left).name).name;
            else if (((VarExp) exp.left).name instanceof IndexVar) var = ((IndexVar) ((VarExp) exp.left).name).name;
            if (symExists(var)) {
                if (findType(var) != 0)
                    System.err.printf("Error: Left side of operation is not an integer at line %d\n", exp.pos+1);
            } else
                System.err.printf("Error: Undeclared operation variable: %s at line %d\n", var, exp.pos+1);
        } else if (exp.left instanceof CallExp) {
            var = ((CallExp) exp.left).func;
            if (symExists(var)) {
                if (findType(var) != 0)
                    System.err.printf("Error: Left side of operation is not an integer at line %d\n", exp.pos+1);
            } else
                System.err.printf("Error: Undeclared operation function: %s at line %d\n", var, exp.pos+1);
        }
        if (exp.right instanceof VarExp) {
            if (((VarExp) exp.right).name instanceof SimpleVar) var = ((SimpleVar) ((VarExp) exp.right).name).name;
            else if (((VarExp) exp.right).name instanceof IndexVar) var = ((IndexVar) ((VarExp) exp.right).name).name;
            if (symExists(var)) {
                if (findType(var) != 0)
                    System.err.printf("Error: Right side of operation is not an integer at line %d\n", exp.pos+1);
            } else
                System.err.printf("Error: Undeclared operation variable: %s at line %d\n", var, exp.pos+1);
        } else if (exp.right instanceof CallExp) {
            var = ((CallExp) exp.right).func;
            if (symExists(var)) {
                if (findType(var) != 0)
                    System.err.printf("Error: Right side of operation is not an integer at line %d\n", exp.pos+1);
            } else
                System.err.printf("Error: Undeclared operation function: %s at line %d\n", var, exp.pos+1);
        }
    }

    public void printMap(Iterator i, int level) {
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
