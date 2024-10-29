import java.util.List;

abstract class ASTNode {
    private ASTNode parent;

    public ASTNode getParent() {
        return parent;
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }
}

/* Program Node */
class ProgramNode extends ASTNode {
    private List<ASTNode> children;

    public ProgramNode(List<ASTNode> children) {
        this.children = children;
        for (ASTNode child : children) {
            child.setParent(this);
        }
    }

    public List<ASTNode> getChildren() {
        return children;
    }

    public void setChildren(List<ASTNode> children) {
        this.children = children;
        for (ASTNode child : children) {
            child.setParent(this);
        }
    }
}

/* Declaration Node */
class DeclarationNode extends ASTNode {
    private String identifier;
    private TypeNode type;
    private ExpressionNode expr;

    public DeclarationNode(String identifier, TypeNode type, ExpressionNode expr) {
        this.identifier = identifier;
        this.type = type;
        this.expr = expr;
        if (type != null) type.setParent(this);
        if (expr != null) expr.setParent(this);
    }

    public String getIdentifier() {
        return identifier;
    }

    public TypeNode getType() {
        return type;
    }

    public void setType(TypeNode type) {
        this.type = type;
        if (type != null) type.setParent(this);
    }

    public ExpressionNode getExpression() {
        return expr;
    }

    public void setExpression(ExpressionNode expr) {
        this.expr = expr;
        if (expr != null) expr.setParent(this);
    }
}

/* Type Nodes */
abstract class TypeNode extends ASTNode { }

class IntegerTypeNode extends TypeNode { }

class BooleanTypeNode extends TypeNode { }

class RealTypeNode extends TypeNode { }

class ArrayTypeNode extends TypeNode {
    private int size;
    private TypeNode elementType;

    public ArrayTypeNode(int size, TypeNode elementType) {
        this.size = size;
        this.elementType = elementType;
        if (elementType != null) elementType.setParent(this);
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
        if (fields != null) {
            for (DeclarationNode field : fields) {
                field.setParent(this);
            }
        }
    }

    public List<DeclarationNode> getFields() {
        return fields;
    }
}

/* Expression Nodes */
abstract class ExpressionNode extends ASTNode { }

class NumberNode extends ExpressionNode {
    private int value;

    public NumberNode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

class RealNode extends ExpressionNode {
    private double value;

    public RealNode(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}

class BooleanNode extends ExpressionNode {
    private boolean value;

