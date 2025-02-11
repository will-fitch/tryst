package tokens;

import java.util.regex.Matcher;

public class Tokenizer {

    public static TokenString tokenize(String line) {

        TokenString tokens = new TokenString();

        line = line.strip();
        while(line.length() != 0) {
            line = next(line, tokens);
            line = line.strip();
        }

        return tokens;

    }

    private static String next(String line, TokenString tokens) {

        line = line.strip();

        String maxMatchString = "";
        Token.Type maxMatchType = null;
        for(Token.Type type: Token.Type.values()) {
            Matcher matcher = type.pattern.matcher(line);
            if(!matcher.find()) continue;
            String match = matcher.group();
            if(!line.startsWith(match)) continue;
            if(match.length() > maxMatchString.length()) {
                maxMatchString = match;
                maxMatchType = type;
            }
        }
        if(maxMatchType == null) {
            System.out.println("NO MATCH");
            return line;
        }

        tokens.append(maxMatchType.newToken(maxMatchString));
        return line.substring(maxMatchString.length());

    }

}
