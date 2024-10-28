package ast;

public class ASTRecordType extends ASTType {
    private ASTDeclaration declaration;

    public ASTRecordType(ASTDeclaration declaration) {
        super("record");
        this.declaration = declaration;
    }

    public ASTDeclaration getDeclaration() {
        return declaration;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
