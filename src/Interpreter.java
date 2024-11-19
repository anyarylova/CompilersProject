import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Environment {
    private Map<String, Object> variables;
    private Environment parent;

    public Environment(Environment parent) {
        this.variables = new HashMap<>();
        this.parent = parent;
    }

    // Define a variable in the current scope
    public void define(String name, Object value) {
        variables.put(name, value);
    }

    // Assign a value to a variable, searches in parent if not found in current scope
    public void assign(String name, Object value) {
        if (variables.containsKey(name)) {
            variables.put(name, value);
        } else if (parent != null) {
            parent.assign(name, value);
        } else {
            throw new RuntimeException("Undefined variable: " + name);
        }
    }

    // Get the value of a variable, searching in the current scope and parent scopes
    public Object get(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        } else if (parent != null) {
            return parent.get(name);
        } else {
            throw new RuntimeException("Undefined variable: " + name);
        }
    }
}

class Interpreter {

    private Environment globalEnv;

    public Interpreter() {
        this.globalEnv = new Environment(null); // Global environment
    }

    // Function to interpret a ProgramNode
    public void interpret(ProgramNode program) {
        // Handle all declarations and function definitions
        for (ASTNode node : program.getChildren()) {
            if (node instanceof DeclarationNode) {
                executeDeclaration((DeclarationNode) node, globalEnv);
            } else if (node instanceof FunctionNode) {
                executeFunctionDeclaration((FunctionNode) node, globalEnv);
            }
        }

        // Execute all statements
        for (ASTNode node : program.getChildren()) {
            if (node instanceof StatementNode) {
                execute((StatementNode) node, globalEnv);
            }
        }
    }

    // Evaluate an expression and return the result
    private Object evaluate(ExpressionNode expr, Environment env) {
        if (expr instanceof NumberNode) {
            return ((NumberNode) expr).getValue();
        } else if (expr instanceof BooleanNode) {
            return ((BooleanNode) expr).isValue();
        } else if (expr instanceof RealNode) {
            return ((RealNode) expr).getValue();
        } else if (expr instanceof IdentifierNode) {
            String name = ((IdentifierNode) expr).getName();
            return env.get(name);
        } else if (expr instanceof BinaryOpNode) {
            return evaluateBinaryOp((BinaryOpNode) expr, env);
        } else if (expr instanceof UnaryOpNode) {
            return evaluateUnaryOp((UnaryOpNode) expr, env);
        } else if (expr instanceof ArrayAccessNode) {
            return evaluateArrayAccess((ArrayAccessNode) expr, env);
        } else if (expr instanceof FunctionCallNode) {
            return evaluateFunctionCall((FunctionCallNode) expr, env);
        } else if (expr instanceof FieldAccessNode) {
            return evaluateFieldAccess((FieldAccessNode) expr, env);
        }
        throw new RuntimeException("Unknown expression: " + expr);
    }

    // Execute a statement
    private void execute(StatementNode stmt, Environment env) {
        if (stmt instanceof PrintNode) {
            executePrint((PrintNode) stmt, env);
        } else if (stmt instanceof AssignmentNode) {
            executeAssignment((AssignmentNode) stmt, env);
        } else if (stmt instanceof IfElseNode) {
            executeIfElse((IfElseNode) stmt, env);
        } else if (stmt instanceof WhileLoopNode) {
            executeWhileLoop((WhileLoopNode) stmt, env);
        } else if (stmt instanceof ForLoopNode) {
            executeForLoop((ForLoopNode) stmt, env);
        } else if (stmt instanceof ReturnNode) {
            ReturnNode returnNode = (ReturnNode) stmt;
            Object value = evaluate(returnNode.getExpr(), env);
            throw new ReturnException(value);
        } else if (stmt instanceof StatementBlockNode) {
            executeBlock((StatementBlockNode) stmt, new Environment(env));
        }
    }

