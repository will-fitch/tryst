import tokens.Token;
import tokens.TokenString;

public abstract class IntExpr extends Expr {
    
    public static int getIntegerExpr(TokenString tokens) {
        return intExpr(tokens).value;
    }

    public static IntExpr intExpr(TokenString tokens) {
        return intAddSub(tokens);
    }

    private static IntExpr intAddSub(TokenString tokens) {
        IntExpr left = intMulDiv(tokens);
        while(tokens.hasNext()) {
            Token next = tokens.peek();
            if(!next.isType(Token.Type.PLUS) && !next.isType(Token.Type.MINUS)) break;
            tokens.pop();
            IntExpr right = intMulDiv(tokens);

            if(next.isType(Token.Type.PLUS)) left = left.plus(right);
            else left = left.minus(right);
        }
        return left;
    }

    private static IntExpr intMulDiv(TokenString tokens) {
        IntExpr left = intNegation(tokens);
        while(tokens.hasNext()) {
            Token next = tokens.peek();
            if(!next.isType(Token.Type.STAR) && !next.isType(Token.Type.F_SLASH)) break;
            tokens.pop();
            IntExpr right = intNegation(tokens);

            if(next.isType(Token.Type.STAR)) left = left.times(right);
            else left = left.dividedBy(right);
        }
        return left;
    }

    private static IntExpr intNegation(TokenString tokens) {
        if(!tokens.peek().isType(Token.Type.MINUS)) return intEncapsulation(tokens);
        tokens.pop();

        IntExpr exprToNegate = intEncapsulation(tokens);
        return exprToNegate.negative();
    }

    private static IntExpr intEncapsulation(TokenString tokens) {
        Token next = tokens.pop();
        if(!next.isType(Token.Type.OPEN_PAREN)) {
            tokens.unpop();
            return intLiteral(tokens);
        }
        
        IntExpr encapsulated = intExpr(tokens);
        if(encapsulated == null) return null;
        if(!tokens.pop().isType(Token.Type.CLOSE_PAREN)) return null;
        return encapsulated;
    }

    private static IntExpr intLiteral(TokenString tokens) {
        Token next = tokens.pop();
        if(next.isType(Token.Type.NUM_LITERAL)) return new IntLit(Integer.parseInt(next.text));

        tokens.unpop();
        return intVar(tokens);
    }

    private static IntExpr intVar(TokenString tokens) {
        Token next = tokens.pop();
        if(!next.isType(Token.Type.IDENTIFIER)) {
            tokens.unpop();
            return null;
        }
        if(Tryst.global.getType(next.text) != Token.Type.INT) {
            tokens.unpop();
            return null;
        }
        return new IntVar(next.text);
    }


    //INSTANCE
    public final int value;

    public IntExpr() {
        this.value = 0;
    }

    public IntExpr(int value) {
        this.value = value;
    }

    @Override
    abstract Integer value();

    public IntExpr plus(IntExpr other) {
        return new IntPlus(this, other);
    }

    public IntExpr minus(IntExpr other) {
        return new IntPlus(this, other);
    }

    public IntExpr times(IntExpr other) {
        return new IntTimes(this, other);
    }

    public IntExpr dividedBy(IntExpr other) {
        return new IntDividedBy(this, other);
    }

    public IntExpr negative() {
        return new IntNegative(this);
    }

    public BlnExpr is(IntExpr other) {
        return new IntIs(this, other);
    }

}

class IntVar extends IntExpr {
    String ident;

    public IntVar(String ident) {
        this.ident = ident;
    }

    public Integer value() {
        return (Integer)Tryst.global.getExpr(ident).value();
    }

    public void assemble(Assembly assembly) {
        Tryst.global.loadTo(assembly, ident, "t5");
        assembly.pushFrom("t5");
    }
}

class IntLit extends IntExpr {
    int value;

    public IntLit(int value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }

    public String assembly() {
        return "li t5 " + value + "\naddi sp sp -4\nsw t5 4(sp)\n";
    }

    @Override
    public void assemble(Assembly assembly) {
        assembly.append("li t5 "+value);
        assembly.pushFrom("t5");
    }
}

class IntPlus extends IntExpr {
    IntExpr left, right;

    public IntPlus(IntExpr left, IntExpr right) {
        this.left = left;
        this.right = right;
    }

    public Integer value() {
        return left.value() + right.value();
    }

    @Override
    public void assemble(Assembly assembly) {
        left.assemble(assembly);
        right.assemble(assembly);
        assembly.popTo("t5");
        assembly.popTo("t6");
        assembly.append("add t5 t5 t6");
        assembly.pushFrom("t5");
    }
}

class IntMinus extends IntExpr {
    IntExpr left, right;

    public IntMinus(IntExpr left, IntExpr right) {
        this.left = left;
        this.right = right;
    }

    public Integer value() {
        return left.value() - right.value();
    }

    @Override
    public void assemble(Assembly assembly) {
        left.assemble(assembly);
        right.assemble(assembly);
        assembly.popTo("t5");
        assembly.popTo("t6");
        assembly.append("sub t5 t5 t6");
        assembly.pushFrom("t5");
    }
}

class IntTimes extends IntExpr {
    IntExpr left, right;

    public IntTimes(IntExpr left, IntExpr right) {
        this.left = left;
        this.right = right;
    }

    public Integer value() {
        return left.value() * right.value();
    }

    @Override
    public void assemble(Assembly assembly) {
        left.assemble(assembly);
        right.assemble(assembly);
        assembly.popTo("t5");
        assembly.popTo("t6");
        assembly.append("mul t5 t5 t6");
        assembly.pushFrom("t5");
    }
}

class IntDividedBy extends IntExpr {
    IntExpr left, right;

    public IntDividedBy(IntExpr left, IntExpr right) {
        this.left = left;
        this.right = right;
    }

    public Integer value() {
        return left.value() / right.value();
    }

    @Override
    public void assemble(Assembly assembly) {
        left.assemble(assembly);
        right.assemble(assembly);
        assembly.popTo("t5");
        assembly.popTo("t6");
        assembly.append("div t5 t5 t6");
        assembly.pushFrom("t5");
    }
}

class IntNegative extends IntExpr {
    IntExpr arg;

    public IntNegative(IntExpr arg) {
        this.arg = arg;
    }

    public Integer value() {
        return -arg.value();
    }

    public String assembly() {
        return "NOT IMPLEMENTED!!";
    }

    @Override
    public void assemble(Assembly assembly) {
        arg.assemble(assembly);
        assembly.popTo("t5");
        assembly.append("neg t5 t5");
        assembly.pushFrom("t5");
    }
}

class IntIs extends BlnExpr {
    IntExpr left, right;

    public IntIs(IntExpr left, IntExpr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Boolean value() {
        return left.value().equals(right.value());
    }

    public String assembly() {
        return "NOT IMPLEMENTED!!";
    }

    @Override
    public void assemble(Assembly assembly) {
        System.err.println("!NOT IMPLEMENTED!!");
    }
}
