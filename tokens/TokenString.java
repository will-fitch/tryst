package tokens;

import java.util.ArrayList;
import java.util.List;

public class TokenString {
    
    private final List<Token> tokens;
    private int index;
    
    public TokenString() {
        tokens = new ArrayList<>();
        index = 0;
    }

    public void append(Token token) {
        tokens.add(token);
    }

    public int size() {
        return tokens.size();
    }

    public boolean hasNext() {
        return index < tokens.size();
    }

    public Token peek() {
        if(!hasNext()) return null;
        return tokens.get(index);
    }

    public Token pop() {
        if(!hasNext()) return null;
        index++;
        return tokens.get(index-1);
    }

    public void unpop() {
        index--;
    }

    public int index() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        String str = "[";
        for(Token t : tokens) {
            str += t.toString();
            str += ", ";
        }
        str = str.substring(0, str.length()-2);
        return str + "]";
    }

}
