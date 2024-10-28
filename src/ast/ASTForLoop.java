package ast;

public class ASTForLoop extends ASTNode {
    private ASTIdentifier variable;
    private ASTNode from;
    private ASTNode to;
    private ASTStatementBlock block;

    public ASTForLoop(ASTIdentifier variable, ASTNode from, ASTNode to, ASTStatementBlock block) {
        this.variable = variable;
        this.from = from;
        this.to = to;
        this.block = block;
    }

    public ASTIdentifier getVariable() {
        return variable;
    }

    public ASTNode getFrom() {
        return from;
    }

    public ASTNode getTo() {
        return to;
    }

    public ASTStatementBlock getBlock() {
        return block;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
