package ast;

public class ASTIfStatement extends ASTNode {
    private ASTNode condition;
    private ASTStatementBlock thenBlock;
    private ASTStatementBlock elseBlock;

    public ASTIfStatement(ASTNode condition, ASTStatementBlock thenBlock, ASTStatementBlock elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTStatementBlock getThenBlock() {
        return thenBlock;
    }

    public ASTStatementBlock getElseBlock() {
        return elseBlock;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
