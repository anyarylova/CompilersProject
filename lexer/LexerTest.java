import java.io.FileReader;
import java.io.IOException;

public class LexerTest {
    public static void main(String[] args) {
        try {
            // Replace "input.txt" with the path to the test file
            FileReader reader = new FileReader(
                "input.txt");
            Lexer lexer = new Lexer(reader);

                // Get next token
                String token;
                while ((token = lexer.yylex())!= null) {
                    System.out.println("Token: " + token + ", Lexeme: " + lexer.yytext());
                }

                // Print the token
                
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
