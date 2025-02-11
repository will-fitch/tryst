
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import tokens.TokenString;
import tokens.Tokenizer;

public class Tryst {

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Incorrect args length (Tryst.java:main)");
            System.exit(0);
        }

        File tryCode = new File(args[0]);

        Scanner sc = null;
        try {
            sc = new Scanner(tryCode);
        } catch (FileNotFoundException ex) {
            System.exit(0);
        }

        String source = "";
        while(sc.hasNextLine()) {
            source += sc.nextLine() + "\n";
        }
        TokenString tokens = Tokenizer.tokenize(source);
        System.out.println("#" + tokens.toString());
        while(tokens.hasNext()) {
            Statement stmt = Statement.getStatement(tokens);
            if(stmt == null) {
                System.out.println("null");
            } else {
                stmt.assemble(assembly);
            }
        }
        assembly.exit();
        System.out.println(assembly);
        
    }

    //STATIC:
    public static final Scope global = new Scope();
    public static final Assembly assembly = new Assembly();

}