    // Binary Operation evaluation
    private Object evaluateBinaryOp(BinaryOpNode node, Environment env) {
        Object left = evaluate(node.getLeft(), env);
        Object right = evaluate(node.getRight(), env);
        String operator = node.getOperator();

        // Promote operands to the same type if necessary
        if (left instanceof Integer && right instanceof Double) {
            left = ((Integer) left).doubleValue();
        } else if (left instanceof Double && right instanceof Integer) {
            right = ((Integer) right).doubleValue();
        }

        // Perform operation based on operand types
        if (left instanceof Integer && right instanceof Integer) {
            int l = (Integer) left;
            int r = (Integer) right;
            switch (operator) {
                case "+":
                    return l + r;
                case "-":
                    return l - r;
                case "*":
                    return l * r;
                case "/":
                    if (r == 0) {
                        throw new RuntimeException("Division by zero");
                    }
                    return l / r;
                case "%":
                    return l % r;
                case "==":
                    return l == r;
                case "!=":
                    return l != r;
                case "<":
                    return l < r;
                case ">":
                    return l > r;
                case "<=":
                    return l <= r;
                case ">=":
                    return l >= r;
                default:
                    throw new RuntimeException("Unknown operator: " + operator);
            }
        } else if (left instanceof Double && right instanceof Double) {
            double l = (Double) left;
            double r = (Double) right;
            switch (operator) {
                case "+":
                    return l + r;
                case "-":
                    return l - r;
                case "*":
                    return l * r;
                case "/":
                    if (r == 0.0) {
                        throw new RuntimeException("Division by zero");
                    }
                    return l / r;
                case "==":
                    return l == r;
                case "!=":
                    return l != r;
                case "<":
                    return l < r;
                case ">":
                    return l > r;
                case "<=":
                    return l <= r;
                case ">=":
                    return l >= r;
                default:
                    throw new RuntimeException("Unknown operator: " + operator);
            }
        } else if (left instanceof Boolean && right instanceof Boolean) {
            boolean l = (Boolean) left;
            boolean r = (Boolean) right;
            switch (operator) {
                case "AND":
                    return l && r;
                case "OR":
                    return l || r;
                default:
                    throw new RuntimeException("Unknown boolean operator: " + operator);
            }
        } else {
            throw new RuntimeException("Unsupported operand types for operator " + operator);
        }
    }

    // Unary Operation evaluation
    private Object evaluateUnaryOp(UnaryOpNode node, Environment env) {
        Object exprValue = evaluate(node.getExpr(), env);
        String operator = node.getOperator();

        if (exprValue instanceof Integer) {
            int value = (Integer) exprValue;
            switch (operator) {
                case "-":
                    return -value;
                default:
                    throw new RuntimeException("Unknown unary operator: " + operator);
            }
        } else if (exprValue instanceof Double) {
            double value = (Double) exprValue;
            switch (operator) {
                case "-":
                    return -value;
                default:
                    throw new RuntimeException("Unknown unary operator: " + operator);
            }
        } else if (exprValue instanceof Boolean) {
            boolean value = (Boolean) exprValue;
            switch (operator) {
                case "NOT":
                    return !value;
                default:
                    throw new RuntimeException("Unknown unary operator: " + operator);
            }
        } else {
            throw new RuntimeException("Unsupported type for unary operator " + operator);
        }
    }

