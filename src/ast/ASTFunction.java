package ast;

public class ASTFunction extends ASTNode {
    private ASTIdentifier id;
    private ASTDeclaration declaration;
    private ASTStatementBlock body;

    public ASTFunction(ASTIdentifier id, ASTDeclaration declaration, ASTStatementBlock body) {
        this.id = id;
        this.declaration = declaration;
        this.body = body;
    }

    public ASTIdentifier getId() {
        return id;
    }

    public ASTDeclaration getDeclaration() {
        return declaration;
    }

    public ASTStatementBlock getBody() {
        return body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
