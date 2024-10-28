package ast;

import java.util.List;
import java.util.ArrayList;

public class ASTProgramElements extends ASTNode {
    private List<ASTNode> elements;

    public ASTProgramElements(ASTNode firstElement) {
        this.elements = new ArrayList<>();
        this.elements.add(firstElement);
    }

    public ASTProgramElements add(ASTNode element) {
        this.elements.add(element);
        return this;
    }

    public List<ASTNode> getElements() {
        return elements;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}