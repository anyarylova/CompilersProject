package ast;

public interface ASTVisitor {
    // Visit methods for program structure
    void visit(ASTProgram node);
    void visit(ASTProgramElements node);
    
    // Visit methods for declarations
    void visit(ASTDeclaration node);
    
    // Visit methods for expressions
    void visit(ASTBinaryExpression node);
    void visit(ASTUnaryExpression node);
    void visit(ASTIntegerLiteral node);
    void visit(ASTRealLiteral node);
    void visit(ASTBooleanLiteral node);
    void visit(ASTIdentifier node);
    void visit(ASTArrayAccess node);
    void visit(ASTArrayLiteral node);
    void visit(ASTExpressionList node);
    
    // Visit methods for statements
    void visit(ASTAssignment node);
    void visit(ASTReturnStatement node);
    void visit(ASTIfStatement node);
    void visit(ASTWhileLoop node);
    void visit(ASTForLoop node);
    void visit(ASTStatementBlock node);
    
    // Visit methods for functions
    void visit(ASTFunction node);
    
    // Visit methods for types
    void visit(ASTType node);
    void visit(ASTArrayType node);
    void visit(ASTRecordType node);
}
