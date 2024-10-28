package ast;

public class ASTArrayAccess extends ASTNode {
    private ASTNode array;
    private ASTNode index;

    public ASTArrayAccess(ASTNode array, ASTNode index) {
        this.array = array;
        this.index = index;
    }

    public ASTNode getArray() {
        return array;
    }

    public ASTNode getIndex() {
        return index;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
