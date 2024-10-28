/* Es 1 - Ex 1 (Main) */
import java.io.*;
import ast.*;
   
public class Main {
  static public void main(String argv[]) {    
    try {
      /* Scanner instantiation */
      Yylex l = new Yylex(new FileReader(argv[0]));
      /* Parser instantiation */
      parser p = new parser(l);
      /* Start the parser */
      ASTNode ast = (ASTNode) p.parse().value;  
      ASTVisitor printVisitor = new PrintVisitor();
      ast.accept(printVisitor);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}


