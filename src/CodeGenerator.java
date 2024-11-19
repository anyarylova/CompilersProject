// CodeGenerator.java

import org.objectweb.asm.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;



public class CodeGenerator implements Opcodes {
    private ClassWriter cw;
    private MethodVisitor mv;
    private String className = "MainClass";
    private Map<String, Integer> variableIndex;
    private Map<String, TypeNode> variableTypes;
    private int currentLocalVarIndex = 0;
    private Map<String, FunctionNode> functionTable;
    private Map<RecordTypeNode, String> recordTypeClasses;
    private int recordClassCounter = 0;
    private int labelCounter = 0;

    public CodeGenerator() {
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        variableIndex = new HashMap<>();
        variableTypes = new HashMap<>();
        functionTable = new HashMap<>();
        recordTypeClasses = new HashMap<>();
    }

    public void generateCode(ProgramNode ast) throws IOException {
        // Define the class
        cw.visit(V1_8, ACC_PUBLIC, className, null, "java/lang/Object", null);

        // Generate built-in print methods
        generatePrintIntMethod();
        generatePrintRealMethod();

        // Generate code for functions first
        for (ASTNode node : ast.getChildren()) {
            if (node instanceof FunctionNode) {
                FunctionNode funcNode = (FunctionNode) node;
                functionTable.put(funcNode.getIdentifier(), funcNode);
                generateFunction(funcNode);
            }
        }

        // Generate main method
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();

        // Generate code for global declarations and statements
        for (ASTNode node : ast.getChildren()) {
            if (node instanceof DeclarationNode) {
                generateDeclaration((DeclarationNode) node);
            } else if (node instanceof StatementNode) {
                generateStatement((StatementNode) node);
            }
        }

        // Add return statement
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // Generate print methods for records
        for (Map.Entry<RecordTypeNode, String> entry : recordTypeClasses.entrySet()) {
            RecordTypeNode recordType = entry.getKey();
            String recordClassName = entry.getValue();
            generatePrintMethodForRecord(recordClassName, recordType);
        }

        // End of class
        cw.visitEnd();

        // Write the class to a file
        FileOutputStream fos = new FileOutputStream(className + ".class");
        fos.write(cw.toByteArray());
        fos.close();

        System.out.println("Bytecode generation completed. Class file written to " + className + ".class");
    }


    private void generateFunction(FunctionNode node) {
        // Reset local variable index for the function
        currentLocalVarIndex = 0;
        variableIndex.clear();
        variableTypes.clear();

        // Get the method descriptor
        String methodDescriptor = getMethodDescriptor(node);

        // Create the method
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, node.getIdentifier(), methodDescriptor, null, null);
        mv.visitCode();

        // Store parameters
        if (node.getParameters() != null) {
            for (DeclarationNode param : node.getParameters()) {
                String paramName = param.getIdentifier();
                TypeNode paramType = param.getType();
                variableIndex.put(paramName, currentLocalVarIndex);
                variableTypes.put(paramName, paramType);
                currentLocalVarIndex += getLocalVariableSize(paramType);
            }
        }

        // Generate code for the function body
        generateStatement(node.getBody());

        // Add default return if necessary
        if (node.getReturnType() instanceof IntegerTypeNode || node.getReturnType() instanceof BooleanTypeNode) {
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
        } else if (node.getReturnType() instanceof RealTypeNode) {
            mv.visitInsn(DCONST_0);
            mv.visitInsn(DRETURN);
        } else {
            mv.visitInsn(RETURN);
        }

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private String getMethodDescriptor(FunctionNode node) {
        StringBuilder descriptor = new StringBuilder("(");
        if (node.getParameters() != null) {
            for (DeclarationNode param : node.getParameters()) {
                descriptor.append(getTypeDescriptor(param.getType()));
            }
        }
        descriptor.append(")");
        descriptor.append(getTypeDescriptor(node.getReturnType()));
        return descriptor.toString();
    }

    private String getTypeDescriptor(TypeNode type) {
        if (type instanceof IntegerTypeNode || type instanceof BooleanTypeNode) {
            return "I";
        } else if (type instanceof RealTypeNode) {
            return "D";
        } else if (type instanceof ArrayTypeNode) {
            ArrayTypeNode arrayType = (ArrayTypeNode) type;
            String elementTypeDescriptor = getTypeDescriptor(arrayType.getElementType());
            return "[" + elementTypeDescriptor;
        } else if (type instanceof RecordTypeNode) {
            String recordClassName = getRecordClassNameFromType((RecordTypeNode) type);
            return "L" + recordClassName + ";";
        }
        return "V"; // Void
    }


