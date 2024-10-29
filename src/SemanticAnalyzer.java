import java.util.*;

public class SemanticAnalyzer {
    private Map<String, TypeNode> symbolTable = new HashMap<>();
    private Map<String, FunctionNode> functionTable = new HashMap<>();
    private Set<String> usedVariables = new HashSet<>();
    private Set<String> declaredVariables = new HashSet<>();
    private Set<String> unusedVariables = new HashSet<>();
    private boolean insideLoop = false;
    private boolean insideFunction = false;

    public void analyze(ProgramNode ast) {
        // Perform semantic analysis checks
        performSemanticChecks(ast);

        // Remove unused variables
        // removeUnusedVariables(ast);

        // Perform optimizations
        performOptimizations(ast);
    }

    /* Semantic Analysis Methods */

    private void performSemanticChecks(ASTNode node) {
        if (node instanceof ProgramNode) {
            performSemanticChecks((ProgramNode) node);
        } else if (node instanceof DeclarationNode) {
            performSemanticChecks((DeclarationNode) node);
        } else if (node instanceof FunctionNode) {
            performSemanticChecks((FunctionNode) node);
        } else if (node instanceof StatementNode) {
            performSemanticChecks((StatementNode) node);
        } else if (node instanceof ExpressionNode) {
            performSemanticChecks((ExpressionNode) node);
        }
        // Handle other node types if necessary
    }

    private void performSemanticChecks(ProgramNode node) {
        for (ASTNode child : node.getChildren()) {
            performSemanticChecks(child);
        }
    }

    private void performSemanticChecks(FunctionNode node) {
        if (functionTable.containsKey(node.getIdentifier())) {
            System.err.println("Semantic Error: Function '" + node.getIdentifier() + "' is already declared.");
        } else {
            functionTable.put(node.getIdentifier(), node);
        }

        boolean previousInsideFunction = insideFunction;
        insideFunction = true;

        // Check function parameters
        if (node.getParameters() != null) {
            for (DeclarationNode param : node.getParameters()) {
                performSemanticChecks(param);
            }
        }

        // Check function body
        if (node.getBody() != null) {
            performSemanticChecks(node.getBody());
        }

        insideFunction = previousInsideFunction;
    }

    private void performSemanticChecks(DeclarationNode node) {
        String id = node.getIdentifier();
        if (symbolTable.containsKey(id)) {
            System.err.println("Semantic Error: Variable '" + id + "' is already declared.");
        } else {
            // Add the variable to the symbol table
            symbolTable.put(id, node.getType());
            declaredVariables.add(id);
            unusedVariables.add(id);
        }

        // Perform semantic checks on the expression assigned
        if (node.getExpression() != null) {
            performSemanticChecks(node.getExpression());

            // Type checking: check that the type of the expression matches the declared type
            TypeNode exprType = getType(node.getExpression());
            if (!typeEquals(node.getType(), exprType)) {
                System.err.println("Type Error: Cannot assign expression of type " + typeName(exprType) +
                        " to variable '" + id + "' of type " + typeName(node.getType()));
            }
        }
    }

