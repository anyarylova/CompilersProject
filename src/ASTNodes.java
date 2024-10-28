

import java.util.List;


abstract class ASTNode {}

// Program node: The root node containing a list of children
class ProgramNode extends ASTNode {
    List<ASTNode> children;

    public ProgramNode(List<ASTNode> children) {
        this.children = children;
    }
}

// Declaration node: Represents variable declarations
class DeclarationNode extends ASTNode {
    String identifier;
    TypeNode type;
    ExpressionNode expr;

    public DeclarationNode(String identifier, TypeNode type, ExpressionNode expr) {
        this.identifier = identifier;
        this.type = type;
        this.expr = expr;
    }
}

// Abstract class for all types of statements (assignments, if-else, loops, etc.)
abstract class StatementNode extends ASTNode { }

// Abstract class for all expressions (arithmetic, logical, values, etc.)
abstract class ExpressionNode extends ASTNode { }

// Abstract class for all types (integer, boolean, real, array, etc.)
abstract class TypeNode extends ASTNode { }

// Function node: Represents function/routine definitions
class FunctionNode extends ASTNode {
    String identifier;
    DeclarationNode declaration;
    StatementNode statement;

    public FunctionNode(String identifier, DeclarationNode declaration, StatementNode statement) {
        this.identifier = identifier;
        this.declaration = declaration;
        this.statement = statement;
    }
}

// Integer type node
class IntegerTypeNode extends TypeNode { }

// Boolean type node
class BooleanTypeNode extends TypeNode { }

// Real type node
class RealTypeNode extends TypeNode { }

// Array type node: Represents arrays with a size and element type
class ArrayTypeNode extends TypeNode {
    int size;
    TypeNode elementType;

    public ArrayTypeNode(int size, TypeNode elementType) {
        this.size = size;
        this.elementType = elementType;
    }
}

// Record type node: Represents record types (like structs in C)
class RecordTypeNode extends TypeNode {
    List<DeclarationNode> fields;

    public RecordTypeNode(List<DeclarationNode> fields) {
        this.fields = fields;
    }
}

// Binary operation node: Represents binary operations like addition, subtraction, etc.
class BinaryOpNode extends ExpressionNode {
    ExpressionNode left, right;
    String operator;

    public BinaryOpNode(ExpressionNode left, ExpressionNode right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
}

// Unary operation node: Represents unary operations like NOT, etc.
class UnaryOpNode extends ExpressionNode {
    ExpressionNode expr;
    String operator;

    public UnaryOpNode(ExpressionNode expr, String operator) {
        this.expr = expr;
        this.operator = operator;
    }
}

// Number node: Represents an integer constant
class NumberNode extends ExpressionNode {
    int value;

    public NumberNode(int value) {
        this.value = value;
    }
}

// Real number node: Represents a real (floating-point) constant
class RealNode extends ExpressionNode {
    double value;

    public RealNode(double value) {
        this.value = value;
    }
}

// Identifier node: Represents an identifier (e.g., a variable name)
class IdentifierNode extends ExpressionNode {
    String name;

    public IdentifierNode(String name) {
        this.name = name;
    }
}

// Boolean node: Represents a boolean constant (true or false)
class BooleanNode extends ExpressionNode {
    boolean value;

    public BooleanNode(boolean value) {
        this.value = value;
    }
}

// Assignment node: Represents assignment statements (e.g., x := expr)
class AssignmentNode extends StatementNode {
    IdentifierNode id;
    ExpressionNode expr;

    public AssignmentNode(IdentifierNode id, ExpressionNode expr) {
        this.id = id;
        this.expr = expr;
    }
}

// If-else node: Represents if-else statements
class IfElseNode extends StatementNode {
    ExpressionNode condition;
    StatementNode thenStmt;
    StatementNode elseStmt;

    public IfElseNode(ExpressionNode condition, StatementNode thenStmt, StatementNode elseStmt) {
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }
}

// While loop node: Represents while-loop statements
class WhileLoopNode extends StatementNode {
    ExpressionNode condition;
    StatementNode body;

    public WhileLoopNode(ExpressionNode condition, StatementNode body) {
        this.condition = condition;
        this.body = body;
    }
}

// For loop node: Represents for-loop statements
class ForLoopNode extends StatementNode {
    IdentifierNode iterator;
    ExpressionNode start, end;
    StatementNode body;

    public ForLoopNode(IdentifierNode iterator, ExpressionNode start, ExpressionNode end, StatementNode body) {
        this.iterator = iterator;
        this.start = start;
        this.end = end;
        this.body = body;
    }
}

// Return node: Represents return statements in functions
class ReturnNode extends StatementNode {
    ExpressionNode expr;

    public ReturnNode(ExpressionNode expr) {
        this.expr = expr;
    }
}
