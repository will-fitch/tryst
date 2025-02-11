
import java.util.HashMap;
import java.util.Map;
import tokens.Token;

public class Scope {

    private final Map<String, Var> variables;
    private int registerIndex;

    public Scope() {
        variables = new HashMap<>();
        registerIndex = 0;
    }

    public Token.Type getType(String ident) {
        if(!variables.containsKey(ident)) return null;
        String type = variables.get(ident).type;
        if(type.equals("BLN")) return Token.Type.BLN;
        if(type.equals("INT")) return Token.Type.INT;
        System.err.println("Scope:getType unrecognized type - " + type);
        return null;
    }

    public String register(String ident) {
        if(!variables.containsKey(ident)) return null;
        return variables.get(ident).register;
    }

    public Expr getExpr(String ident) {
        if(!variables.containsKey(ident)) return null;
        return variables.get(ident).expr;
    }

    public void declare(String ident, String type, Expr expr) {
        if(registerIndex > 4) System.err.println("WARNING: TOO MANY VARIABLE DECLARATIONS!");
        variables.put(ident, new Var(type, expr, "t"+registerIndex));
        registerIndex++;
    }

}

class Var {

    public final String type;
    public final Expr expr;
    public final String register;

    public Var(String type, Expr expr, String register) {
        this.type = type;
        this.expr = expr;
        this.register = register;
    }

}