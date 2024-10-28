import java.util.*;

public class SemanticAnalyzer {
  private Map<String, String> symbolTable = new HashMap<>();
  private Set<String> usedVariables = new HashSet<>();
  private boolean insideFunction = false;

  public void analyze(ProgramNode program) {
      visitProgram(program);
      removeUnusedVariables(program);
  }

  private void visitProgram(ProgramNode node) {
      for (ASTNode child : node.getChildren()) {
          if (child instanceof DeclarationNode) {
              visitDeclaration((DeclarationNode) child);
          } else if (child instanceof FunctionNode) {
              visitFunction((FunctionNode) child);
          } else if (child instanceof StatementNode) {
              visitStatement((StatementNode) child);
          }
      }
  }

  private void visitDeclaration(DeclarationNode node) {
      String id = node.getIdentifier();
      if (symbolTable.containsKey(id)) {
          System.out.println("Error: Variable " + id + " is already declared.");
      } else {
          symbolTable.put(id, node.getType().getClass().getSimpleName());
          if (node.getExpression() != null) {
              visitExpression(node.getExpression());
          }
      }
  }

  private void visitFunction(FunctionNode node) {
      insideFunction = true;
      if (node.getDeclaration() != null) {
          visitDeclaration(node.getDeclaration());
      }
      if (node.getStatement() != null) {
          visitStatement(node.getStatement());
      }
      insideFunction = false;
  }

  private void visitStatement(StatementNode node) {
      if (node instanceof AssignmentNode) {
          visitAssignment((AssignmentNode) node);
      } else if (node instanceof IfElseNode) {
          visitIfElse((IfElseNode) node);
      } else if (node instanceof WhileLoopNode) {
          visitWhileLoop((WhileLoopNode) node);
      } else if (node instanceof ForLoopNode) {
          visitForLoop((ForLoopNode) node);
      } else if (node instanceof ReturnNode) {
          visitReturn((ReturnNode) node);
      }
  }

  private void visitAssignment(AssignmentNode node) {
      String id = node.getIdentifier().getName();
      if (!symbolTable.containsKey(id)) {
          System.out.println("Error: Variable " + id + " used before declaration.");
      } else {
          usedVariables.add(id);
          visitExpression(node.getExpression());
      }
  }

  private void visitIfElse(IfElseNode node) {
      visitExpression(node.getCondition());
      visitStatement(node.getThenStmt());
      if (node.getElseStmt() != null) {
          visitStatement(node.getElseStmt());
      }
  }

  private void visitWhileLoop(WhileLoopNode node) {
      visitExpression(node.getCondition());
      visitStatement(node.getBody());
  }

  private void visitForLoop(ForLoopNode node) {
      if (!symbolTable.containsKey(node.getIterator().getName())) {
          symbolTable.put(node.getIterator().getName(), "Integer");
      }
      visitExpression(node.getStart());
      visitExpression(node.getEnd());
      visitStatement(node.getBody());
  }

  private void visitReturn(ReturnNode node) {
      if (!insideFunction) {
          System.out.println("Error: Return statement used outside of a function.");
      } else if (node.getExpr() != null) {
          visitExpression(node.getExpr());
      }
  }

  private void visitExpression(ExpressionNode node) {
      if (node instanceof BinaryOpNode) {
          visitBinaryOp((BinaryOpNode) node);
      } else if (node instanceof UnaryOpNode) {
          visitUnaryOp((UnaryOpNode) node);
      } else if (node instanceof IdentifierNode) {
          visitIdentifier((IdentifierNode) node);
      }
  }

  private void visitBinaryOp(BinaryOpNode node) {
      visitExpression(node.getLeft());
      visitExpression(node.getRight());

      // Constant Expression Simplification (Folding)
      if (node.getLeft() instanceof NumberNode && node.getRight() instanceof NumberNode) {
          int leftValue = ((NumberNode) node.getLeft()).getValue();
          int rightValue = ((NumberNode) node.getRight()).getValue();
          int result = 0;

          switch (node.getOperator()) {
              case "+":
                  result = leftValue + rightValue;
                  break;
              case "-":
                  result = leftValue - rightValue;
                  break;
              case "*":
                  result = leftValue * rightValue;
                  break;
              case "/":
                  if (rightValue != 0) {
                      result = leftValue / rightValue;
                  } else {
                      System.out.println("Error: Division by zero.");
                  }
                  break;
          }

          // Replace the current node with the result as a NumberNode
          if (node.getParent() instanceof BinaryOpNode) {
              BinaryOpNode parent = (BinaryOpNode) node.getParent();
              if (parent.getLeft() == node) {
                  parent.setLeft(new NumberNode(result));
              } else if (parent.getRight() == node) {
                  parent.setRight(new NumberNode(result));
              }
          }
      }
  }

  private void visitUnaryOp(UnaryOpNode node) {
      visitExpression(node.getExpr());
  }

  private void visitIdentifier(IdentifierNode node) {
      if (!symbolTable.containsKey(node.getName())) {
          System.out.println("Error: Variable " + node.getName() + " used before declaration.");
      } else {
          usedVariables.add(node.getName());
      }
  }

  private void removeUnusedVariables(ProgramNode node) {
      List<ASTNode> children = node.getChildren();
      children.removeIf(child -> (child instanceof DeclarationNode) && !usedVariables.contains(((DeclarationNode) child).getIdentifier()));
  }
}