    // Assignment execution
    private void executeAssignment(AssignmentNode node, Environment env) {
        if (node.getVariable() instanceof IdentifierNode) {
            String name = ((IdentifierNode) node.getVariable()).getName();
            Object value = evaluate(node.getExpression(), env);
            Object varValue = env.get(name);

            if (varValue instanceof Boolean && value instanceof Integer) {
                // Convert Integer to Boolean
                boolean boolValue = ((Integer) value) != 0;
                env.assign(name, boolValue);
            } else if (varValue instanceof Integer && value instanceof Double) {
                // Convert Double to Integer by truncation
                int intValue = ((Double) value).intValue();
                env.assign(name, intValue);
            } else if (varValue instanceof Double && value instanceof Integer) {
                // Promote Integer to Double
                env.assign(name, ((Integer) value).doubleValue());
            } else if (!varValue.getClass().equals(value.getClass())) {
                throw new RuntimeException("Type mismatch: Cannot assign " + value.getClass().getSimpleName() + " to " + varValue.getClass().getSimpleName());
            } else {
                env.assign(name, value);
            }
        } else if (node.getVariable() instanceof ArrayAccessNode) {
            ArrayAccessNode arrayAccess = (ArrayAccessNode) node.getVariable();
            Object arrayObj = evaluate(arrayAccess.getArray(), env);
            if (!(arrayObj instanceof Object[])) {
                throw new RuntimeException("Type Error: Attempting to index a non-array type.");
            }
            Object[] array = (Object[]) arrayObj;
            int index = (int) evaluate(arrayAccess.getIndex(), env);
            Object value = evaluate(node.getExpression(), env);

            // Bounds checking
            if (index < 0 || index >= array.length) {
                throw new RuntimeException("Array Index Out of Bounds: " + index);
            }

            array[index] = value;
        } else if (node.getVariable() instanceof FieldAccessNode) {
            FieldAccessNode fieldAccess = (FieldAccessNode) node.getVariable();

            // Get the record from environment
            Object recordObj = evaluate(fieldAccess.getRecord(), env);
            if (!(recordObj instanceof Map)) {
                throw new RuntimeException("Cannot assign to field of non-record type");
            }

            Map<String, Object> record = (Map<String, Object>) recordObj;

            Object value = evaluate(node.getExpression(), env);
            record.put(fieldAccess.getFieldName(), value);
        } else {
            throw new RuntimeException("Unsupported assignment target");
        }
    }

    // If-Else execution
    private void executeIfElse(IfElseNode node, Environment env) {
        Object conditionValue = evaluate(node.getCondition(), env);
        if (!(conditionValue instanceof Boolean)) {
            throw new RuntimeException("Condition in if statement must be boolean");
        }
        if ((Boolean) conditionValue) {
            execute(node.getThenStmt(), env);
        } else if (node.getElseStmt() != null) {
            execute(node.getElseStmt(), env);
        }
    }

    // While Loop execution
    private void executeWhileLoop(WhileLoopNode node, Environment env) {
        Object conditionValue = evaluate(node.getCondition(), env);
        if (!(conditionValue instanceof Boolean)) {
            throw new RuntimeException("Condition in while loop must be boolean");
        }
        while ((Boolean) conditionValue) {
            execute(node.getBody(), env);
            conditionValue = evaluate(node.getCondition(), env);
            if (!(conditionValue instanceof Boolean)) {
                throw new RuntimeException("Condition in while loop must be boolean");
            }
        }
    }

    // For Loop execution
    private void executeForLoop(ForLoopNode node, Environment env) {
        Environment loopEnv = new Environment(env);
        String iterator = node.getIterator().getName();
        Object startValue = evaluate(node.getStart(), env);
        Object endValue = evaluate(node.getEnd(), env);

        if (!(startValue instanceof Integer) || !(endValue instanceof Integer)) {
            throw new RuntimeException("Start and end values in for loop must be integers");
        }

        loopEnv.define(iterator, startValue);
        int end = (Integer) endValue;

        while ((Integer) loopEnv.get(iterator) <= end) {
            execute(node.getBody(), loopEnv);
            int curr = (Integer) loopEnv.get(iterator);
            loopEnv.assign(iterator, curr + 1);
        }
    }

    // Block of statements execution (with a new scope)
    private void executeBlock(StatementBlockNode block, Environment env) {
        for (StatementNode stmt : block.getStatements()) {
            execute(stmt, env);
        }
    }

    // Variable declaration execution
    private void executeDeclaration(DeclarationNode node, Environment env) {
        String name = node.getIdentifier();
        Object value = null;

        if (node.getType() instanceof RecordTypeNode) {
            Map<String, Object> record = new HashMap<>();
            RecordTypeNode recordType = (RecordTypeNode) node.getType();

            for (DeclarationNode field : recordType.getFields()) {
                Object fieldValue = getDefaultValue(field.getType());
                record.put(field.getIdentifier(), fieldValue);
            }
            value = record;
        } else if (node.getType() instanceof ArrayTypeNode) {
            ArrayTypeNode arrayType = (ArrayTypeNode) node.getType();
            int size = arrayType.getSize();
            Object[] array = new Object[size];

            // Initialize array elements with default values
            for (int i = 0; i < size; i++) {
                array[i] = getDefaultValue(arrayType.getElementType());
            }

            value = array;
        } else if (node.getExpression() != null) {
            value = evaluate(node.getExpression(), env);
        } else {
            value = getDefaultValue(node.getType());
        }

        env.define(name, value);
    }

