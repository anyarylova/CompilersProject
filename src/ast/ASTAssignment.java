package ast;

public class ASTAssignment extends ASTNode {
    private ASTNode variable;
    private ASTNode expression;

    public ASTAssignment(ASTNode variable, ASTNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    public ASTNode getVariable() {
        return variable;
    }

    public ASTNode getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
