package ast;

import java.util.List;
import java.util.ArrayList;

public class ASTStatementBlock extends ASTNode {
    private List<ASTNode> statements;

    public ASTStatementBlock(ASTNode firstStatement) {
        this.statements = new ArrayList<>();
        this.statements.add(firstStatement);
    }

    public ASTStatementBlock add(ASTNode statement) {
        this.statements.add(statement);
        return this;
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}