    private void performSemanticChecks(StatementNode node) {
        if (node instanceof AssignmentNode) {
            AssignmentNode assignNode = (AssignmentNode) node;
            performSemanticChecks(assignNode.getVariable());
            performSemanticChecks(assignNode.getExpression());

            String id = null;
            if (assignNode.getVariable() instanceof IdentifierNode) {
                id = ((IdentifierNode) assignNode.getVariable()).getName();
            } else if (assignNode.getVariable() instanceof ArrayAccessNode) {
                // Handle array assignments
                // For simplicity, assume the array variable is the base array
                ArrayAccessNode arrayAccess = (ArrayAccessNode) assignNode.getVariable();
                performSemanticChecks(arrayAccess);
                if (arrayAccess.getArray() instanceof IdentifierNode) {
                    id = ((IdentifierNode) arrayAccess.getArray()).getName();
                }
            } else if (assignNode.getVariable() instanceof FieldAccessNode) {
                // Handle field assignments
                FieldAccessNode fieldAccess = (FieldAccessNode) assignNode.getVariable();
                performSemanticChecks(fieldAccess);
                if (fieldAccess.getRecord() instanceof IdentifierNode) {
                    id = ((IdentifierNode) fieldAccess.getRecord()).getName();
                }
            } else {
                System.err.println("Semantic Error: Invalid assignment target.");
            }

            if (id != null) {
                if (!symbolTable.containsKey(id)) {
                    System.err.println("Semantic Error: Variable '" + id + "' is not declared.");
                } else {
                    unusedVariables.remove(id);
                    usedVariables.add(id);

                    // Type checking
                    TypeNode varType = symbolTable.get(id);
                    TypeNode exprType = getType(assignNode.getExpression());
                    if (!typeEquals(varType, exprType)) {
                        System.err.println("Type Error: Cannot assign expression of type " + typeName(exprType) +
                                " to variable '" + id + "' of type " + typeName(varType));
                    }
                }
            }
        } else if (node instanceof ReturnNode) {
            if (!insideFunction) {
                System.err.println("Semantic Error: 'return' statement not inside a function.");
            } else {
                performSemanticChecks(((ReturnNode) node).getExpr());
            }
        } else if (node instanceof IfElseNode) {
            IfElseNode ifNode = (IfElseNode) node;
            performSemanticChecks(ifNode.getCondition());

            // Type checking: condition must be boolean
            TypeNode conditionType = getType(ifNode.getCondition());
            if (!(conditionType instanceof BooleanTypeNode)) {
                System.err.println("Type Error: Condition in 'if' statement must be boolean.");
            }

            performSemanticChecks(ifNode.getThenStmt());
            if (ifNode.getElseStmt() != null) {
                performSemanticChecks(ifNode.getElseStmt());
            }
        } else if (node instanceof WhileLoopNode) {
            boolean previousInsideLoop = insideLoop;
            insideLoop = true;

            WhileLoopNode whileNode = (WhileLoopNode) node;
            performSemanticChecks(whileNode.getCondition());

            // Type checking: condition must be boolean
            TypeNode conditionType = getType(whileNode.getCondition());
            if (!(conditionType instanceof BooleanTypeNode)) {
                System.err.println("Type Error: Condition in 'while' loop must be boolean.");
            }

            performSemanticChecks(whileNode.getBody());

            insideLoop = previousInsideLoop;
        } else if (node instanceof ForLoopNode) {
            boolean previousInsideLoop = insideLoop;
            insideLoop = true;

            ForLoopNode forNode = (ForLoopNode) node;

            // Declare loop variable
            String iteratorName = forNode.getIterator().getName();
            if (symbolTable.containsKey(iteratorName)) {
                System.err.println("Semantic Error: Variable '" + iteratorName + "' is already declared.");
            } else {
                symbolTable.put(iteratorName, new IntegerTypeNode());
                declaredVariables.add(iteratorName);
                unusedVariables.add(iteratorName);
            }

            performSemanticChecks(forNode.getStart());
            performSemanticChecks(forNode.getEnd());

            // Type checking: start and end expressions must be integers
            TypeNode startType = getType(forNode.getStart());
            TypeNode endType = getType(forNode.getEnd());
            if (!(startType instanceof IntegerTypeNode) || !(endType instanceof IntegerTypeNode)) {
                System.err.println("Type Error: Start and end expressions in 'for' loop must be integers.");
            }

            performSemanticChecks(forNode.getBody());

            // Remove loop variable from symbol table after loop
            symbolTable.remove(iteratorName);

            insideLoop = previousInsideLoop;
        } else if (node instanceof StatementBlockNode) {
            StatementBlockNode blockNode = (StatementBlockNode) node;
            for (StatementNode stmt : blockNode.getStatements()) {
                performSemanticChecks(stmt);
            }
        }
        // Handle other statement types if necessary
    }