    // Function declaration execution
    private void executeFunctionDeclaration(FunctionNode node, Environment env) {
        String function = node.getIdentifier();
        env.define(function, node);
    }

    // Array access evaluation
    private Object evaluateArrayAccess(ArrayAccessNode node, Environment env) {
        Object arrayObj = evaluate(node.getArray(), env);
        if (!(arrayObj instanceof Object[])) {
            throw new RuntimeException("Type Error: Attempting to index a non-array type.");
        }
        Object[] array = (Object[]) arrayObj;
        int index = (int) evaluate(node.getIndex(), env);

        // Bounds checking
        if (index < 0 || index >= array.length) {
            throw new RuntimeException("Array Index Out of Bounds: " + index);
        }

        return array[index];
    }

    // Record access evaluation
    private Object evaluateFieldAccess(FieldAccessNode node, Environment env) {
        Object recordObject = evaluate(node.getRecord(), env);
        if (!(recordObject instanceof Map)) {
            throw new RuntimeException("Cannot access field of non-record type");
        }
        Map<String, Object> record = (Map<String, Object>) recordObject;

        if (!record.containsKey(node.getFieldName())) {
            throw new RuntimeException("Field not found: " + node.getFieldName());
        }

        return record.get(node.getFieldName());
    }

    // Function call evaluation
    private Object evaluateFunctionCall(FunctionCallNode node, Environment env) {
        String functionName = node.getFunctionName();
        Object function = env.get(functionName);

        if (!(function instanceof FunctionNode)) {
            throw new RuntimeException("Not a function: " + functionName);
        }

        FunctionNode functionNode = (FunctionNode) function;
        Environment functionEnv = new Environment(env);
        List<DeclarationNode> parameters = functionNode.getParameters();
        List<ExpressionNode> arguments = node.getArguments();

        if (parameters.size() != arguments.size()) {
            throw new RuntimeException("Argument count mismatch when calling function " + functionName);
        }

        for (int i = 0; i < parameters.size(); i++) {
            String paramName = parameters.get(i).getIdentifier();
            Object argValue = evaluate(arguments.get(i), env);
            functionEnv.define(paramName, argValue);
        }

        try {
            execute(functionNode.getBody(), functionEnv);
        } catch (ReturnException e) {
            return e.getValue();
        }

        // If no return statement, return null
        return null;
    }

    // Function to print the execution result
    private void executePrint(PrintNode node, Environment env) {
        Object value = evaluate(node.getExpression(), env);
        System.out.println(value);
    }

    // Helper method to get default value based on type
    private Object getDefaultValue(TypeNode type) {
        if (type instanceof IntegerTypeNode) {
            return 0;
        } else if (type instanceof RealTypeNode) {
            return 0.0;
        } else if (type instanceof BooleanTypeNode) {
            return false;
        } else if (type instanceof ArrayTypeNode) {
            ArrayTypeNode arrayType = (ArrayTypeNode) type;
            int size = arrayType.getSize();
            Object[] array = new Object[size];
            for (int i = 0; i < size; i++) {
                array[i] = getDefaultValue(arrayType.getElementType());
            }
            return array;
        } else if (type instanceof RecordTypeNode) {
            Map<String, Object> record = new HashMap<>();
            RecordTypeNode recordType = (RecordTypeNode) type;
            for (DeclarationNode field : recordType.getFields()) {
                record.put(field.getIdentifier(), getDefaultValue(field.getType()));
            }
            return record;
        }
        return null;
    }
}

// Exception to handle return statements
class ReturnException extends RuntimeException {
    private final Object value;

    public ReturnException(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
