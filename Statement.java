import tokens.Token;
import tokens.Token.Type;
import tokens.TokenString;

public abstract class Statement {
 
    public static Statement getStatement(TokenString tokens) {
        Token next = tokens.pop();

        //PRINT
        if(next.isType(Type.PRINT)) {
            if(!tokens.pop().isType(Type.OPEN_PAREN)) {
                System.err.println("print statement error 1");
                tokens.unpop();
                return null;
            }
            Expr expr = Expr.getExpression(tokens);
            if(expr == null) {
                System.err.print("print statement error: null expr  ");
                return null;
            }
            if(!tokens.pop().isType(Type.CLOSE_PAREN)) {
                System.err.println("print statement error 2");
                return null;
            }
            if(!tokens.pop().isType(Type.SEMI)) {
                System.err.println("print statement error 3");
                return null;
            }
            return new PrintStatement(expr);
        }

        //BLN DECLARATION
        if(next.isType(Type.BLN)) {
            Token ident = tokens.pop();
            if(!ident.isType(Type.IDENTIFIER)) {
                System.err.println("BlnDeclaration error");
            }
            if(!tokens.pop().isType(Type.COLON)) {
                System.err.println("BlnDeclaration missing colon");
                System.exit(0);
            }
            BlnExpr expr = BlnExpr.blnExpr(tokens);
            if(expr == null) {
                System.err.println("BlnDeclaration expr null");
                System.exit(0);
            }
            if(!tokens.pop().isType(Type.SEMI)) {
                System.err.println("BlnDeclaration missing semicolon");
                return null;
            }
            return new BlnDeclaration(ident.text, expr);
        }

        //INT DECLARATION
        if(next.isType(Type.INT)) {
            Token ident = tokens.pop();
            if(!ident.isType(Type.IDENTIFIER)) {
                System.err.println("IntDeclaration error");
            }
            if(!tokens.pop().isType(Type.COLON)) {
                System.err.println("IntDeclaration missing colon");
                System.exit(0);
            }
            IntExpr expr = IntExpr.intExpr(tokens);
            if(expr == null) {
                System.err.println("IntDeclaration expr null");
                System.exit(0);
            }
            if(!tokens.pop().isType(Type.SEMI)) {
                System.err.println("IntDeclaration missing semicolon");
                return null;
            }
            return new IntDeclaration(ident.text, expr);
        }

        //ASSIGNMENT
        if(next.isType(Type.IDENTIFIER)) {
            if(!tokens.pop().isType(Type.LEFT_ASSIGN)) {
                tokens.unpop();
                System.err.println("Assignment expected LEFT_ASSIGN token");
                return null;
            }
            Expr expr = Expr.getExpression(tokens);
            if(expr == null) {
                System.err.println("Null expression in Assignment");
                return null;
            }
            if(!tokens.pop().isType(Type.SEMI)) {
                tokens.unpop();
                System.err.println("Assignment expected SEMI token");
                return null;
            }
            if(expr instanceof BlnExpr blnExpr)
                return new BlnAssignment(next.text, blnExpr);
            if(expr instanceof IntExpr intExpr)
                return new IntAssignment(next.text, intExpr);
            
        }

        if(next.isType(Type.IF)) {
            if(!tokens.pop().isType(Type.OPEN_PAREN)) {
                tokens.unpop();
                System.err.println("Expected open paren after IF");
                return null;
            }
            BlnExpr expr = BlnExpr.blnExpr(tokens);
            if(expr == null) {
                System.err.println("Couldn't boolean for IF");
                return null;
            }
            if(!tokens.pop().isType(Type.CLOSE_PAREN)) {
                tokens.unpop();
                System.err.println("Expected close paren after IF cond");
                return null;
            }
            Statement body = Statement.getStatement(tokens);
            if(body == null) {
                System.err.println("Expected body of if");
                return null;
            }
            return new IfStatement(expr, body);

        }
        return null;
    }

    abstract void execute();
    abstract void assemble(Assembly assembly);

    public static class IfStatement extends Statement {

        BlnExpr condition;
        Statement body;

        public IfStatement(BlnExpr condition, Statement body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public void execute() {
            System.err.println("Cannot interpret IF yet...");
        }

        @Override
        public void assemble(Assembly assembly) {
            condition.assemble(assembly);
            assembly.popTo("t5");
            String label = assembly.getLabel("if");
            assembly.append("beqz t5 "+label);
            body.assemble(assembly);
            assembly.append(label + ":");
        }

    }
    
    public static class PrintStatement extends Statement {

        Expr expr;

        public PrintStatement(Expr expr) {
            this.expr = expr;
        }

        @Override
        public void execute() {
            System.out.println(expr.value());
        }

        @Override
        public void assemble(Assembly assembly) {
            expr.assemble(assembly);
            assembly.append("li a0 1");
            assembly.popTo("a1");
            assembly.append("ecall");
            assembly.append("li a0 11");
            assembly.append("li a1 13");
            assembly.append("ecall");
        }

    }

    public static class BlnDeclaration extends Statement {

        String ident;
        BlnExpr expr;

        public BlnDeclaration(String ident, BlnExpr expr) {
            this.ident = ident;
            this.expr = expr;
        }

        @Override
        public void execute() {
            Tryst.global.declare(ident, "BLN", expr);
            System.out.println("declared BLN " + ident);
        }

        @Override
        public void assemble(Assembly assembly) {
            Tryst.global.declare(ident, "BLN", expr);
            expr.assemble(assembly);
            assembly.popTo(Tryst.global.register(ident));
        }

    }

    public static class IntDeclaration extends Statement {

        String ident;
        IntExpr expr;

        public IntDeclaration(String ident, IntExpr expr) {
            this.ident = ident;
            this.expr = expr;
        }

        @Override
        public void execute() {
            Tryst.global.declare(ident, "INT", expr);
            System.out.println("declared INT " + ident);
        }

        @Override
        public void assemble(Assembly assembly) {
            Tryst.global.declare(ident, "INT", expr);
            expr.assemble(assembly);
            assembly.popTo(Tryst.global.register(ident));
        }

    }

    public static class BlnAssignment extends Statement {

        String ident;
        BlnExpr expr;

        public BlnAssignment(String ident, BlnExpr expr) {
            this.ident = ident;
            this.expr = expr;
        }

        @Override
        public void execute() {
            Tryst.global.declare(ident, "BLN", expr);
            System.out.println("assigned BLN " + ident);
        }

        @Override
        public void assemble(Assembly assembly) {
            expr.assemble(assembly);
            assembly.popTo(Tryst.global.register(ident));
        }

    }

    public static class IntAssignment extends Statement {

        String ident;
        IntExpr expr;

        public IntAssignment(String ident, IntExpr expr) {
            this.ident = ident;
            this.expr = expr;
        }

        @Override
        public void execute() {
            Tryst.global.declare(ident, "INT", expr);
            System.out.println("assigned INT " + ident);
        }

        @Override
        public void assemble(Assembly assembly) {
            expr.assemble(assembly);
            assembly.popTo(Tryst.global.register(ident));
        }

    }
    
}
