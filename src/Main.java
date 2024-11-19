/* Main.java */
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

                // Print AST before optimization
                System.out.println("AST before optimization:");
                analyzer.printAST(ast, 0);

                // Analyze the AST for semantic errors and optimizations
                System.out.println("Running semantic analysis and optimizations...");
                analyzer.analyze(ast);

                // Print AST after optimization
                System.out.println("AST after optimization:");
                analyzer.printAST(ast, 0);

                // Generate code
                System.out.println("Generating code...");
                CodeGenerator codeGen = new CodeGenerator();
                codeGen.generateCode(ast);

                System.out.println("Compilation finished.");
            } else {
                System.out.println("Parsing failed: AST is null.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

