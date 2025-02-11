package tokens;

import java.util.regex.Matcher;

public class Tokenizer {

    /**
     * converts Tryst code to a series of Tokens
     * 
     * @param line a Tryst sourcecode String to be tokenized
     * @return TokenString object representing the parsed tokens in order
     */
    public static TokenString tokenize(String line) {

        TokenString tokens = new TokenString();

        while(line.length() != 0) {
            //whitespace is for chumps
            line = line.strip();

            // ignore multi-line comment
            // starting with `[`
            // ending with `]`
            if(line.startsWith("`[`")) {
                line = line.substring(3);
                int closeIndex = line.indexOf("`]`");
                if(closeIndex == -1) return tokens;
                line = line.substring(closeIndex+3);
                continue;
            }
            //ignore single-line comment
            // starting with ``
            else if(line.startsWith("``")) {
                line = line.substring(2);
                int closeIndex = line.indexOf('\n');
                System.out.println("CLOSE INDEX: "+closeIndex);
                if(closeIndex == -1) return tokens;
                line = line.substring(closeIndex);
                continue;
            }

            //get next token of line, update line
            line = next(line, tokens);
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
