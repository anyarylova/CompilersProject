package ast;

public class ASTIntegerLiteral extends ASTNode {
    private int value;

    public ASTIntegerLiteral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
