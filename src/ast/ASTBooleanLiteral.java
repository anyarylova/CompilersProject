package ast;

public class ASTBooleanLiteral extends ASTNode {
    private boolean value;

    public ASTBooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
