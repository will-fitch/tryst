package tokens;

import java.util.regex.Pattern;

public class Token {
    public static enum Type {
        IF("if"),
        BLN("bln"), INT("int"),
        SEMI(";"), COLON(":"), LEFT_ASSIGN("<~"),
        TRUE("true"), FALSE("false"),
        PRINT("print"),
        OPEN_PAREN("\\("), CLOSE_PAREN("\\)"),
        OPEN_BRACE("\\{"), CLOSE_BRACE("\\}"),
        PLUS("\\+"), MINUS("-"), STAR("\\*"), F_SLASH("/"),
        EQUALS("="), AND("and"), OR("or"), EXCLAME("!"),
        NUM_LITERAL("\\d[_\\d]*", true), IDENTIFIER("[_a-zA-Z][_a-zA-Z\\d]*", true);

        public final Pattern pattern;
        public final boolean unique;
        Type(String regex) {
            this.pattern = Pattern.compile(regex);
            this.unique = false;
        }

        Type(String regex, boolean unique) {
            this.pattern = Pattern.compile(regex);
            this.unique = true;
        }

        public Token newToken(String text) {
            return new Token(this, text);
        }
    }


    //INSTANCE:
    public final Type type;
    public final String text;

    private Token(Type type, String text) {
        this.type = type;

        if(type == Type.NUM_LITERAL) {
            String numtext = "";
            for(String part : text.split("_")) numtext += part;
            this.text = numtext;
        } else {
            this.text = text;
        }
    }

    public boolean isType(Type type) {
        return this.type == type;
    }

    public boolean isType(Type... types) {
        for(Type type : types) {
            if(isType(type)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        if(type.unique) return type.toString() + "(" + text + ")";
        return type.toString();
    }
}