    private void performSemanticChecks(ExpressionNode node) {
        if (node instanceof BinaryOpNode) {
            BinaryOpNode binOp = (BinaryOpNode) node;
            performSemanticChecks(binOp.getLeft());
            performSemanticChecks(binOp.getRight());

            // Type checking for binary operations
            TypeNode leftType = getType(binOp.getLeft());
            TypeNode rightType = getType(binOp.getRight());
            if (!typeEquals(leftType, rightType)) {
                System.err.println("Type Error: Mismatched types in binary operation: " +
                        typeName(leftType) + " and " + typeName(rightType));
            } else {
                String operator = binOp.getOperator();
                if ((operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/"))
                        && !(leftType instanceof IntegerTypeNode || leftType instanceof RealTypeNode)) {
                    System.err.println("Type Error: Arithmetic operations require numeric types.");
                } else if ((operator.equals("==") || operator.equals(">") || operator.equals("<"))
                        && !(leftType instanceof IntegerTypeNode || leftType instanceof RealTypeNode)) {
                    System.err.println("Type Error: Comparison operations require numeric types.");
                }
            }
        } else if (node instanceof UnaryOpNode) {
            UnaryOpNode unOp = (UnaryOpNode) node;
            performSemanticChecks(unOp.getExpr());

            // Type checking for unary operations
            TypeNode exprType = getType(unOp.getExpr());
            if (unOp.getOperator().equals("NOT") && !(exprType instanceof BooleanTypeNode)) {
                System.err.println("Type Error: 'NOT' operator requires boolean type.");
            } else if (unOp.getOperator().equals("-") && !(exprType instanceof IntegerTypeNode || exprType instanceof RealTypeNode)) {
                System.err.println("Type Error: Unary minus requires numeric type.");
            }
        } else if (node instanceof IdentifierNode) {
            IdentifierNode idNode = (IdentifierNode) node;
            String id = idNode.getName();
            if (!symbolTable.containsKey(id)) {
                System.err.println("Semantic Error: Variable '" + id + "' is not declared.");
            } else {
                unusedVariables.remove(id);
                usedVariables.add(id);
            }
        } else if (node instanceof ArrayAccessNode) {
            ArrayAccessNode arrayNode = (ArrayAccessNode) node;
            performSemanticChecks(arrayNode.getArray());
            performSemanticChecks(arrayNode.getIndex());

            // Type checking: array must be an array type
            TypeNode arrayType = getType(arrayNode.getArray());
            if (!(arrayType instanceof ArrayTypeNode)) {
                System.err.println("Type Error: Attempting to index a non-array type.");
            } else {
                // Check index type
                TypeNode indexType = getType(arrayNode.getIndex());
                if (!(indexType instanceof IntegerTypeNode)) {
                    System.err.println("Type Error: Array index must be of integer type.");
                } else {
                    // Array bounds checking if index is constant
                    if (arrayNode.getIndex() instanceof NumberNode) {
                        int indexValue = ((NumberNode) arrayNode.getIndex()).getValue();
                        int arraySize = ((ArrayTypeNode) arrayType).getSize();
                        if (indexValue < 0 || indexValue >= arraySize) {
                            System.err.println("Semantic Error: Array index out of bounds.");
                        }
                    }
                }
            }
        } else if (node instanceof FieldAccessNode) {
            FieldAccessNode fieldNode = (FieldAccessNode) node;
            performSemanticChecks(fieldNode.getRecord());

            TypeNode recordType = getType(fieldNode.getRecord());
            if (!(recordType instanceof RecordTypeNode)) {
                System.err.println("Type Error: Attempting to access field of non-record type.");
            } else {
                RecordTypeNode recType = (RecordTypeNode) recordType;
                boolean fieldExists = false;
                for (DeclarationNode field : recType.getFields()) {
                    if (field.getIdentifier().equals(fieldNode.getFieldName())) {
                        fieldExists = true;
                        break;
                    }
                }
                if (!fieldExists) {
                    System.err.println("Semantic Error: Field '" + fieldNode.getFieldName() + "' does not exist in record.");
                }
            }
        }
        // Handle other expression types if necessary
    }

    private TypeNode getType(ExpressionNode expr) {
        if (expr instanceof NumberNode) {
            return new IntegerTypeNode();
        } else if (expr instanceof RealNode) {
            return new RealTypeNode();
        } else if (expr instanceof BooleanNode) {
            return new BooleanTypeNode();
        } else if (expr instanceof IdentifierNode) {
            String id = ((IdentifierNode) expr).getName();
            if (symbolTable.containsKey(id)) {
                return symbolTable.get(id);
            } else {
                System.err.println("Semantic Error: Variable '" + id + "' is not declared.");
                return null;
            }
        } else if (expr instanceof BinaryOpNode) {
            BinaryOpNode binOp = (BinaryOpNode) expr;
            TypeNode leftType = getType(binOp.getLeft());
            TypeNode rightType = getType(binOp.getRight());
            if (!typeEquals(leftType, rightType)) {
                return null;
            }
            String operator = binOp.getOperator();
            if (operator.equals("==") || operator.equals(">") || operator.equals("<")
                    || operator.equals("AND") || operator.equals("OR") || operator.equals("XOR")) {
                return new BooleanTypeNode();
            }
            return leftType;
        } else if (expr instanceof UnaryOpNode) {
            UnaryOpNode unOp = (UnaryOpNode) expr;
            return getType(unOp.getExpr());
        } else if (expr instanceof ArrayAccessNode) {
            TypeNode arrayType = getType(((ArrayAccessNode) expr).getArray());
            if (arrayType instanceof ArrayTypeNode) {
                return ((ArrayTypeNode) arrayType).getElementType();
            } else {
                return null;
            }
        } else if (expr instanceof FieldAccessNode) {
            TypeNode recordType = getType(((FieldAccessNode) expr).getRecord());
            if (recordType instanceof RecordTypeNode) {
                RecordTypeNode recType = (RecordTypeNode) recordType;
                for (DeclarationNode field : recType.getFields()) {
                    if (field.getIdentifier().equals(((FieldAccessNode) expr).getFieldName())) {
                        return field.getType();
                    }
                }
            }
            return null;
        }
        return null;
    }

    private boolean typeEquals(TypeNode t1, TypeNode t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        return t1.getClass().equals(t2.getClass());
    }

    private String typeName(TypeNode type) {
        if (type == null) {
            return "Unknown";
        }
        if (type instanceof IntegerTypeNode) {
            return "Integer";
        } else if (type instanceof RealTypeNode) {
            return "Real";
        } else if (type instanceof BooleanTypeNode) {
            return "Boolean";
        } else if (type instanceof ArrayTypeNode) {
            return "Array";
        } else if (type instanceof RecordTypeNode) {
            return "Record";
        }
        return "Unknown";
    }

    /* Removing Unused Variables */

    private void removeUnusedVariables(ASTNode node) {
        if (node instanceof ProgramNode) {
            removeUnusedVariables((ProgramNode) node);
        } else if (node instanceof DeclarationNode) {
            String id = ((DeclarationNode) node).getIdentifier();
            if (unusedVariables.contains(id)) {
                // Remove this declaration from its parent
                ASTNode parent = node.getParent();
                if (parent instanceof ProgramNode) {
                    ((ProgramNode) parent).getChildren().remove(node);
                } else if (parent instanceof RecordTypeNode) {
                    ((RecordTypeNode) parent).getFields().remove(node);
                }
                symbolTable.remove(id);
                unusedVariables.remove(id);
                declaredVariables.remove(id);
                System.out.println("Optimization: Removed unused variable '" + id + "'.");
            }
        } else {
            // Recursively remove unused variables in child nodes
            for (ASTNode child : getChildren(node)) {
                removeUnusedVariables(child);
            }
        }
    }

    private List<ASTNode> getChildren(ASTNode node) {
        List<ASTNode> children = new ArrayList<>();
        if (node instanceof ProgramNode) {
            children.addAll(((ProgramNode) node).getChildren());
        } else if (node instanceof FunctionNode) {
            FunctionNode funcNode = (FunctionNode) node;
            if (funcNode.getParameters() != null) {
                children.addAll(funcNode.getParameters());
            }
            if (funcNode.getBody() != null) {
                children.add(funcNode.getBody());
            }
        } else if (node instanceof DeclarationNode) {
            if (((DeclarationNode) node).getExpression() != null)
                children.add(((DeclarationNode) node).getExpression());
            if (((DeclarationNode) node).getType() != null)
                children.add(((DeclarationNode) node).getType());
        } else if (node instanceof StatementNode) {
            if (node instanceof IfElseNode) {
                IfElseNode ifNode = (IfElseNode) node;
                children.add(ifNode.getCondition());
                if (ifNode.getThenStmt() != null)
                    children.add(ifNode.getThenStmt());
                if (ifNode.getElseStmt() != null)
                    children.add(ifNode.getElseStmt());
            } else if (node instanceof WhileLoopNode) {
                WhileLoopNode whileNode = (WhileLoopNode) node;
                children.add(whileNode.getCondition());
                if (whileNode.getBody() != null)
                    children.add(whileNode.getBody());
            } else if (node instanceof ForLoopNode) {
                ForLoopNode forNode = (ForLoopNode) node;
                children.add(forNode.getStart());
                children.add(forNode.getEnd());
                if (forNode.getBody() != null)
                    children.add(forNode.getBody());
            } else if (node instanceof AssignmentNode) {
                AssignmentNode assignNode = (AssignmentNode) node;
                children.add(assignNode.getVariable());
                children.add(assignNode.getExpression());
            } else if (node instanceof ReturnNode) {
                ReturnNode returnNode = (ReturnNode) node;
                if (returnNode.getExpr() != null)
                    children.add(returnNode.getExpr());
            } else if (node instanceof StatementBlockNode) {
                StatementBlockNode blockNode = (StatementBlockNode) node;
                children.addAll(blockNode.getStatements());
            }
        } else if (node instanceof ExpressionNode) {
            if (node instanceof BinaryOpNode) {
                BinaryOpNode binOp = (BinaryOpNode) node;
                children.add(binOp.getLeft());
                children.add(binOp.getRight());
            } else if (node instanceof UnaryOpNode) {
                UnaryOpNode unOp = (UnaryOpNode) node;
                children.add(unOp.getExpr());
            } else if (node instanceof ArrayAccessNode) {
                ArrayAccessNode arrayNode = (ArrayAccessNode) node;
                children.add(arrayNode.getArray());
                children.add(arrayNode.getIndex());
            } else if (node instanceof FieldAccessNode) {
                FieldAccessNode fieldNode = (FieldAccessNode) node;
                children.add(fieldNode.getRecord());
            }
        } else if (node instanceof TypeNode) {
            if (node instanceof ArrayTypeNode) {
                ArrayTypeNode arrayType = (ArrayTypeNode) node;
                children.add(arrayType.getElementType());
            } else if (node instanceof RecordTypeNode) {
                RecordTypeNode recordType = (RecordTypeNode) node;
                children.addAll(recordType.getFields());
            }
        }
        return children;
    }

    /* Optimization Methods */

  /* Optimization Methods */

    private void performOptimizations(ASTNode node) {
        if (node instanceof ProgramNode) {
            performOptimizations((ProgramNode) node);
        } else if (node instanceof DeclarationNode) {
            performOptimizations((DeclarationNode) node);
        } else if (node instanceof FunctionNode) {
            performOptimizations((FunctionNode) node);
        } else if (node instanceof StatementNode) {
            performOptimizations((StatementNode) node);
        } else if (node instanceof ExpressionNode) {
            // Optimizations for expressions are handled in optimizeExpression
        }
    }

    private void performOptimizations(ProgramNode node) {
        List<ASTNode> optimizedChildren = new ArrayList<>();
        for (ASTNode child : node.getChildren()) {
            performOptimizations(child);
            if (!(child instanceof EmptyNode)) {
                optimizedChildren.add(child);
            }
        }
        node.setChildren(optimizedChildren);
    }

    private void performOptimizations(FunctionNode node) {
        if (node.getBody() != null) {
            performOptimizations(node.getBody());
        }
    }


    private void performOptimizations(DeclarationNode node) {
        if (node.getExpression() != null) {
            node.setExpression(optimizeExpression(node.getExpression()));
        }
    }

    private void performOptimizations(StatementNode node) {
        if (node instanceof IfElseNode) {
            IfElseNode ifNode = (IfElseNode) node;
            ifNode.setCondition(optimizeExpression(ifNode.getCondition()));
            performOptimizations(ifNode.getThenStmt());
            if (ifNode.getElseStmt() != null) {
                performOptimizations(ifNode.getElseStmt());
            }
            // Simplify if-else structures where possible
            if (ifNode.getCondition() instanceof BooleanNode) {
                boolean conditionValue = ((BooleanNode) ifNode.getCondition()).isValue();
                if (conditionValue) {
                    // Replace the if-else node with the then statement
                    replaceNodeInParent(ifNode, ifNode.getThenStmt());
                    System.out.println("Optimization: Simplified if-else statement with constant true condition.");
                } else if (ifNode.getElseStmt() != null) {
                    // Replace the if-else node with the else statement
                    replaceNodeInParent(ifNode, ifNode.getElseStmt());
                    System.out.println("Optimization: Simplified if-else statement with constant false condition.");
                } else {
                    // Remove the if-else node
                    replaceNodeInParent(ifNode, new EmptyNode());
                    System.out.println("Optimization: Removed if-else statement with constant false condition and no else branch.");
                }
            }
        } else if (node instanceof WhileLoopNode) {
            WhileLoopNode whileNode = (WhileLoopNode) node;
            whileNode.setCondition(optimizeExpression(whileNode.getCondition()));
            performOptimizations(whileNode.getBody());

            // Remove unreachable code if condition is constant false
            if (whileNode.getCondition() instanceof BooleanNode) {
                boolean conditionValue = ((BooleanNode) whileNode.getCondition()).isValue();
                if (!conditionValue) {
                    // Remove the while loop
                    replaceNodeInParent(whileNode, new EmptyNode());
                    System.out.println("Optimization: Removed while loop with constant false condition.");
                }
            }
        } else if (node instanceof ForLoopNode) {
            ForLoopNode forNode = (ForLoopNode) node;
            forNode.setStart(optimizeExpression(forNode.getStart()));
            forNode.setEnd(optimizeExpression(forNode.getEnd()));
            performOptimizations(forNode.getBody());
        } else if (node instanceof AssignmentNode) {
            AssignmentNode assignNode = (AssignmentNode) node;
            assignNode.setExpression(optimizeExpression(assignNode.getExpression()));
        } else if (node instanceof ReturnNode) {
            ReturnNode returnNode = (ReturnNode) node;
            returnNode.setExpr(optimizeExpression(returnNode.getExpr()));
            // Remove unreachable code after return
            removeUnreachableCode(returnNode);
        }
    }

    private ExpressionNode optimizeExpression(ExpressionNode node) {
        if (node instanceof BinaryOpNode) {
            BinaryOpNode binOp = (BinaryOpNode) node;
            binOp.setLeft(optimizeExpression(binOp.getLeft()));
            binOp.setRight(optimizeExpression(binOp.getRight()));

            // Constant folding
            if (isConstant(binOp.getLeft()) && isConstant(binOp.getRight())) {
                ExpressionNode result = foldConstants(binOp);
                if (result != null) {
                    System.out.println("Optimization: Simplified binary operation '" + binOp.getOperator() + "' with constants.");
                    return result;
                }
            }
        } else if (node instanceof UnaryOpNode) {
            UnaryOpNode unOp = (UnaryOpNode) node;
            unOp.setExpr(optimizeExpression(unOp.getExpr()));

            if (isConstant(unOp.getExpr())) {
                ExpressionNode result = foldConstants(unOp);
                if (result != null) {
                    System.out.println("Optimization: Simplified unary operation '" + unOp.getOperator() + "' with constant.");
                    return result;
                }
            }
        }
        return node;
    }

    private boolean isConstant(ExpressionNode node) {
        return node instanceof NumberNode || node instanceof BooleanNode || node instanceof RealNode;
    }

    private ExpressionNode foldConstants(ExpressionNode node) {
        if (node instanceof BinaryOpNode) {
            BinaryOpNode binOp = (BinaryOpNode) node;
            ExpressionNode left = binOp.getLeft();
            ExpressionNode right = binOp.getRight();
            String operator = binOp.getOperator();

            if (left instanceof NumberNode && right instanceof NumberNode) {
                int leftVal = ((NumberNode) left).getValue();
                int rightVal = ((NumberNode) right).getValue();
                int result = 0;
                switch (operator) {
                    case "+":
                        result = leftVal + rightVal;
                        break;
                    case "-":
                        result = leftVal - rightVal;
                        break;
                    case "*":
                        result = leftVal * rightVal;
                        break;
                    case "/":
                        result = leftVal / rightVal;
                        break;
                    case "==":
                        return new BooleanNode(leftVal == rightVal);
                    case ">":
                        return new BooleanNode(leftVal > rightVal);
                    case "<":
                        return new BooleanNode(leftVal < rightVal);
                }
                return new NumberNode(result);
            } else if (left instanceof BooleanNode && right instanceof BooleanNode) {
                boolean leftVal = ((BooleanNode) left).isValue();
                boolean rightVal = ((BooleanNode) right).isValue();
                boolean result = false;
                switch (operator) {
                    case "AND":
                        result = leftVal && rightVal;
                        break;
                    case "OR":
                        result = leftVal || rightVal;
                        break;
                    case "XOR":
                        result = leftVal ^ rightVal;
                        break;
                }
                return new BooleanNode(result);
            }
        } else if (node instanceof UnaryOpNode) {
            UnaryOpNode unOp = (UnaryOpNode) node;
            ExpressionNode expr = unOp.getExpr();
            String operator = unOp.getOperator();

            if (expr instanceof NumberNode && operator.equals("-")) {
                int val = ((NumberNode) expr).getValue();
                return new NumberNode(-val);
            } else if (expr instanceof BooleanNode && operator.equals("NOT")) {
                boolean val = ((BooleanNode) expr).isValue();
                return new BooleanNode(!val);
            }
        }
        return null;
    }

    private void removeUnreachableCode(ReturnNode returnNode) {
        ASTNode parent = returnNode.getParent();
        if (parent instanceof StatementNode) {
            List<StatementNode> statements = new ArrayList<>();
            statements.add((StatementNode) parent);
            ASTNode grandParent = parent.getParent();
            if (grandParent instanceof StatementBlockNode) {
                int index = ((StatementBlockNode) grandParent).getStatements().indexOf(parent);
                List<StatementNode> toRemove = ((StatementBlockNode) grandParent).getStatements().subList(index + 1,
                        ((StatementBlockNode) grandParent).getStatements().size());
                if (!toRemove.isEmpty()) {
                    toRemove.clear();
                    System.out.println("Optimization: Removed unreachable code after 'return' statement.");
                }
            }
        }
    }

    private void replaceNodeInParent(ASTNode oldNode, ASTNode newNode) {
        ASTNode parent = oldNode.getParent();
        if (parent == null) return;

        if (parent instanceof ProgramNode) {
            List<ASTNode> children = ((ProgramNode) parent).getChildren();
            int index = children.indexOf(oldNode);
            if (newNode instanceof EmptyNode) {
                children.remove(index);
            } else {
                children.set(index, newNode);
                newNode.setParent(parent);
            }
        } else if (parent instanceof StatementNode) {
            // Handle replacement in statements
            if (parent instanceof IfElseNode) {
                IfElseNode ifNode = (IfElseNode) parent;
                if (ifNode.getThenStmt() == oldNode) {
                    ifNode.setThenStmt((StatementNode) newNode);
                } else if (ifNode.getElseStmt() == oldNode) {
                    ifNode.setElseStmt((StatementNode) newNode);
                }
            } else if (parent instanceof WhileLoopNode) {
                WhileLoopNode whileNode = (WhileLoopNode) parent;
                if (whileNode.getBody() == oldNode) {
                    whileNode.setBody((StatementNode) newNode);
                }
            } else if (parent instanceof ForLoopNode) {
                ForLoopNode forNode = (ForLoopNode) parent;
                if (forNode.getBody() == oldNode) {
                    forNode.setBody((StatementNode) newNode);
                }
            }
        } else if (parent instanceof DeclarationNode) {
            ((DeclarationNode) parent).setExpression((ExpressionNode) newNode);
        }
        // Handle other parent types if necessary
    }


    /* AST Printing Method */

    public void printAST(ASTNode node, int indent) {
        String indentation = " ".repeat(indent);
        if (node instanceof ProgramNode) {
            System.out.println(indentation + "Program");
            for (ASTNode child : ((ProgramNode) node).getChildren()) {
                printAST(child, indent + 2);
            }
        } else if (node instanceof DeclarationNode) {
            DeclarationNode decl = (DeclarationNode) node;
            System.out.println(indentation + "Declaration: " + decl.getIdentifier());
            System.out.println(indentation + "  Type:");
            printAST(decl.getType(), indent + 4);
            if (decl.getExpression() != null) {
                System.out.println(indentation + "  Expression:");
                printAST(decl.getExpression(), indent + 4);
            }
        } else if (node instanceof IntegerTypeNode) {
            System.out.println(indentation + "IntegerType");
        } else if (node instanceof NumberNode) {
            NumberNode num = (NumberNode) node;
            System.out.println(indentation + "Number: " + num.getValue());
        } else if (node instanceof IdentifierNode) {
            IdentifierNode id = (IdentifierNode) node;
            System.out.println(indentation + "Identifier: " + id.getName());
        } else if (node instanceof BinaryOpNode) {
            BinaryOpNode binOp = (BinaryOpNode) node;
            System.out.println(indentation + "BinaryOp: " + binOp.getOperator());
            System.out.println(indentation + "  Left:");
            printAST(binOp.getLeft(), indent + 4);
            System.out.println(indentation + "  Right:");
            printAST(binOp.getRight(), indent + 4);
        } else if (node instanceof BooleanNode) {
            BooleanNode boolNode = (BooleanNode) node;
            System.out.println(indentation + "Boolean: " + boolNode.isValue());
        } else if (node instanceof UnaryOpNode) {
            UnaryOpNode unOp = (UnaryOpNode) node;
            System.out.println(indentation + "UnaryOp: " + unOp.getOperator());
            printAST(unOp.getExpr(), indent + 2);
        } else if (node instanceof RealNode) {
            RealNode realNode = (RealNode) node;
            System.out.println(indentation + "Real: " + realNode.getValue());
        } else if (node instanceof BooleanTypeNode) {
            System.out.println(indentation + "BooleanType");
        } else if (node instanceof RealTypeNode) {
            System.out.println(indentation + "RealType");
        } else if (node instanceof FunctionNode) {
            FunctionNode func = (FunctionNode) node;
            System.out.println(indentation + "Function: " + func.getIdentifier());

            if (func.getParameters() != null && !func.getParameters().isEmpty()) {
                System.out.println(indentation + "  Parameters:");
                for (DeclarationNode param : func.getParameters()) {
                    printAST(param, indent + 4);
                }
            }

            if (func.getReturnType() != null) {
                System.out.println(indentation + "  Return Type:");
                printAST(func.getReturnType(), indent + 4);
            }

            if (func.getBody() != null) {
                System.out.println(indentation + "  Body:");
                printAST(func.getBody(), indent + 4);
            }
        } else if (node instanceof AssignmentNode) {
            AssignmentNode assign = (AssignmentNode) node;
            System.out.println(indentation + "Assignment");
            System.out.println(indentation + "  Variable:");
            printAST(assign.getVariable(), indent + 4);
            System.out.println(indentation + "  Expression:");
            printAST(assign.getExpression(), indent + 4);
        } else if (node instanceof ReturnNode) {
            ReturnNode retNode = (ReturnNode) node;
            System.out.println(indentation + "Return");
            printAST(retNode.getExpr(), indent + 2);
        } else if (node instanceof IfElseNode) {
            IfElseNode ifNode = (IfElseNode) node;
            System.out.println(indentation + "IfElse");
            System.out.println(indentation + "  Condition:");
            printAST(ifNode.getCondition(), indent + 4);
            System.out.println(indentation + "  Then:");
            printAST(ifNode.getThenStmt(), indent + 4);
            if (ifNode.getElseStmt() != null) {
                System.out.println(indentation + "  Else:");
                printAST(ifNode.getElseStmt(), indent + 4);
            }
        } else if (node instanceof WhileLoopNode) {
            WhileLoopNode whileNode = (WhileLoopNode) node;
            System.out.println(indentation + "WhileLoop");
            System.out.println(indentation + "  Condition:");
            printAST(whileNode.getCondition(), indent + 4);
            System.out.println(indentation + "  Body:");
            printAST(whileNode.getBody(), indent + 4);
        } else if (node instanceof ForLoopNode) {
            ForLoopNode forNode = (ForLoopNode) node;
            System.out.println(indentation + "ForLoop");
            System.out.println(indentation + "  Iterator:");
            printAST(forNode.getIterator(), indent + 4);
            System.out.println(indentation + "  Start:");
            printAST(forNode.getStart(), indent + 4);
            System.out.println(indentation + "  End:");
            printAST(forNode.getEnd(), indent + 4);
            System.out.println(indentation + "  Body:");
            printAST(forNode.getBody(), indent + 4);
        } else if (node instanceof ArrayTypeNode) {
            ArrayTypeNode arrayType = (ArrayTypeNode) node;
            System.out.println(indentation + "ArrayType");
            System.out.println(indentation + "  Size: " + arrayType.getSize());
            System.out.println(indentation + "  ElementType:");
            printAST(arrayType.getElementType(), indent + 4);
        } else if (node instanceof ArrayAccessNode) {
            ArrayAccessNode arrayAccess = (ArrayAccessNode) node;
            System.out.println(indentation + "ArrayAccess");
            System.out.println(indentation + "  Array:");
            printAST(arrayAccess.getArray(), indent + 4);
            System.out.println(indentation + "  Index:");
            printAST(arrayAccess.getIndex(), indent + 4);
        } else if (node instanceof FieldAccessNode) {
            FieldAccessNode fieldAccess = (FieldAccessNode) node;
            System.out.println(indentation + "FieldAccess");
            System.out.println(indentation + "  Record:");
            printAST(fieldAccess.getRecord(), indent + 4);
            System.out.println(indentation + "  Field: " + fieldAccess.getFieldName());
        } else if (node instanceof RecordTypeNode) {
            RecordTypeNode recordType = (RecordTypeNode) node;
            System.out.println(indentation + "RecordType");
            System.out.println(indentation + "  Fields:");
            for (DeclarationNode field : recordType.getFields()) {
                printAST(field, indent + 4);
            }
        } else if (node instanceof EmptyNode) {
            System.out.println(indentation + "Empty");
        } else if (node instanceof StatementBlockNode) {
            StatementBlockNode blockNode = (StatementBlockNode) node;
            System.out.println(indentation + "StatementBlock");
            for (StatementNode stmt : blockNode.getStatements()) {
                printAST(stmt, indent + 2);
            }
        } else {
            System.out.println(indentation + "Unknown Node Type: " + node.getClass().getSimpleName());
        }
    }
}



 