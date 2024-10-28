package ast;

public class ASTProgram extends ASTNode {
    private ASTProgramElements elements;

    public ASTProgram(ASTProgramElements elements) {
        this.elements = elements;
    }

    public ASTProgramElements getElements() {
        return elements;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

