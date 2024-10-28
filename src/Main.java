/* Es 1 - Ex 1 (Main) */
import java.io.*;
   
public class Main {
  static public void main(String argv[]) {    
    try {
      /* Scanner instantiation */
      Yylex l = new Yylex(new FileReader(argv[0]));
      /* Parser instantiation */
      parser p = new parser(l);
      /* Start the parser */
      ProgramNode ast = (ProgramNode) p.parse().value;
      if (ast != null) {
        // Create the semantic analyzer
        SemanticAnalyzer analyzer = new SemanticAnalyzer();

        // Analyze the AST for semantic errors and optimizations
        System.out.println("Running semantic analysis and optimizations...");
        analyzer.analyze(ast);
    } else {
        System.out.println("Parsing failed: AST is null.");
    }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}


