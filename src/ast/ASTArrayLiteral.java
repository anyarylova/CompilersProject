package ast;

public class ASTArrayLiteral extends ASTNode {
    private ASTExpressionList elements;

    public ASTArrayLiteral(ASTExpressionList elements) {
        this.elements = elements;
    }

    public ASTExpressionList getElements() {
        return elements;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