    public BooleanNode(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
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

class BinaryOpNode extends ExpressionNode {
    private ExpressionNode left;
    private ExpressionNode right;
    private String operator;

    public BinaryOpNode(ExpressionNode left, ExpressionNode right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        if (left != null) left.setParent(this);
        if (right != null) right.setParent(this);
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public void setLeft(ExpressionNode left) {
        this.left = left;
        if (left != null) left.setParent(this);
    }

    public ExpressionNode getRight() {
        return right;
    }

    public void setRight(ExpressionNode right) {
        this.right = right;
        if (right != null) right.setParent(this);
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
        if (expr != null) expr.setParent(this);
    }

    public ExpressionNode getExpr() {
        return expr;
    }

    public void setExpr(ExpressionNode expr) {
        this.expr = expr;
        if (expr != null) expr.setParent(this);
    }

    public String getOperator() {
        return operator;
    }
}

class ArrayAccessNode extends ExpressionNode {
    private ExpressionNode array;
    private ExpressionNode index;

    public ArrayAccessNode(ExpressionNode array, ExpressionNode index) {
        this.array = array;
        this.index = index;
        if (array != null) array.setParent(this);
        if (index != null) index.setParent(this);
    }

    public ExpressionNode getArray() {
        return array;
    }

    public void setArray(ExpressionNode array) {
        this.array = array;
        if (array != null) array.setParent(this);
    }

    public ExpressionNode getIndex() {
        return index;
    }

    public void setIndex(ExpressionNode index) {
        this.index = index;
        if (index != null) index.setParent(this);
    }
}

class FieldAccessNode extends ExpressionNode {
    private ExpressionNode record;
    private String fieldName;

    public FieldAccessNode(ExpressionNode record, String fieldName) {
        this.record = record;
        this.fieldName = fieldName;
        if (record != null) record.setParent(this);
    }

    public ExpressionNode getRecord() {
        return record;
    }

    public void setRecord(ExpressionNode record) {
        this.record = record;
        if (record != null) record.setParent(this);
    }

    public String getFieldName() {
        return fieldName;
    }
}

class FunctionCallNode extends ExpressionNode {
    private String functionName;
    private List<ExpressionNode> arguments;

    public FunctionCallNode(String functionName, List<ExpressionNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
        if (arguments != null) {
            for (ExpressionNode arg : arguments) {
                arg.setParent(this);
            }
        }
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<ExpressionNode> getArguments() {
        return arguments;
    }
}

/* Statement Nodes */
abstract class StatementNode extends ASTNode { }

class AssignmentNode extends StatementNode {
    private ExpressionNode variable;
    private ExpressionNode expr;

    public AssignmentNode(ExpressionNode variable, ExpressionNode expr) {
        this.variable = variable;
        this.expr = expr;
        if (variable != null) variable.setParent(this);
        if (expr != null) expr.setParent(this);
    }

    public ExpressionNode getVariable() {
        return variable;
    }

    public void setVariable(ExpressionNode variable) {
        this.variable = variable;
        if (variable != null) variable.setParent(this);
    }

    public ExpressionNode getExpression() {
        return expr;
    }

    public void setExpression(ExpressionNode expr) {
        this.expr = expr;
        if (expr != null) expr.setParent(this);
    }
}

class ReturnNode extends StatementNode {
    private ExpressionNode expr;

    public ReturnNode(ExpressionNode expr) {
        this.expr = expr;
        if (expr != null) expr.setParent(this);
    }

    public ExpressionNode getExpr() {
        return expr;
    }

    public void setExpr(ExpressionNode expr) {
        this.expr = expr;
        if (expr != null) expr.setParent(this);
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
        if (condition != null) condition.setParent(this);
        if (thenStmt != null) thenStmt.setParent(this);
        if (elseStmt != null) elseStmt.setParent(this);
    }

    public IfElseNode(ExpressionNode condition, StatementNode thenStmt) {
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = null;
        if (condition != null) condition.setParent(this);
        if (thenStmt != null) thenStmt.setParent(this);
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
        if (condition != null) condition.setParent(this);
    }

    public StatementNode getThenStmt() {
        return thenStmt;
    }

    public void setThenStmt(StatementNode thenStmt) {
        this.thenStmt = thenStmt;
        if (thenStmt != null) thenStmt.setParent(this);
    }

    public StatementNode getElseStmt() {
        return elseStmt;
    }

    public void setElseStmt(StatementNode elseStmt) {
        this.elseStmt = elseStmt;
        if (elseStmt != null) elseStmt.setParent(this);
    }
}

class WhileLoopNode extends StatementNode {
    private ExpressionNode condition;
    private StatementNode body;

    public WhileLoopNode(ExpressionNode condition, StatementNode body) {
        this.condition = condition;
        this.body = body;
        if (condition != null) condition.setParent(this);
        if (body != null) body.setParent(this);
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
        if (condition != null) condition.setParent(this);
    }

    public StatementNode getBody() {
        return body;
    }

    public void setBody(StatementNode body) {
        this.body = body;
        if (body != null) body.setParent(this);
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
        if (iterator != null) iterator.setParent(this);
        if (start != null) start.setParent(this);
        if (end != null) end.setParent(this);
        if (body != null) body.setParent(this);
    }

    public IdentifierNode getIterator() {
        return iterator;
    }

    public void setIterator(IdentifierNode iterator) {
        this.iterator = iterator;
        if (iterator != null) iterator.setParent(this);
    }

    public ExpressionNode getStart() {
        return start;
    }

    public void setStart(ExpressionNode start) {
        this.start = start;
        if (start != null) start.setParent(this);
    }

    public ExpressionNode getEnd() {
        return end;
    }

    public void setEnd(ExpressionNode end) {
        this.end = end;
        if (end != null) end.setParent(this);
    }

    public StatementNode getBody() {
        return body;
    }

    public void setBody(StatementNode body) {
        this.body = body;
        if (body != null) body.setParent(this);
    }
}

class StatementBlockNode extends StatementNode {
    private List<StatementNode> statements;

    public StatementBlockNode(List<StatementNode> statements) {
        this.statements = statements;
        if (statements != null) {
            for (StatementNode stmt : statements) {
                stmt.setParent(this);
            }
        }
    }

    public List<StatementNode> getStatements() {
        return statements;
    }
}

/* Function Node */
class FunctionNode extends ASTNode {
    private String identifier;
    private List<DeclarationNode> parameters;
    private TypeNode returnType;
    private StatementNode body;

    public FunctionNode(String identifier, List<DeclarationNode> parameters, TypeNode returnType, StatementNode body) {
        this.identifier = identifier;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
        if (parameters != null) {
            for (DeclarationNode param : parameters) {
                param.setParent(this);
            }
        }
        if (returnType != null) returnType.setParent(this);
        if (body != null) body.setParent(this);
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<DeclarationNode> getParameters() {
        return parameters;
    }

    public TypeNode getReturnType() {
        return returnType;
    }

    public StatementNode getBody() {
        return body;
    }
}

/* Empty Node */
class EmptyNode extends StatementNode {
    // Represents an empty statement or placeholder
}

class ExpressionListNode extends ExpressionNode {
    private List<ExpressionNode> expressions;

    public ExpressionListNode(List<ExpressionNode> expressions) {
        this.expressions = expressions;
        if (expressions != null) {
            for (ExpressionNode expr : expressions) {
                expr.setParent(this);
            }
        }
    }

    public List<ExpressionNode> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<ExpressionNode> expressions) {
        this.expressions = expressions;
        if (expressions != null) {
            for (ExpressionNode expr : expressions) {
                expr.setParent(this);
            }
        }
    }
}