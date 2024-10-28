import java.util.List;

abstract class ASTNode {
    ASTNode parent;

    public ASTNode getParent() {
        return parent;
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }
}

class ProgramNode extends ASTNode {
    private List<ASTNode> children;

    public ProgramNode(List<ASTNode> children) {
        this.children = children;
    }

    public List<ASTNode> getChildren() {
        return children;
    }
}

class DeclarationNode extends ASTNode {
    private String identifier;
    private TypeNode type;
    private ExpressionNode expr;

    public DeclarationNode(String identifier, TypeNode type, ExpressionNode expr) {
        this.identifier = identifier;
        this.type = type;
        this.expr = expr;
    }

    public String getIdentifier() {
        return identifier;
    }

    public TypeNode getType() {
        return type;
    }

    public ExpressionNode getExpression() {
        return expr;
    }
}

abstract class StatementNode extends ASTNode { }
abstract class ExpressionNode extends ASTNode { }
abstract class TypeNode extends ASTNode { }

class FunctionNode extends ASTNode {
    private String identifier;
    private DeclarationNode declaration;
    private StatementNode statement;

    public FunctionNode(String identifier, DeclarationNode declaration, StatementNode statement) {
        this.identifier = identifier;
        this.declaration = declaration;
        this.statement = statement;
    }

    public String getIdentifier() {
        return identifier;
    }

    public DeclarationNode getDeclaration() {
        return declaration;
    }

    public StatementNode getStatement() {
        return statement;
    }
}

class IntegerTypeNode extends TypeNode { }
class BooleanTypeNode extends TypeNode { }
class RealTypeNode extends TypeNode { }

class ArrayTypeNode extends TypeNode {
    private int size;
    private TypeNode elementType;

    public ArrayTypeNode(int size, TypeNode elementType) {
        this.size = size;
        this.elementType = elementType;
    }

    public int getSize() {
        return size;
    }

    public TypeNode getElementType() {
        return elementType;
    }
}

class RecordTypeNode extends TypeNode {
    private List<DeclarationNode> fields;

    public RecordTypeNode(List<DeclarationNode> fields) {
        this.fields = fields;
    }

    public List<DeclarationNode> getFields() {
        return fields;
    }
}

class BinaryOpNode extends ExpressionNode {
    private ExpressionNode left;
    private ExpressionNode right;
    private String operator;

    public BinaryOpNode(ExpressionNode left, ExpressionNode right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public void setLeft(ExpressionNode left) {
        this.left = left;
    }

    public ExpressionNode getRight() {
        return right;
    }

    public void setRight(ExpressionNode right) {
        this.right = right;
    }

    public String getOperator() {
        return operator;
    }
}

class UnaryOpNode extends ExpressionNode {
    private ExpressionNode expr;
    private String operator;

    public UnaryOpNode(ExpressionNode expr, String operator) {
        this.expr = expr;
        this.operator = operator;
    }

    public ExpressionNode getExpr() {
        return expr;
    }

    public String getOperator() {
        return operator;
    }
}

class IdentifierNode extends ExpressionNode {
    private String name;

    public IdentifierNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class NumberNode extends ExpressionNode {
    private int value;

    public NumberNode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

// Real number node: Represents a real (floating-point) constant
class RealNode extends ExpressionNode {
    double value;

    public RealNode(double value) {
        this.value = value;
    }
}

// Boolean node: Represents a boolean constant (true or false)
class BooleanNode extends ExpressionNode {
    boolean value;

    public BooleanNode(boolean value) {
        this.value = value;
    }
}

class ReturnNode extends StatementNode {
    private ExpressionNode expr;

    public ReturnNode(ExpressionNode expr) {
        this.expr = expr;
    }

    public ExpressionNode getExpr() {
        return expr;
    }
}

class AssignmentNode extends StatementNode {
    private IdentifierNode identifier;
    private ExpressionNode expr;

    public AssignmentNode(IdentifierNode identifier, ExpressionNode expr) {
        this.identifier = identifier;
        this.expr = expr;
    }

    public IdentifierNode getIdentifier() {
        return identifier;
    }

    public ExpressionNode getExpression() {
        return expr;
    }
}

class IfElseNode extends StatementNode {
    private ExpressionNode condition;
    private StatementNode thenStmt;
    private StatementNode elseStmt;

    public IfElseNode(ExpressionNode condition, StatementNode thenStmt, StatementNode elseStmt) {
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public StatementNode getThenStmt() {
        return thenStmt;
    }

    public StatementNode getElseStmt() {
        return elseStmt;
    }
}

class WhileLoopNode extends StatementNode {
    private ExpressionNode condition;
    private StatementNode body;

    public WhileLoopNode(ExpressionNode condition, StatementNode body) {
        this.condition = condition;
        this.body = body;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public StatementNode getBody() {
        return body;
    }
}

class ForLoopNode extends StatementNode {
    private IdentifierNode iterator;
    private ExpressionNode start;
    private ExpressionNode end;
    private StatementNode body;

    public ForLoopNode(IdentifierNode iterator, ExpressionNode start, ExpressionNode end, StatementNode body) {
        this.iterator = iterator;
        this.start = start;
        this.end = end;
        this.body = body;
    }

    public IdentifierNode getIterator() {
        return iterator;
    }

    public ExpressionNode getStart() {
        return start;
    }

    public ExpressionNode getEnd() {
        return end;
    }

    public StatementNode getBody() {
        return body;
    }
}
