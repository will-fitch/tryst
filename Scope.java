
import java.util.HashMap;
import java.util.Map;
import tokens.Token;

public class Scope {

    private final Map<String, Var> variables;

    public Scope() {
        variables = new HashMap<>();
    }

    public Token.Type getType(String ident) {
        if(!variables.containsKey(ident)) return null;
        String type = variables.get(ident).type;
        if(type.equals("BLN")) return Token.Type.BLN;
        if(type.equals("INT")) return Token.Type.INT;
        System.err.println("Scope:getType unrecognized type - " + type);
        return null;
    }

    public void loadTo(Assembly assembly, String ident, String register) {
        if(!variables.containsKey(ident)) return;
        assembly.load(register, "s0", variables.get(ident).offset);
    }

    public void saveFrom(Assembly assembly, String ident, String register) {
        if(!variables.containsKey(ident)) return;
        assembly.save(register, "s0", variables.get(ident).offset);
    }

    public void open(Assembly assembly) {
        assembly.moveValue("sp", "s0");
        assembly.append("addi sp sp " + (-4*variables.size()));
    }

    public Expr getExpr(String ident) {
        if(!variables.containsKey(ident)) return null;
        return variables.get(ident).expr;
    }

    public void declare(String ident, String type, Expr expr) {
        variables.put(ident, new Var(type, expr, 4*(variables.size()+1)));
    }

}

class Var {

    public final String type;
    public final Expr expr;
    public final int offset;

    public Var(String type, Expr expr, int offset) {
        this.type = type;
        this.expr = expr;
        this.offset = offset;
    }

}