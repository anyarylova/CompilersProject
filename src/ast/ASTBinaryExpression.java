package ast;


public class ASTBinaryExpression extends ASTNode {
    private ASTNode left;
    private ASTNode right;
    private String operator;

    public ASTBinaryExpression(ASTNode left, ASTNode right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public ASTNode getLeft() {
        return left;
    }

    public ASTNode getRight() {
        return right;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
