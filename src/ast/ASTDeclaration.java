package ast;

public class ASTDeclaration extends ASTNode {
    private ASTIdentifier id;
    private ASTType type;
    private ASTNode expression;

    public ASTDeclaration(ASTIdentifier id, ASTType type, ASTNode expression) {
        this.id = id;
        this.type = type;
        this.expression = expression;
    }

    public ASTIdentifier getId() {
        return id;
    }

    public ASTType getType() {
        return type;
    }

    public ASTNode getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
