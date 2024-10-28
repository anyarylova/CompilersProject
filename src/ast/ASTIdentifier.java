package ast;

public class ASTIdentifier extends ASTNode {
    private String name;

    public ASTIdentifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
