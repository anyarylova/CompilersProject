package ast;

public class ASTRealLiteral extends ASTNode {
    private double value;

    public ASTRealLiteral(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