    private void generateDeclaration(DeclarationNode node) {
        String varName = node.getIdentifier();
        TypeNode type = node.getType();
        variableIndex.put(varName, currentLocalVarIndex);
        variableTypes.put(varName, type);
        currentLocalVarIndex += getLocalVariableSize(type);

        if (type instanceof ArrayTypeNode) {
            ArrayTypeNode arrayType = (ArrayTypeNode) type;
            int size = arrayType.getSize();
            mv.visitIntInsn(BIPUSH, size); // Push array size onto the stack
            mv.visitIntInsn(NEWARRAY, getArrayTypeCode(arrayType.getElementType()));
            storeVariable(varName, type);
        } else if (type instanceof RecordTypeNode) {
            String recordClassName = getRecordClassNameFromType((RecordTypeNode) type);
            mv.visitTypeInsn(NEW, recordClassName);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, recordClassName, "<init>", "()V", false);
            storeVariable(varName, type);
        } else {
            if (node.getExpression() != null) {
                generateExpression(node.getExpression());
                storeVariable(varName, type);
            } else {
                // Initialize with default value
                if (type instanceof IntegerTypeNode || type instanceof BooleanTypeNode) {
                    mv.visitInsn(ICONST_0);
                } else if (type instanceof RealTypeNode) {
                    mv.visitInsn(DCONST_0);
                }
                storeVariable(varName, type);
            }
        }
    }

    private void generateStatement(StatementNode node) {
        if (node instanceof AssignmentNode) {
            AssignmentNode assignNode = (AssignmentNode) node;
            TypeNode exprType = getType(assignNode.getExpression());
            String varName = ((IdentifierNode) assignNode.getVariable()).getName();
            TypeNode varType = variableTypes.get(varName);

            if (assignNode.getVariable() instanceof IdentifierNode) {
                // String varName = ((IdentifierNode) assignNode.getVariable()).getName();
                // TypeNode varType = variableTypes.get(varName);
                // if (!typeEquals(varType, exprType)) {
                //     throw new RuntimeException("Type mismatch: Cannot assign " + typeName(exprType) + " to " + typeName(varType));
                // }
                // generateExpression(assignNode.getExpression());
                // storeVariable(varName, varType);

                // TypeNode exprType = getType(assignNode.getExpression());

                if (varType instanceof BooleanTypeNode && exprType instanceof IntegerTypeNode) {
                    // Generate code to evaluate the integer expression
                    generateExpression(assignNode.getExpression());
                    Label falseLabel = new Label();
                    Label endLabel = new Label();
                    // Convert integer to boolean
                    mv.visitJumpInsn(IFEQ, falseLabel);
                    mv.visitInsn(ICONST_1); // True
                    mv.visitJumpInsn(GOTO, endLabel);
                    mv.visitLabel(falseLabel);
                    mv.visitInsn(ICONST_0); // False
                    mv.visitLabel(endLabel);
                    // Store the boolean value
                    storeVariable(varName, varType);
                } else if (varType instanceof IntegerTypeNode && exprType instanceof RealTypeNode) {
                    // Generate code to evaluate the expression
                    generateExpression(assignNode.getExpression());
                    // Insert conversion from double to int
                    mv.visitInsn(D2I);
                    // Store the integer value
                    storeVariable(varName, varType);
                } else if (!typeEquals(varType, exprType)) {
                    throw new RuntimeException("Type mismatch: Cannot assign " + typeName(exprType) + " to " + typeName(varType));
                } else {
                    generateExpression(assignNode.getExpression());
                    storeVariable(varName, varType);
                }
            } else if (assignNode.getVariable() instanceof ArrayAccessNode) {
                ArrayAccessNode arrayAccess = (ArrayAccessNode) assignNode.getVariable();
                TypeNode elementType = getType(arrayAccess);

                if (!typeEquals(elementType, exprType)) {
                    throw new RuntimeException("Type mismatch: Cannot assign " + typeName(exprType) + " to array element of type " + typeName(elementType));
                }

                // Load array reference
                generateExpression(arrayAccess.getArray());
                // Push index onto the stack
                generateExpression(arrayAccess.getIndex());
                // Generate the value to be stored
                generateExpression(assignNode.getExpression());
                if (elementType instanceof IntegerTypeNode || elementType instanceof BooleanTypeNode) {
                    mv.visitInsn(IASTORE); // Store integer or boolean into array
                } else if (elementType instanceof RealTypeNode) {
                    mv.visitInsn(DASTORE); // Store double into array
                }
            } else if (assignNode.getVariable() instanceof FieldAccessNode) {
                FieldAccessNode fieldAccess = (FieldAccessNode) assignNode.getVariable();
                TypeNode fieldType = getFieldType(fieldAccess);

                if (!typeEquals(fieldType, exprType)) {
                    throw new RuntimeException("Type mismatch: Cannot assign " + typeName(exprType) + " to field of type " + typeName(fieldType));
                }

                // Generate code to load the record instance
                generateExpression(fieldAccess.getRecord());
                // Generate code for the value to be stored
                generateExpression(assignNode.getExpression());
                // Store into the field
                String fieldName = fieldAccess.getFieldName();
                String recordClassName = getRecordClassName(fieldAccess.getRecord());
                mv.visitFieldInsn(PUTFIELD, recordClassName, fieldName, getTypeDescriptor(fieldType));
            } else {
                throw new RuntimeException("Unsupported assignment target.");
            }
        } else if (node instanceof ReturnNode) {
            generateExpression(((ReturnNode) node).getExpr());
            TypeNode returnType = getType(((ReturnNode) node).getExpr());
            if (returnType instanceof IntegerTypeNode || returnType instanceof BooleanTypeNode) {
                mv.visitInsn(IRETURN);
            } else if (returnType instanceof RealTypeNode) {
                mv.visitInsn(DRETURN);
            } else {
                mv.visitInsn(RETURN);
            }
        } else if (node instanceof StatementBlockNode) {
            for (StatementNode stmt : ((StatementBlockNode) node).getStatements()) {
                generateStatement(stmt);
            }
        } else if (node instanceof IfElseNode) {
            generateIfElse((IfElseNode) node);
        } else if (node instanceof WhileLoopNode) {
            generateWhileLoop((WhileLoopNode) node);
        } else if (node instanceof ForLoopNode) {
            generateForLoop((ForLoopNode) node);
        } else if (node instanceof PrintNode) {
            generatePrint((PrintNode) node);
        }
        // Handle other statement types
    }

    private void generateIfElse(IfElseNode node) {
        Label elseLabel = new Label();
        Label endLabel = new Label();
        generateExpression(node.getCondition());
        mv.visitJumpInsn(IFEQ, elseLabel);
        // Then block
        generateStatement(node.getThenStmt());
        mv.visitJumpInsn(GOTO, endLabel);
        // Else block
        mv.visitLabel(elseLabel);
        if (node.getElseStmt() != null) {
            generateStatement(node.getElseStmt());
        }
        mv.visitLabel(endLabel);
    }

    private void generateWhileLoop(WhileLoopNode node) {
        Label startLabel = new Label();
        Label endLabel = new Label();
        mv.visitLabel(startLabel);
        generateExpression(node.getCondition());
        mv.visitJumpInsn(IFEQ, endLabel);
        generateStatement(node.getBody());
        mv.visitJumpInsn(GOTO, startLabel);
        mv.visitLabel(endLabel);
    }

    private void generateForLoop(ForLoopNode node) {
        String iteratorName = node.getIterator().getName();
        TypeNode iteratorType = new IntegerTypeNode();
        variableIndex.put(iteratorName, currentLocalVarIndex);
        variableTypes.put(iteratorName, iteratorType);
        currentLocalVarIndex += getLocalVariableSize(iteratorType);

        // Initialize iterator
        generateExpression(node.getStart());
        storeVariable(iteratorName, iteratorType);

        Label startLabel = new Label();
        Label endLabel = new Label();
        mv.visitLabel(startLabel);

        // Load iterator
        loadVariable(iteratorName, iteratorType);
        // Load end value
        generateExpression(node.getEnd());
        // Compare iterator <= end
        mv.visitJumpInsn(IF_ICMPGT, endLabel);

        // Loop body
        generateStatement(node.getBody());

        // Increment iterator
        loadVariable(iteratorName, iteratorType);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IADD);
        storeVariable(iteratorName, iteratorType);

        // Jump back to start
        mv.visitJumpInsn(GOTO, startLabel);
        mv.visitLabel(endLabel);

        // Remove iterator from variableIndex if necessary
    }

    private void generatePrint(PrintNode node) {
        TypeNode exprType = getType(node.getExpression());
        if (exprType instanceof IntegerTypeNode || exprType instanceof BooleanTypeNode) {
            generateExpression(node.getExpression());
            mv.visitMethodInsn(INVOKESTATIC, className, "printInt", "(I)V", false);
        } else if (exprType instanceof RealTypeNode) {
            generateExpression(node.getExpression());
            mv.visitMethodInsn(INVOKESTATIC, className, "printReal", "(D)V", false);
        } else if (exprType instanceof RecordTypeNode) {
            generateExpression(node.getExpression());
            String recordClassName = getRecordClassName(node.getExpression());
            mv.visitMethodInsn(INVOKESTATIC, className, "print" + recordClassName, "(L" + recordClassName + ";)V", false);
        } else {
            throw new RuntimeException("Unsupported type for print statement.");
        }
    }

    private void generatePrintMethodForRecord(String recordClassName, RecordTypeNode recordType) {
        MethodVisitor mvPrint = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "print" + recordClassName, "(L" + recordClassName + ";)V", null, null);
        mvPrint.visitCode();

        // Print opening brace
        mvPrint.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mvPrint.visitLdcInsn("{");
        mvPrint.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V", false);

        List<DeclarationNode> fields = recordType.getFields();
        for (int i = 0; i < fields.size(); i++) {
            DeclarationNode field = fields.get(i);
            String fieldName = field.getIdentifier();
            TypeNode fieldType = field.getType();
            String fieldDescriptor = getTypeDescriptor(fieldType);

            // Print field name
            mvPrint.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mvPrint.visitLdcInsn("\"" + fieldName + "\": ");
            mvPrint.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V", false);

            // Load field value
            mvPrint.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mvPrint.visitVarInsn(ALOAD, 0); // Load the record object
            mvPrint.visitFieldInsn(GETFIELD, recordClassName, fieldName, fieldDescriptor);

            // Print field value based on its type
            if (fieldType instanceof IntegerTypeNode || fieldType instanceof BooleanTypeNode) {
                mvPrint.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(I)V", false);
            } else if (fieldType instanceof RealTypeNode) {
                mvPrint.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(D)V", false);
            } else if (fieldType instanceof RecordTypeNode) {
                // Recursive call to print nested records
                String nestedRecordClassName = getRecordClassNameFromType((RecordTypeNode) fieldType);
                mvPrint.visitMethodInsn(INVOKESTATIC, className, "print" + nestedRecordClassName, "(L" + nestedRecordClassName + ";)V", false);
            } else {
                throw new RuntimeException("Unsupported field type in record for printing.");
            }

            // Print comma if not the last field
            if (i < fields.size() - 1) {
                mvPrint.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mvPrint.visitLdcInsn(", ");
                mvPrint.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V", false);
            }
        }

        // Print closing brace and newline
        mvPrint.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mvPrint.visitLdcInsn("}");
        mvPrint.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        mvPrint.visitInsn(RETURN);
        mvPrint.visitMaxs(0, 0);
        mvPrint.visitEnd();
    }



    private void generateExpression(ExpressionNode node) {
        if (node instanceof NumberNode) {
            int value = ((NumberNode) node).getValue();
            mv.visitLdcInsn(value);
        } else if (node instanceof RealNode) {
            double value = ((RealNode) node).getValue();
            mv.visitLdcInsn(value);
        } else if (node instanceof BooleanNode) {
            boolean value = ((BooleanNode) node).isValue();
            mv.visitInsn(value ? ICONST_1 : ICONST_0);
        } else if (node instanceof IdentifierNode) {
            String varName = ((IdentifierNode) node).getName();
            TypeNode varType = variableTypes.get(varName);
            loadVariable(varName, varType);
        } else if (node instanceof BinaryOpNode) {
            generateBinaryOperation((BinaryOpNode) node);
        } else if (node instanceof UnaryOpNode) {
            generateUnaryOperation((UnaryOpNode) node);
        } else if (node instanceof FunctionCallNode) {
            generateFunctionCall((FunctionCallNode) node);
        } else if (node instanceof ArrayAccessNode) {
            ArrayAccessNode arrayAccess = (ArrayAccessNode) node;
            // Load array reference
            generateExpression(arrayAccess.getArray());
            // Push index onto the stack
            generateExpression(arrayAccess.getIndex());
            TypeNode elementType = getType(arrayAccess);
            if (elementType instanceof IntegerTypeNode || elementType instanceof BooleanTypeNode) {
                mv.visitInsn(IALOAD); // Load integer or boolean from array
            } else if (elementType instanceof RealTypeNode) {
                mv.visitInsn(DALOAD); // Load double from array
            }
        } else if (node instanceof FieldAccessNode) {
            FieldAccessNode fieldAccess = (FieldAccessNode) node;
            // Generate code to load the record instance
            generateExpression(fieldAccess.getRecord());
            // Access the field
            String fieldName = fieldAccess.getFieldName();
            TypeNode fieldType = getFieldType(fieldAccess);
            String recordClassName = getRecordClassName(fieldAccess.getRecord());
            mv.visitFieldInsn(GETFIELD, recordClassName, fieldName, getTypeDescriptor(fieldType));
        } else {
            throw new RuntimeException("Unsupported expression type: " + node.getClass().getSimpleName());
        }
    }

    private void generateBinaryOperation(BinaryOpNode node) {
        generateExpression(node.getLeft());
        generateExpression(node.getRight());
        TypeNode type = getType(node.getLeft());
        String operator = node.getOperator();

        if (type instanceof IntegerTypeNode || type instanceof BooleanTypeNode) {
            switch (operator) {
                case "+":
                    mv.visitInsn(IADD);
                    break;
                case "-":
                    mv.visitInsn(ISUB);
                    break;
                case "*":
                    mv.visitInsn(IMUL);
                    break;
                case "/":
                    checkDivisionByZero();
                    mv.visitInsn(IDIV);
                    break;
                case "==":
                    compareIntegers(IF_ICMPEQ);
                    break;
                case ">":
                    compareIntegers(IF_ICMPGT);
                    break;
                case "<":
                    compareIntegers(IF_ICMPLT);
                    break;
                case "AND":
                    mv.visitInsn(IAND);
                    break;
                case "OR":
                    mv.visitInsn(IOR);
                    break;
                case "XOR":
                    mv.visitInsn(IXOR);
                    break;
                default:
                    throw new RuntimeException("Unsupported operator: " + operator);
            }
        } else if (type instanceof RealTypeNode) {
            switch (operator) {
                case "+":
                    mv.visitInsn(DADD);
                    break;
                case "-":
                    mv.visitInsn(DSUB);
                    break;
                case "*":
                    mv.visitInsn(DMUL);
                    break;
                case "/":
                    mv.visitInsn(DDIV);
                    break;
                case "==":
                case ">":
                case "<":
                    generateRealComparison(operator);
                    break;
                default:
                    throw new RuntimeException("Unsupported operator for real numbers: " + operator);
            }
        } else {
            throw new RuntimeException("Unsupported type for binary operation.");
        }
    }

    private void generateUnaryOperation(UnaryOpNode node) {
        generateExpression(node.getExpr());
        String operator = node.getOperator();
        if (operator.equals("NOT")) {
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IXOR);
        } else if (operator.equals("-")) {
            TypeNode exprType = getType(node.getExpr());
            if (exprType instanceof IntegerTypeNode) {
                mv.visitInsn(INEG);
            } else if (exprType instanceof RealTypeNode) {
                mv.visitInsn(DNEG);
            } else {
                throw new RuntimeException("Unsupported type for unary minus.");
            }
        } else {
            throw new RuntimeException("Unsupported unary operator: " + operator);
        }
    }

    private void generateFunctionCall(FunctionCallNode node) {
        String funcName = node.getFunctionName();
        FunctionNode function = functionTable.get(funcName);
        if (function == null) {
            throw new RuntimeException("Function '" + funcName + "' not declared.");
        }
        String methodDescriptor = getMethodDescriptor(function);
        // Push arguments onto the stack
        if (node.getArguments() != null) {
            for (ExpressionNode arg : node.getArguments()) {
                generateExpression(arg);
            }
        }
        mv.visitMethodInsn(INVOKESTATIC, className, funcName, methodDescriptor, false);
    }

    private void storeVariable(String varName, TypeNode type) {
        Integer index = variableIndex.get(varName);
        if (index == null) {
            throw new RuntimeException("Variable '" + varName + "' is not declared.");
        }
        if (type instanceof ArrayTypeNode || type instanceof RecordTypeNode) {
            mv.visitVarInsn(ASTORE, index);
        } else if (type instanceof IntegerTypeNode || type instanceof BooleanTypeNode) {
            mv.visitVarInsn(ISTORE, index);
        } else if (type instanceof RealTypeNode) {
            mv.visitVarInsn(DSTORE, index);
        } else {
            throw new RuntimeException("Unsupported type for storing variable.");
        }
    }

    private void loadVariable(String varName, TypeNode type) {
        Integer index = variableIndex.get(varName);
        if (index == null) {
            throw new RuntimeException("Variable '" + varName + "' is not declared.");
        }
        if (type instanceof ArrayTypeNode || type instanceof RecordTypeNode) {
            mv.visitVarInsn(ALOAD, index);
        } else if (type instanceof IntegerTypeNode || type instanceof BooleanTypeNode) {
            mv.visitVarInsn(ILOAD, index);
        } else if (type instanceof RealTypeNode) {
            mv.visitVarInsn(DLOAD, index);
        } else {
            throw new RuntimeException("Unsupported type for loading variable.");
        }
    }

    private TypeNode getType(ExpressionNode expr) {
        if (expr instanceof NumberNode) {
            return new IntegerTypeNode();
        } else if (expr instanceof RealNode) {
            return new RealTypeNode();
        } else if (expr instanceof BooleanNode) {
            return new BooleanTypeNode();
        } else if (expr instanceof IdentifierNode) {
            String varName = ((IdentifierNode) expr).getName();
            TypeNode type = variableTypes.get(varName);
            if (type == null) {
                throw new RuntimeException("Variable '" + varName + "' is not declared.");
            }
            return type;
        } else if (expr instanceof BinaryOpNode) {
            return getType(((BinaryOpNode) expr).getLeft());
        } else if (expr instanceof UnaryOpNode) {
            return getType(((UnaryOpNode) expr).getExpr());
        } else if (expr instanceof FunctionCallNode) {
            FunctionNode func = functionTable.get(((FunctionCallNode) expr).getFunctionName());
            if (func == null) {
                throw new RuntimeException("Function '" + ((FunctionCallNode) expr).getFunctionName() + "' not declared.");
            }
            return func.getReturnType();
        } else if (expr instanceof ArrayAccessNode) {
            TypeNode arrayType = getType(((ArrayAccessNode) expr).getArray());
            if (arrayType instanceof ArrayTypeNode) {
                return ((ArrayTypeNode) arrayType).getElementType();
            } else {
                throw new RuntimeException("Type Error: Attempting to index a non-array type.");
            }
        } else if (expr instanceof FieldAccessNode) {
            return getFieldType((FieldAccessNode) expr);
        } else {
            throw new RuntimeException("Unsupported expression type in getType.");
        }
    }

    private boolean typeEquals(TypeNode t1, TypeNode t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        if (t1 instanceof IntegerTypeNode && t2 instanceof RealTypeNode) {
        // Allow assigning Real to Integer with implicit conversion
            return true;
        }
        if (t1 instanceof BooleanTypeNode && t2 instanceof IntegerTypeNode) {
        // Allow assigning Integer to Boolean with implicit conversion
            return true;
        }
        if (t1.getClass().equals(t2.getClass())) {
            if (t1 instanceof ArrayTypeNode && t2 instanceof ArrayTypeNode) {
                return typeEquals(((ArrayTypeNode) t1).getElementType(), ((ArrayTypeNode) t2).getElementType());
            } else if (t1 instanceof RecordTypeNode && t2 instanceof RecordTypeNode) {
                // Additional checks can be added for records
                return true; // Simplified
            }
            return true;
        }
        return false;
    }

    private String typeName(TypeNode type) {
        if (type instanceof IntegerTypeNode) {
            return "Integer";
        } else if (type instanceof RealTypeNode) {
            return "Real";
        } else if (type instanceof BooleanTypeNode) {
            return "Boolean";
        } else if (type instanceof ArrayTypeNode) {
            return "Array of " + typeName(((ArrayTypeNode) type).getElementType());
        } else if (type instanceof RecordTypeNode) {
            return "Record";
        }
        return "Unknown";
    }

    private int getLocalVariableSize(TypeNode type) {
        if (type instanceof RealTypeNode) {
            return 2;
        }
        return 1;
    }

    private void checkDivisionByZero() {
        mv.visitInsn(DUP);
        Label continueLabel = new Label();
        mv.visitJumpInsn(IFNE, continueLabel);
        mv.visitTypeInsn(NEW, "java/lang/ArithmeticException");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Division by zero");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/ArithmeticException", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(ATHROW);
        mv.visitLabel(continueLabel);
    }

    private void compareIntegers(int jumpInsn) {
        Label trueLabel = new Label();
        Label endLabel = new Label();
        mv.visitJumpInsn(jumpInsn, trueLabel);
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(GOTO, endLabel);
        mv.visitLabel(trueLabel);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(endLabel);
    }

    private void generateRealComparison(String operator) {
        mv.visitInsn(DCMPL);
        Label trueLabel = new Label();
        Label endLabel = new Label();
        int jumpInsn;
        switch (operator) {
            case "==":
                jumpInsn = IFEQ;
                break;
            case ">":
                jumpInsn = IFGT;
                break;
            case "<":
                jumpInsn = IFLT;
                break;
            default:
                throw new RuntimeException("Unsupported real comparison operator: " + operator);
        }
        mv.visitJumpInsn(jumpInsn, trueLabel);
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(GOTO, endLabel);
        mv.visitLabel(trueLabel);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(endLabel);
    }

    private String generateRecordClass(RecordTypeNode recordType) {
        if (recordTypeClasses.containsKey(recordType)) {
            return recordTypeClasses.get(recordType);
        }
        String recordClassName = "RecordClass" + (recordClassCounter++);
        ClassWriter recordClassWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        recordClassWriter.visit(V1_8, ACC_PUBLIC, recordClassName, null, "java/lang/Object", null);

        // Generate fields
        for (DeclarationNode field : recordType.getFields()) {
            String fieldName = field.getIdentifier();
            String fieldDescriptor = getTypeDescriptor(field.getType());
            recordClassWriter.visitField(ACC_PUBLIC, fieldName, fieldDescriptor, null, null).visitEnd();
        }

        // Generate constructor
        MethodVisitor constructor = recordClassWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(1, 1);
        constructor.visitEnd();

        recordClassWriter.visitEnd();

        // Write the record class to a file
        try {
            FileOutputStream fos = new FileOutputStream(recordClassName + ".class");
            fos.write(recordClassWriter.toByteArray());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recordTypeClasses.put(recordType, recordClassName);
        return recordClassName;
    }

    private String getRecordClassName(ExpressionNode recordExpr) {
        TypeNode recordType = getType(recordExpr);
        if (recordType instanceof RecordTypeNode) {
            return recordTypeClasses.get(recordType);
        }
        throw new RuntimeException("Type Error: Expected a record type.");
    }

    private String getRecordClassNameFromType(RecordTypeNode recordType) {
        String className = recordTypeClasses.get(recordType);
        if (className == null) {
            className = generateRecordClass(recordType);
        }
        return className;
    }

    private TypeNode getFieldType(FieldAccessNode fieldAccess) {
        TypeNode recordType = getType(fieldAccess.getRecord());
        if (recordType instanceof RecordTypeNode) {
            RecordTypeNode recType = (RecordTypeNode) recordType;
            for (DeclarationNode field : recType.getFields()) {
                if (field.getIdentifier().equals(fieldAccess.getFieldName())) {
                    return field.getType();
                }
            }
            throw new RuntimeException("Field '" + fieldAccess.getFieldName() + "' not found in record.");
        } else {
            throw new RuntimeException("Type Error: Attempting to access field of non-record type.");
        }
    }

    private int getArrayTypeCode(TypeNode elementType) {
        if (elementType instanceof IntegerTypeNode || elementType instanceof BooleanTypeNode) {
            return T_INT;
        } else if (elementType instanceof RealTypeNode) {
            return T_DOUBLE;
        }
        // Add other types if needed
        return T_INT; // Default to int
    }

    private void generatePrintIntMethod() {
        MethodVisitor printMv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "printInt", "(I)V", null, null);
        printMv.visitCode();
        printMv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        printMv.visitVarInsn(ILOAD, 0);
        printMv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        printMv.visitInsn(RETURN);
        printMv.visitMaxs(2, 1);
        printMv.visitEnd();
    }

    private void generatePrintRealMethod() {
        MethodVisitor printMv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "printReal", "(D)V", null, null);
        printMv.visitCode();
        printMv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        printMv.visitVarInsn(DLOAD, 0);
        printMv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V", false);
        printMv.visitInsn(RETURN);
        printMv.visitMaxs(3, 2);
        printMv.visitEnd();
    }
}
