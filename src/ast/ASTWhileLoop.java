package ast;

public class ASTWhileLoop extends ASTNode {
    private ASTNode condition;
    private ASTStatementBlock block;

    public ASTWhileLoop(ASTNode condition, ASTStatementBlock block) {
        this.condition = condition;
        this.block = block;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTStatementBlock getBlock() {
        return block;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
