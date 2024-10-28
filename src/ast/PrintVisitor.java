package ast;

public class PrintVisitor implements ASTVisitor {

    @Override
    public void visit(ASTProgram node) {
        System.out.println("Visiting Program");
        node.getElements().accept(this);  // Traverse program elements
    }

    @Override
    public void visit(ASTProgramElements node) {
        System.out.println("Visiting Program Elements");
        for (ASTNode element : node.getElements()) {
            element.accept(this);  // Traverse each program element
        }
    }

    @Override
    public void visit(ASTDeclaration node) {
        System.out.println("Visiting Declaration: " + node.getId().getName());
        node.getType().accept(this);      // Visit the type
        node.getExpression().accept(this);  // Visit the initial expression
    }

    @Override
    public void visit(ASTBinaryExpression node) {
        System.out.println("Visiting Binary Expression: " + node.getOperator());
        node.getLeft().accept(this);  // Visit left operand
        node.getRight().accept(this); // Visit right operand
    }

    @Override
    public void visit(ASTUnaryExpression node) {
        System.out.println("Visiting Unary Expression: " + node.getOperator());
        node.getExpression().accept(this);  // Visit the inner expression
    }

    @Override
    public void visit(ASTIntegerLiteral node) {
        System.out.println("Visiting Integer Literal: " + node.getValue());
    }

    @Override
    public void visit(ASTRealLiteral node) {
        System.out.println("Visiting Real Literal: " + node.getValue());
    }

    @Override
    public void visit(ASTBooleanLiteral node) {
        System.out.println("Visiting Boolean Literal: " + node.getValue());
    }

    @Override
    public void visit(ASTIdentifier node) {
        System.out.println("Visiting Identifier: " + node.getName());
    }

    @Override
    public void visit(ASTArrayAccess node) {
        System.out.println("Visiting Array Access");
        node.getArray().accept(this);  // Visit the array
        node.getIndex().accept(this);  // Visit the index expression
    }

    @Override
    public void visit(ASTArrayLiteral node) {
        System.out.println("Visiting Array Literal");
        node.getElements().accept(this);  // Visit the elements of the array
    }

    @Override
    public void visit(ASTExpressionList node) {
        System.out.println("Visiting Expression List");
        for (ASTNode expression : node.getExpressions()) {
            expression.accept(this);  // Visit each expression in the list
        }
    }

    @Override
    public void visit(ASTAssignment node) {
        System.out.println("Visiting Assignment");
        node.getVariable().accept(this);  // Visit the variable
        node.getExpression().accept(this);  // Visit the assigned expression
    }

    @Override
    public void visit(ASTReturnStatement node) {
        System.out.println("Visiting Return Statement");
        node.getExpression().accept(this);  // Visit the return expression
    }

    @Override
    public void visit(ASTIfStatement node) {
        System.out.println("Visiting If Statement");
        node.getCondition().accept(this);  // Visit the condition
        System.out.println("Visiting Then Block");
        node.getThenBlock().accept(this);  // Visit the "then" block
        if (node.getElseBlock() != null) {
            System.out.println("Visiting Else Block");
            node.getElseBlock().accept(this);  // Visit the "else" block
        }
    }

    @Override
    public void visit(ASTWhileLoop node) {
        System.out.println("Visiting While Loop");
        node.getCondition().accept(this);  // Visit the loop condition
        node.getBlock().accept(this);  // Visit the loop body
    }

    @Override
    public void visit(ASTForLoop node) {
        System.out.println("Visiting For Loop: " + node.getVariable().getName());
        node.getFrom().accept(this);  // Visit the "from" expression
        node.getTo().accept(this);    // Visit the "to" expression
        node.getBlock().accept(this);  // Visit the loop body
    }

    @Override
    public void visit(ASTStatementBlock node) {
        System.out.println("Visiting Statement Block");
        for (ASTNode statement : node.getStatements()) {
            statement.accept(this);  // Visit each statement in the block
        }
    }

    @Override
    public void visit(ASTFunction node) {
        System.out.println("Visiting Function: " + node.getId().getName());
        node.getDeclaration().accept(this);  // Visit the function's declaration
        node.getBody().accept(this);  // Visit the function's body
    }

    @Override
    public void visit(ASTType node) {
        System.out.println("Visiting Type: " + node.getTypeName());
    }

    @Override
    public void visit(ASTArrayType node) {
        System.out.println("Visiting Array Type");
        node.getElementType().accept(this);  // Visit the element type
    }

    @Override
    public void visit(ASTRecordType node) {
        System.out.println("Visiting Record Type");
        node.getDeclaration().accept(this);  // Visit the record declaration
    }
}
