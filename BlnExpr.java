import tokens.Token;
import tokens.TokenString;

public abstract class BlnExpr extends Expr {

    public static boolean getBooleanExpression(TokenString tokens) {
        return blnExpr(tokens).value();
    }
    
    public static BlnExpr blnExpr(TokenString tokens) {
        return blnEquality(tokens);
    }

    private static BlnExpr blnEquality(TokenString tokens) {
        BlnExpr left = blnConjunction(tokens);
        if(!tokens.hasNext()) return left;
        if(!tokens.pop().isType(Token.Type.EQUALS)) {
            tokens.unpop();
            return left;
        }
        BlnExpr right = blnEquality(tokens);
        return left.is(right);
    }

    private static BlnExpr blnConjunction(TokenString tokens) {
        BlnExpr left = blnNegation(tokens);
        while(tokens.hasNext()) {
            Token next = tokens.peek();
            if(!next.isType(Token.Type.AND) && !next.isType(Token.Type.OR)) break;
            tokens.pop();
            BlnExpr right = blnNegation(tokens);

            if(next.isType(Token.Type.AND)) left = left.and(right);
            else left = left.or(right);
        }
        return left;
    }

    private static BlnExpr blnNegation(TokenString tokens) {
        if(!tokens.peek().isType(Token.Type.EXCLAME)) return blnEncapsulation(tokens);
        tokens.pop();

        BlnExpr exprToNegate = blnEncapsulation(tokens);
        return exprToNegate.not();
    }

    private static BlnExpr blnEncapsulation(TokenString tokens) {
        int index =  tokens.index();
        Token next = tokens.pop();
        if(!next.isType(Token.Type.OPEN_PAREN)) {
            tokens.unpop();
            return blnLiteral(tokens);
        }
        
        BlnExpr encapsulated = blnExpr(tokens);
        if(encapsulated == null) {
            tokens.setIndex(index);
            return blnLiteral(tokens);
        }
        if(!tokens.pop().isType(Token.Type.CLOSE_PAREN)) return null;
        return encapsulated;

    }

    private static BlnExpr blnLiteral(TokenString tokens) {
        Token next = tokens.pop();
        if(next.isType(Token.Type.TRUE)) return new BlnLit(true);
        if(next.isType(Token.Type.FALSE)) return new BlnLit(false);

        tokens.unpop();
        return blnVar(tokens);
    }

    //very broken, should not recalculate expression, just retrieve value
    private static BlnExpr blnVar(TokenString tokens) {
        Token next = tokens.pop();
        if(!next.isType(Token.Type.IDENTIFIER)) {
            tokens.unpop();
            return intEquality(tokens);
        }
        if(Tryst.global.getType(next.text) != Token.Type.BLN) {
            tokens.unpop();
            return intEquality(tokens);
        }
        return (BlnExpr)Tryst.global.getExpr(next.text);
    }

    private static BlnExpr intEquality(TokenString tokens) {
        int index = tokens.index();
        IntExpr left = IntExpr.intExpr(tokens);
        if(left == null || tokens.hasNext() == false) {
            tokens.setIndex(index);
            return null;
        }
        if(!tokens.pop().isType(Token.Type.EQUALS)) {
            tokens.setIndex(index);
            return null;
        }
        IntExpr right = IntExpr.intExpr(tokens);
        if(right == null) {
            tokens.setIndex(index);
            return null;
        }
        return left.is(right);
    }

    

    //ABSTRACT:

    @Override
    abstract Boolean value();

    //INSTANCE:

    public BlnExpr and(BlnExpr other) {
        return new BlnAnd(this, other);
    }

    public BlnExpr or(BlnExpr other) {
        return new BlnOr(this, other);
    }

    public BlnExpr is(BlnExpr other) {
        return new BlnIs(this, other);
    }

    public BlnExpr not() {
        return new BlnNot(this);
    }

}

class BlnLit extends BlnExpr {
    boolean value;

    public BlnLit(boolean value) {
        this.value = value;
    }

    @Override
    Boolean value() {
        return value;
    }

    @Override
    public void assemble(Assembly assembly) {
        if(value) assembly.append("li t5 1");
        else assembly.append("li t5 0");
        assembly.pushFrom("t5");
    }

}

class BlnAnd extends BlnExpr {
    BlnExpr left, right;

    public BlnAnd(BlnExpr left, BlnExpr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    Boolean value() {
        return left.value() && right.value();
    }

    @Override
    public void assemble(Assembly assembly) {
        left.assemble(assembly);
        right.assemble(assembly);
        assembly.popTo("t5");
        assembly.popTo("t6");
        assembly.append("and t5 t5 t6");
        assembly.pushFrom("t5");
    }

}

class BlnOr extends BlnExpr {
    BlnExpr left, right;

    public BlnOr(BlnExpr left, BlnExpr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    Boolean value() {
        return left.value() || right.value();
    }

    @Override
    public void assemble(Assembly assembly) {
        left.assemble(assembly);
        right.assemble(assembly);
        assembly.popTo("t5");
        assembly.popTo("t6");
        assembly.append("or t5 t5 t6");
        assembly.pushFrom("t5");
    }

}

class BlnIs extends BlnExpr {
    BlnExpr left, right;

    public BlnIs(BlnExpr left, BlnExpr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    Boolean value() {
        return left.value().equals(right.value());
    }

    @Override
    public void assemble(Assembly assembly) {
        left.assemble(assembly);
        right.assemble(assembly);
        assembly.popTo("t5");
        assembly.popTo("t6");
        assembly.append("xor t5 t5 t6");
        assembly.append("seqz t5 t5");
        assembly.pushFrom("t5");
    }

}

class BlnNot extends BlnExpr {
    BlnExpr arg;

    public BlnNot(BlnExpr arg) {
        this.arg = arg;
    }

    @Override
    Boolean value() {
        return !arg.value();
    }

    @Override
    public void assemble(Assembly assembly) {
        arg.assemble(assembly);
        assembly.popTo("t5");
        assembly.append("snez t5 t5");
        assembly.pushFrom("t5");
    }
}