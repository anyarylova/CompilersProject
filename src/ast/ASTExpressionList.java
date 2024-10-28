package ast;

import java.util.List;
import java.util.ArrayList;

public class ASTExpressionList extends ASTNode {
    private List<ASTNode> expressions;

    public ASTExpressionList(ASTNode firstExpression) {
        this.expressions = new ArrayList<>();
        this.expressions.add(firstExpression);
    }

    public ASTExpressionList add(ASTNode expression) {
        this.expressions.add(expression);
        return this;
    }

    public List<ASTNode> getExpressions() {
        return expressions;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
