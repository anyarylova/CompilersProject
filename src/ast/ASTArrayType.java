package ast;

public class ASTArrayType extends ASTType {
    private int size;
    private ASTType elementType;

    public ASTArrayType(int size, ASTType elementType) {
        super("array");
        this.size = size;
        this.elementType = elementType;
    }

    public int getSize() {
        return size;
    }

    public ASTType getElementType() {
        return elementType;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}