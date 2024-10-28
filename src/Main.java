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
      Object result = p.parse();
      ProgramNode ast = (ProgramNode) p.parse().value;
      System.out.println("Parsing completed. Generated AST:");
      printAST(ast, 0);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void printAST(ASTNode node, int indent) {
    if (node == null) return;

    // Indentation for better visual representation of the tree
    for (int i = 0; i < indent; i++) {
      System.out.print("  ");
    }

    // Print the node class name (could also include more details)
    System.out.println(node.getClass().getSimpleName());

    // Recursively print child nodes depending on the node type
    if (node instanceof ProgramNode) {
      for (ASTNode child : ((ProgramNode) node).children) {
        printAST(child, indent + 1);
      }
    } else if (node instanceof DeclarationNode) {
      DeclarationNode decl = (DeclarationNode) node;
      System.out.println("Identifier: " + decl.identifier);
      printAST(decl.type, indent + 1);
      printAST(decl.expr, indent + 1);
    } else if (node instanceof FunctionNode) {
      FunctionNode func = (FunctionNode) node;
      System.out.println("Function Identifier: " + func.identifier);
      printAST(func.declaration, indent + 1);
      printAST(func.statement, indent + 1);
    } else if (node instanceof IfElseNode) {
      IfElseNode ifElse = (IfElseNode) node;
      printAST(ifElse.condition, indent + 1);
      printAST(ifElse.thenStmt, indent + 1);
      printAST(ifElse.elseStmt, indent + 1);
    } else if (node instanceof WhileLoopNode) {
      WhileLoopNode whileNode = (WhileLoopNode) node;
      printAST(whileNode.condition, indent + 1);
      printAST(whileNode.body, indent + 1);
    } else if (node instanceof ForLoopNode) {
      ForLoopNode forNode = (ForLoopNode) node;
      printAST(forNode.iterator, indent + 1);
      printAST(forNode.start, indent + 1);
      printAST(forNode.end, indent + 1);
      printAST(forNode.body, indent + 1);
    } else if (node instanceof BinaryOpNode) {
      BinaryOpNode binOp = (BinaryOpNode) node;
      System.out.println("Operator: " + binOp.operator);
      printAST(binOp.left, indent + 1);
      printAST(binOp.right, indent + 1);
    } else if (node instanceof UnaryOpNode) {
      UnaryOpNode unOp = (UnaryOpNode) node;
      System.out.println("Operator: " + unOp.operator);
      printAST(unOp.expr, indent + 1);
    } else if (node instanceof AssignmentNode) {
      AssignmentNode assign = (AssignmentNode) node;
      printAST(assign.id, indent + 1);
      printAST(assign.expr, indent + 1);
    } else if (node instanceof IdentifierNode) {
      System.out.println("Identifier: " + ((IdentifierNode) node).name);
    } else if (node instanceof NumberNode) {
      System.out.println("Number: " + ((NumberNode) node).value);
    } else if (node instanceof RealNode) {
      System.out.println("Real: " + ((RealNode) node).value);
    } else if (node instanceof BooleanNode) {
      System.out.println("Boolean: " + ((BooleanNode) node).value);
    }
  }
}


