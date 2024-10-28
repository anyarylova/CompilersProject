package ast;

public class ASTUnaryExpression extends ASTNode {
    private String operator;
    private ASTNode expression;

    public ASTUnaryExpression(String operator, ASTNode expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public String getOperator() {
        return operator;
    }

    public ASTNode getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
