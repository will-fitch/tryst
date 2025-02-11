import tokens.TokenString;

public abstract class Expr {

    public static Expr getExpression(TokenString tokens) {

        Expr expr = BlnExpr.blnExpr(tokens);
        if(expr != null) {
            return expr;
        }
        expr = IntExpr.intExpr(tokens);
        return expr;

    }

    abstract Object value();
    abstract void assemble(Assembly assembly);
}
