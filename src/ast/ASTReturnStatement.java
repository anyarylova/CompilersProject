package ast;

public class ASTReturnStatement extends ASTNode {
    private ASTNode expression;

    public ASTReturnStatement(ASTNode expression) {
        this.expression = expression;
    }

    public ASTNode getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

