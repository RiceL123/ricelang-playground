/*
 * Checker.java
 *
 * This VC compiler pass is responsible for performing semantic analysis 
 * on the abstract syntax tree (AST) of a VC program. It checks for scope and 
 * type rules, decorates the AST with type information, and links identifiers 
 * to their declarations.
 *
 * Sun 09 Mar 2025 08:44:27 AEDT
 *
 */
package VC.Checker;

import VC.ASTs.*;
import VC.ErrorReporter;
import VC.Scanner.SourcePosition;
import VC.StdEnvironment;
import java.util.Objects;
import java.util.Optional;

public final class Checker implements Visitor {

    // Enum for error messages
    private enum ErrorMessage {
        MISSING_MAIN("*0: main function is missing"),
        // Defined occurrences of identifiers (global, local, and parameters)
        MAIN_RETURN_TYPE_NOT_INT("*1: return type of main is not int"),
        IDENTIFIER_REDECLARED("*2: identifier redeclared"),
        IDENTIFIER_DECLARED_VOID("*3: identifier declared void"),
        IDENTIFIER_DECLARED_VOID_ARRAY("*4: identifier declared void[]"),
        // applied occurrences of identifiers
        IDENTIFIER_UNDECLARED("*5: identifier undeclared"),
        // assignments
        INCOMPATIBLE_TYPE_FOR_ASSIGNMENT("*6: incompatible type for ="),
        INVALID_LVALUE_IN_ASSIGNMENT("*7: invalid lvalue in assignment"),
        // types for expressions 
        INCOMPATIBLE_TYPE_FOR_RETURN("*8: incompatible type for return"),
        INCOMPATIBLE_TYPE_FOR_BINARY_OPERATOR("*9: incompatible type for this binary operator"),
        INCOMPATIBLE_TYPE_FOR_UNARY_OPERATOR("*10: incompatible type for this unary operator"),
        // scalars
        ARRAY_FUNCTION_AS_SCALAR("*11: attempt to use an array/function as a scalar"),
        // arrays
        SCALAR_FUNCTION_AS_ARRAY("*12: attempt to use a scalar/function as an array"),
        WRONG_TYPE_FOR_ARRAY_INITIALISER("*13: wrong type for element in array initialiser"),
        INVALID_INITIALISER_ARRAY_FOR_SCALAR("*14: invalid initialiser: array initialiser for scalar"),
        INVALID_INITIALISER_SCALAR_FOR_ARRAY("*15: invalid initialiser: scalar initialiser for array"),
        EXCESS_ELEMENTS_IN_ARRAY_INITIALISER("*16: excess elements in array initialiser"),
        ARRAY_SUBSCRIPT_NOT_INTEGER("*17: array subscript is not an integer"),
        ARRAY_SIZE_MISSING("*18: array size missing"),
        // functions
        SCALAR_ARRAY_AS_FUNCTION("*19: attempt to reference a scalar/array as a function"),
        // conditional expressions in if, for and while
        IF_CONDITIONAL_NOT_BOOLEAN("*20: if conditional is not boolean"),
        FOR_CONDITIONAL_NOT_BOOLEAN("*21: for conditional is not boolean"),
        WHILE_CONDITIONAL_NOT_BOOLEAN("*22: while conditional is not boolean"),
        // break and continue
        BREAK_NOT_IN_LOOP("*23: break must be in a while/for"),
        CONTINUE_NOT_IN_LOOP("*24: continue must be in a while/for"),
        // parameters
        TOO_MANY_ACTUAL_PARAMETERS("*25: too many actual parameters"),
        TOO_FEW_ACTUAL_PARAMETERS("*26: too few actual parameters"),
        WRONG_TYPE_FOR_ACTUAL_PARAMETER("*27: wrong type for actual parameter"),
        // reserved for errors that I may have missed (J. Xue)
        MISC_1("*28: misc 1"),
        MISC_2("*29: misc 2"),
        // the following two checks are optional 
        STATEMENTS_NOT_REACHED("*30: statement(s) not reached"),
        MISSING_RETURN_STATEMENT("*31: missing return statement");

        private final String message;

        ErrorMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private final SymbolTable idTable;
    private static final SourcePosition dummyPos = new SourcePosition();
    private final ErrorReporter reporter;

    public Checker(ErrorReporter reporter) {
        this.reporter = Objects.requireNonNull(reporter, "ErrorReporter must not be null");
        this.idTable = new SymbolTable();
        establishStdEnvironment();
    }

    public void check(AST ast) {
        ast.visit(this, null);
    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////////////// Program ///////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitProgram(Program ast, Object o) {
        ast.FL.visit(this, null);
        idTable.retrieve("main").ifPresentOrElse(
                entry -> {
                    if (!(entry.attr instanceof FuncDecl mainDecl)) {
                        reporter.reportError(ErrorMessage.MISSING_MAIN.getMessage(), "", entry.attr.position);
                        return;
                    }

                    if (!mainDecl.T.isIntType()) {
                        reporter.reportError(ErrorMessage.MAIN_RETURN_TYPE_NOT_INT.getMessage() + ": got %", entry.attr.T.toString(), entry.attr.position);
                    }

                    // if (!mainDecl.PL.isEmptyParaList()) {
                    //     reporter.reportError(ErrorMessage.MISC_1 + ": %", "main function requires an empty parameter list", entry.attr.position);
                    // }
                },
                () -> {
                    reporter.reportError(ErrorMessage.MISSING_MAIN.getMessage(), "", ast.position);
                }
        );

        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////////// Declaration List //////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitDeclList(DeclList ast, Object o) {
        ast.D.visit(this, null);
        ast.DL.visit(this, null);
        return null;
    }

    @Override
    public Object visitEmptyDeclList(EmptyDeclList ast, Object o) {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////// Function Declaration ////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitFuncDecl(FuncDecl ast, Object o) {
        idTable.retrieveOneLevel(ast.I.spelling).ifPresent(entry -> {
            reporter.reportError(ErrorMessage.IDENTIFIER_REDECLARED.getMessage() + ": %", ast.I.spelling, ast.position);
        });

        // not even syntatically possible
        if (ast.T instanceof ArrayType arrayType && arrayType.isVoidType()) { 
            reporter.reportError(ErrorMessage.IDENTIFIER_DECLARED_VOID_ARRAY.getMessage() + ": %", ast.I.spelling, ast.position);
        }

        idTable.insert(ast.I.spelling, ast);

        ast.S.visit(this, ast);

        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////// Variable Declarations ///////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private void declareVariable(Ident ident, Decl decl) {
        idTable.retrieveOneLevel(ident.spelling).ifPresent(entry
                -> reporter.reportError(ErrorMessage.IDENTIFIER_REDECLARED.getMessage() + ": %", ident.spelling, ident.position)
        );
        idTable.insert(ident.spelling, decl);
        ident.visit(this, null);
    }

    private record InheritedArrayDeclAttrs(Type expectedType, Integer position) {

        public InheritedArrayDeclAttrs(Type expectedType) {
            this(expectedType, 0);
        }
    }

    private record SynthesizedArrayDeclAttrs(Integer arrayInitExprLength) {

    }

    private void checkVar(Ident I, Type T, Expr E, SourcePosition position) {
        Objects.requireNonNull(I, "Ident (I) cannot be null");
        Objects.requireNonNull(T, "Type (T) cannot be null");
        Objects.requireNonNull(E, "Expr (E) cannot be null");
        Objects.requireNonNull(position, "SourcePosition (position) cannot be null");

        if (!T.isArrayType() && E instanceof ArrayInitExpr) {
            reporter.reportError(ErrorMessage.INVALID_INITIALISER_ARRAY_FOR_SCALAR.getMessage(), "", position);
            T = StdEnvironment.errorType;
        }

        if (T.isVoidType()) {
            reporter.reportError(ErrorMessage.IDENTIFIER_DECLARED_VOID.getMessage() + ": %", I.spelling, I.position);
            T = StdEnvironment.errorType;
        }

        if (T instanceof ArrayType arrayType && arrayType.T instanceof VoidType) {
            reporter.reportError(ErrorMessage.IDENTIFIER_DECLARED_VOID_ARRAY.getMessage() + ": %", I.spelling, I.position);
            T = StdEnvironment.errorType;
        }

        if (T instanceof ArrayType arrayType) {
            // index should be an intliteral according to the grammar or empty if there is an initializer
            arrayType.E.visit(this, null);

            // check the initialization
            Object res = E.visit(this, new InheritedArrayDeclAttrs(arrayType.T));

            if (arrayType.E.isEmptyExpr() && !(res instanceof SynthesizedArrayDeclAttrs)) {
                reporter.reportError(ErrorMessage.ARRAY_SIZE_MISSING.getMessage() + ": %", I.spelling, arrayType.position);
            }

            if (!E.isEmptyExpr() && !(E instanceof ArrayInitExpr)) {
                reporter.reportError(ErrorMessage.INVALID_INITIALISER_SCALAR_FOR_ARRAY.getMessage() + ": %", I.spelling, position);
            }

            if (res instanceof SynthesizedArrayDeclAttrs attrs) {
                if (arrayType.E.isEmptyExpr()) {
                    // set the empty array length
                    arrayType.E = new IntExpr(new IntLiteral(attrs.arrayInitExprLength().toString(), arrayType.E.position), arrayType.E.position);
                } else {
                    if (!(arrayType.E instanceof IntExpr intExpr)) {
                        throw new Error("Array Init size must be IntExpr but found: " + arrayType.E);
                    }

                    Integer expectedLength = Integer.valueOf(intExpr.IL.spelling);

                    // if its less than the expected length, the remaining values will be zeroed out in jasmin
                    if (attrs.arrayInitExprLength() > expectedLength) {
                        reporter.reportError(ErrorMessage.EXCESS_ELEMENTS_IN_ARRAY_INITIALISER.getMessage() + ": %", I.spelling, arrayType.E.position);
                    }
                }
            }

            return;
        }

        if (!(T instanceof ErrorType)) {
            E.visit(this, null);
        }

        if (!T.assignable(E.type)) {
            reporter.reportError(ErrorMessage.INCOMPATIBLE_TYPE_FOR_ASSIGNMENT.getMessage(), "", position);
        }
    }

    @Override
    public Object visitGlobalVarDecl(GlobalVarDecl ast, Object o) {
        declareVariable(ast.I, ast);
        checkVar(ast.I, ast.T, ast.E, ast.position);
        return null;
    }

    @Override
    public Object visitLocalVarDecl(LocalVarDecl ast, Object o) {
        declareVariable(ast.I, ast);
        checkVar(ast.I, ast.T, ast.E, ast.position);
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////////// Array Initializer /////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitArrayInitExpr(ArrayInitExpr ast, Object o) {
        if (!(o instanceof InheritedArrayDeclAttrs)) {
            reporter.reportError(ErrorMessage.INVALID_INITIALISER_ARRAY_FOR_SCALAR.getMessage(), "", ast.position);
        }

        return ast.IL.visit(this, o);
    }

    @Override
    public Object visitArrayExprList(ArrayExprList ast, Object o) {
        ast.E.visit(this, null);

        InheritedArrayDeclAttrs inheritedAttrs = (InheritedArrayDeclAttrs) o;

        if (!inheritedAttrs.expectedType().assignable(ast.E.type)) {
            reporter.reportError(ErrorMessage.WRONG_TYPE_FOR_ARRAY_INITIALISER.getMessage() + ": at position %", inheritedAttrs.position().toString(), ast.E.position);
        }

        if (inheritedAttrs.expectedType().isFloatType() && ast.E.type.isIntType()) {
            ast.E = new UnaryExpr(new Operator("i2f", ast.position), ast.E, ast.position);
        }

        return ast.EL.visit(this, new InheritedArrayDeclAttrs(inheritedAttrs.expectedType(), inheritedAttrs.position() + 1));
    }

    @Override
    public Object visitEmptyArrayExprList(EmptyArrayExprList ast, Object o) {
        Integer arrayLength = ((InheritedArrayDeclAttrs) o).position;
        return new SynthesizedArrayDeclAttrs(arrayLength);
    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////////////// Parameters ////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitParaList(ParaList ast, Object o) {
        ast.P.visit(this, null);
        ast.PL.visit(this, null);
        return null;
    }

    @Override
    public Object visitEmptyParaList(EmptyParaList ast, Object o) {
        return null;
    }

    @Override
    public Object visitParaDecl(ParaDecl ast, Object o) {
        declareVariable(ast.I, ast);

        if (ast.T.isVoidType()) {
            reporter.reportError(ErrorMessage.IDENTIFIER_DECLARED_VOID.getMessage() + ": %", ast.I.spelling, ast.I.position);
        } else if (ast.T instanceof ArrayType arrayType && arrayType.T instanceof VoidType) {
            reporter.reportError(ErrorMessage.IDENTIFIER_DECLARED_VOID_ARRAY.getMessage() + ": %", ast.I.spelling, ast.I.position);
        }

        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// Types //////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitErrorType(ErrorType ast, Object o) {
        return StdEnvironment.errorType;
    }

    @Override
    public Object visitBooleanType(BooleanType ast, Object o) {
        return StdEnvironment.booleanType;
    }

    @Override
    public Object visitIntType(IntType ast, Object o) {
        return StdEnvironment.intType;
    }

    @Override
    public Object visitFloatType(FloatType ast, Object o) {
        return StdEnvironment.floatType;
    }

    @Override
    public Object visitStringType(StringType ast, Object o) {
        return StdEnvironment.stringType;
    }

    @Override
    public Object visitVoidType(VoidType ast, Object o) {
        return StdEnvironment.voidType;
    }

    @Override
    public Object visitArrayType(ArrayType ast, Object o) {
        return ast;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////// Literals, Identifiers and Operators ///////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitIdent(Ident I, Object o) {
        Optional<IdEntry> binding = idTable.retrieve(I.spelling);
        binding.ifPresent(entry -> I.decl = entry.attr); // Link the identifier to its declaration
        return binding.map(entry -> entry.attr).orElse(null);
    }

    @Override
    public Object visitBooleanLiteral(BooleanLiteral SL, Object o) {
        return StdEnvironment.booleanType;
    }

    @Override
    public Object visitIntLiteral(IntLiteral IL, Object o) {
        return StdEnvironment.intType;
    }

    @Override
    public Object visitFloatLiteral(FloatLiteral IL, Object o) {
        return StdEnvironment.floatType;
    }

    @Override
    public Object visitStringLiteral(StringLiteral IL, Object o) {
        return StdEnvironment.stringType;
    }

    @Override
    public Object visitOperator(Operator O, Object o) {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Statement List //////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitStmtList(StmtList ast, Object o) {
        ast.S.visit(this, o);
        if (ast.S instanceof ReturnStmt && ast.SL instanceof StmtList) {
            reporter.reportError(ErrorMessage.STATEMENTS_NOT_REACHED.getMessage(), "", ast.SL.position);
        }
        ast.SL.visit(this, o);
        return null;
    }

    @Override
    public Object visitEmptyStmtList(EmptyStmtList ast, Object o) {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////////////// Statements ////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private record InheritedStmtAttrs(Type expectedReturnType, boolean isInLoop) {

        public InheritedStmtAttrs(Type expectedReturnType) {
            this(expectedReturnType, false);
        }
    }

    @Override
    public Object visitCompoundStmt(CompoundStmt ast, Object o) {
        idTable.openScope();

        if (o instanceof FuncDecl funcDecl) {
            funcDecl.PL.visit(this, null); // declare the parameters within the scope
        }

        InheritedStmtAttrs attrs = (o instanceof FuncDecl funcDecl)
                ? new InheritedStmtAttrs(funcDecl.T, false)
                : (o instanceof InheritedStmtAttrs inheritedStmtAttrs) ? inheritedStmtAttrs
                        : null; // should never be null

        assert attrs != null;

        ast.DL.visit(this, null);

        ast.SL.visit(this, attrs);

        idTable.closeScope();

        return o;
    }

    @Override
    public Object visitExprStmt(ExprStmt ast, Object o) {
        Object res = ast.E.visit(this, o);

        if (ast.E.type.isArrayType()) {
            reporter.reportError(ErrorMessage.ARRAY_FUNCTION_AS_SCALAR.getMessage() + ": ", "", ast.E.position);
        }

        return res;
    }

    @Override
    public Object visitEmptyStmt(EmptyStmt ast, Object o) {
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt ast, Object o) {
        ast.E.visit(this, null);
        if (!(ast.E.type.isBooleanType())) {
            reporter.reportError(ErrorMessage.IF_CONDITIONAL_NOT_BOOLEAN.getMessage() + " (found: %)", ast.E.type.toString(), ast.position);
        }

        ast.S1.visit(this, o);
        ast.S2.visit(this, o);

        return null;
    }

    @Override
    public Object visitWhileStmt(WhileStmt ast, Object o) {
        ast.E.visit(this, o);
        if (!ast.E.type.isBooleanType()) {
            reporter.reportError(ErrorMessage.WHILE_CONDITIONAL_NOT_BOOLEAN.getMessage() + " (found: %)", ast.E.type.toString(), ast.position);
        }

        if (!(o instanceof InheritedStmtAttrs inheritedStmtAttrs)) {
            throw new Error("expected inheritedStmtAttrs but found: " + o);
        }

        ast.S.visit(this, new InheritedStmtAttrs(inheritedStmtAttrs.expectedReturnType(), true));

        return null;
    }

    @Override
    public Object visitForStmt(ForStmt ast, Object o) {
        ast.E1.visit(this, null);

        ast.E2.visit(this, null);
        if (ast.E2.isEmptyExpr()) {
            ast.E2.type = StdEnvironment.booleanType;
            ast.E2 = new BooleanExpr(new BooleanLiteral("true", dummyPos), dummyPos);
        } else if (!ast.E2.type.isBooleanType()) {
            reporter.reportError(ErrorMessage.FOR_CONDITIONAL_NOT_BOOLEAN.getMessage() + " (found: %)", ast.E2.type.toString(), ast.position);
        }

        ast.E3.visit(this, null);

        if (!(o instanceof InheritedStmtAttrs inheritedStmtAttrs)) {
            throw new Error("expected inheritedStmtAttrs but found: " + o);
        }

        ast.S.visit(this, new InheritedStmtAttrs(inheritedStmtAttrs.expectedReturnType(), true));

        return null;
    }

    @Override
    public Object visitBreakStmt(BreakStmt ast, Object o) {
        if (!(o instanceof InheritedStmtAttrs inheritedStmtAttrs)) {
            throw new Error("expected inheritedStmtAttrs but found: " + o);
        }

        if (!inheritedStmtAttrs.isInLoop()) {
            reporter.reportError(ErrorMessage.BREAK_NOT_IN_LOOP.getMessage(), "", ast.position);
        }

        return null;
    }

    @Override
    public Object visitContinueStmt(ContinueStmt ast, Object o) {
        if (!(o instanceof InheritedStmtAttrs inheritedStmtAttrs)) {
            throw new Error("expected inheritedStmtAttrs but found: " + o);
        }

        if (!inheritedStmtAttrs.isInLoop()) {
            reporter.reportError(ErrorMessage.CONTINUE_NOT_IN_LOOP.getMessage(), "", ast.position);
        }

        return null;
    }

    @Override
    public Object visitReturnStmt(ReturnStmt ast, Object o) {
        if (!(o instanceof InheritedStmtAttrs inheritedStmtAttrs)) {
            throw new Error("No inherited Stmt attrs");
        }

        ast.E.visit(this, null);

        if (!inheritedStmtAttrs.expectedReturnType().assignable(ast.E.type)) {
            reporter.reportError(ErrorMessage.INCOMPATIBLE_TYPE_FOR_RETURN.getMessage() + ": %" + "but expected: " + inheritedStmtAttrs.expectedReturnType(), ast.E.type.toString(), ast.position);
        }

        if (inheritedStmtAttrs.expectedReturnType().isFloatType() && ast.E.type.isIntType()) {
            Operator op = new Operator("i2f", dummyPos);
            Expr eAST = new UnaryExpr(op, ast.E, dummyPos);
            eAST.type = StdEnvironment.floatType;
            ast.E = eAST;
        }

        return null;
    }

    @Override
    public Object visitEmptyCompStmt(EmptyCompStmt ast, Object o) {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////////////// Expressions ///////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitEmptyExpr(EmptyExpr ast, Object o) {
        ast.type = (ast.parent instanceof ReturnStmt) ? StdEnvironment.voidType : StdEnvironment.errorType;
        return ast.type;
    }

    @Override
    public Object visitBooleanExpr(BooleanExpr ast, Object o) {
        ast.type = StdEnvironment.booleanType;
        return ast.type;
    }

    @Override
    public Object visitIntExpr(IntExpr ast, Object o) {
        ast.type = StdEnvironment.intType;
        return ast.type;
    }

    @Override
    public Object visitFloatExpr(FloatExpr ast, Object o) {
        ast.type = StdEnvironment.floatType;
        return ast.type;
    }

    @Override
    public Object visitVarExpr(VarExpr ast, Object o) {
        ast.type = (Type) ast.V.visit(this, null);
        return ast.type;
    }

    @Override
    public Object visitStringExpr(StringExpr ast, Object o) {
        ast.type = StdEnvironment.stringType;
        return ast.type;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr ast, Object o) {
        ast.E.visit(this, null);
        ast.type = ast.E.type;

        // arrays as a whole aren't allowed on unary expressions
        if (ast.E.type.isArrayType()) {
            reporter.reportError(ErrorMessage.ARRAY_FUNCTION_AS_SCALAR.getMessage() + ": %", ast.E.type.toString(), ast.position);
            ast.type = StdEnvironment.errorType;
        }

        switch (ast.O.spelling) {
            case "!" -> {
                if (ast.E.type.isBooleanType()) {
                    ast.O.spelling = "i" + ast.O.spelling;
                } else {
                    reporter.reportError(ErrorMessage.INCOMPATIBLE_TYPE_FOR_UNARY_OPERATOR.getMessage() + ": %", ast.O.spelling, ast.O.position);
                    ast.type = StdEnvironment.errorType;
                }
            }
            case "+", "-" -> {
                if (ast.E.type.isFloatType()) {
                    ast.O.spelling = "f" + ast.O.spelling;
                } else if (ast.E.type.isIntType()) {
                    ast.O.spelling = "i" + ast.O.spelling;
                } else {
                    reporter.reportError(ErrorMessage.INCOMPATIBLE_TYPE_FOR_UNARY_OPERATOR.getMessage() + ": %", ast.O.spelling, ast.O.position);
                    ast.type = StdEnvironment.errorType;
                }
            }
            default -> {
                throw new Error("expected an unaryoperator but got: " + ast.O.spelling);
            }
        }

        return ast.type;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr ast, Object o) {
        ast.E1.visit(this, o);
        ast.E2.visit(this, o);

        // avoid spurrious errors - if either type is an error we can just leave to avoid spurrious errors
        if (ast.E1.type.isErrorType() || ast.E2.type.isErrorType()) {
            ast.type = StdEnvironment.errorType;
            return null;
        }

        // arrays as a whole aren't allowed on binary expressions
        if (ast.E1.type.isArrayType()) {
            String tokenName = ast.E1.toString();
            if (ast.E1 instanceof VarExpr varExpr && varExpr.V instanceof SimpleVar simpleVar) {
                tokenName = simpleVar.I.spelling;
            }
            reporter.reportError(ErrorMessage.ARRAY_FUNCTION_AS_SCALAR.getMessage() + ": %", tokenName, ast.position);
        }

        if (ast.E2.type.isArrayType()) {
            String tokenName = ast.E2.toString();
            if (ast.E2 instanceof VarExpr varExpr && varExpr.V instanceof SimpleVar simpleVar) {
                tokenName = simpleVar.I.spelling;
            }
            reporter.reportError(ErrorMessage.ARRAY_FUNCTION_AS_SCALAR.getMessage() + ": %", tokenName, ast.position);
        }

        if (ast.E1.type.isFloatType() && ast.E2.type.isIntType()) {
            ast.E2 = new UnaryExpr(new Operator("i2f", dummyPos), ast.E2, dummyPos);
            ast.E2.type = StdEnvironment.floatType;
        }

        if (ast.E1.type.isIntType() && ast.E2.type.isFloatType()) {
            ast.E1 = new UnaryExpr(new Operator("i2f", dummyPos), ast.E1, dummyPos);
            ast.E1.type = StdEnvironment.floatType;
        }

        switch (ast.O.spelling) {
            case "+", "-", "*", "/" -> {
                if (ast.E1.type.isFloatType() && ast.E2.type.isFloatType()) {
                    ast.O.spelling = "f" + ast.O.spelling;
                    ast.type = StdEnvironment.floatType;
                } else if (ast.E1.type.isIntType() && ast.E2.type.isIntType()) {
                    ast.O.spelling = "i" + ast.O.spelling;
                    ast.type = StdEnvironment.intType;
                } else {
                    reporter.reportError(ErrorMessage.INCOMPATIBLE_TYPE_FOR_BINARY_OPERATOR.getMessage() + ": %" + " got: " + ast.E1.type.toString() + " " + ast.O.spelling + " " + ast.E2.type.toString(), ast.O.spelling, ast.O.position);
                    ast.type = StdEnvironment.errorType;
                }
            }
            case "<", "<=", ">", ">=" -> {
                ast.type = StdEnvironment.booleanType;

                if (ast.E1.type.isFloatType() && ast.E2.type.isFloatType()) {
                    ast.O.spelling = "f" + ast.O.spelling;
                } else if (ast.E1.type.isIntType() && ast.E2.type.isIntType()) {
                    ast.O.spelling = "i" + ast.O.spelling;
                } else {
                    reporter.reportError(ErrorMessage.INCOMPATIBLE_TYPE_FOR_BINARY_OPERATOR.getMessage() + ": %" + " got: " + ast.E1.type.toString() + " " + ast.O.spelling + " " + ast.E2.type.toString(), ast.O.spelling, ast.O.position);
                    ast.type = StdEnvironment.errorType;
                }
            }
            case "&&", "||" -> {
                ast.type = StdEnvironment.booleanType;

                if (ast.E1.type.isBooleanType() && ast.E2.type.isBooleanType()) {
                    ast.O.spelling = "i" + ast.O.spelling;
                } else {
                    reporter.reportError(ErrorMessage.INCOMPATIBLE_TYPE_FOR_BINARY_OPERATOR.getMessage() + ": %" + " got: " + ast.E1.type.toString() + " " + ast.O.spelling + " " + ast.E2.type.toString(), ast.O.spelling, ast.O.position);
                    ast.type = StdEnvironment.errorType;
                }
            }
            case "==", "!=" -> {
                ast.type = StdEnvironment.booleanType;

                if (ast.E1.type.isFloatType() && ast.E2.type.isFloatType()) {
                    ast.O.spelling = "f" + ast.O.spelling;
                } else if ((ast.E1.type.isIntType() && ast.E2.type.isIntType()) || (ast.E1.type.isBooleanType() && ast.E2.type.isBooleanType())) {
                    ast.O.spelling = "i" + ast.O.spelling;
                } else {
                    reporter.reportError(ErrorMessage.INCOMPATIBLE_TYPE_FOR_BINARY_OPERATOR.getMessage() + ": %" + " got: " + ast.E1.type.toString() + " " + ast.O.spelling + " " + ast.E2.type.toString(), ast.O.spelling, ast.O.position);
                    ast.type = StdEnvironment.errorType;
                }
            }
            default -> {
                throw new Error("expected a binary operator but got: " + ast.O.spelling);
            }
        }

        return ast.type;
    }

    @Override
    public Object visitAssignExpr(AssignExpr ast, Object o) {
        ast.E1.visit(this, null);
        ast.E2.visit(this, null);

        ast.type = ast.E1.type;

        if (ast.E1.type.isFloatType() && ast.E2.type.isIntType()) {
            ast.E2 = new UnaryExpr(new Operator("i2f", dummyPos), ast.E2, dummyPos);
            ast.E2.type = StdEnvironment.floatType;
        }

        if (ast.E1.type.isArrayType()) {
            reporter.reportError(ErrorMessage.ARRAY_FUNCTION_AS_SCALAR.getMessage() + ": %", ast.E1.toString(), ast.E1.position);
            ast.type = StdEnvironment.errorType;
        }

        if ((!(ast.E1 instanceof VarExpr) && !(ast.E1 instanceof ArrayExpr)) || ast.E1.type.isVoidType() || ast.E1.type.isErrorType()) {
            reporter.reportError(ErrorMessage.INVALID_LVALUE_IN_ASSIGNMENT.getMessage() + ": %", ast.E1.toString(), ast.E1.position);
            ast.type = StdEnvironment.errorType;
        }

        if (!ast.E1.type.assignable(ast.E2.type)) {
            reporter.reportError(ErrorMessage.INCOMPATIBLE_TYPE_FOR_ASSIGNMENT.getMessage() + ": %", "", ast.position);
            ast.type = StdEnvironment.errorType;
        }

        return ast.type;
    }

    @Override
    public Object visitArrayExpr(ArrayExpr ast, Object o) {
        // get the type (this should be a SimVar)
        ast.V.visit(this, null);

        // get the index
        ast.E.visit(this, null);

        String tokenName = ast.V.toString();
        if (ast.V instanceof SimpleVar simpleVar) {
            tokenName = simpleVar.I.spelling;
        }

        if (!ast.E.type.isIntType()) {
            reporter.reportError(ErrorMessage.ARRAY_SUBSCRIPT_NOT_INTEGER.getMessage() + ": %", tokenName, ast.E.position);
            ast.type = StdEnvironment.errorType;
            return null;
        }

        // this is an element of the array so that type is not ArrayType
        if (ast.V.type instanceof ArrayType arrayType) {
            ast.type = arrayType.T;
        } else {
            ast.type = StdEnvironment.errorType;
            reporter.reportError(ErrorMessage.SCALAR_FUNCTION_AS_ARRAY.getMessage() + ": %", tokenName, ast.position);
        }

        return ast.type;
    }

    ///////////////////////////////////////////////////////////////////////////
    ////////////////////////////// Function Call //////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitCallExpr(CallExpr ast, Object o) {
        // when calling a function, we gotta check that the correct number of arguments are passed.
        Optional<IdEntry> res = idTable.retrieve(ast.I.spelling);

        res.ifPresentOrElse(
                entry -> {
                    if (!entry.attr.isFuncDecl()) {
                        // idk about this error message whateva
                        reporter.reportError(ErrorMessage.SCALAR_ARRAY_AS_FUNCTION.getMessage() + ": %", ast.I.spelling, ast.position);
                        ast.type = StdEnvironment.errorType;
                        return;
                    }

                    // check if the argument list is the correct type relative to the entry
                    List paralist = ((FuncDecl) entry.attr).PL;

                    // while visiting this wee ned to check that the paralist list is the same....
                    ast.AL.visit(this, paralist);

                    // from the entry we can now define the thing its referencing i guess
                    ast.type = entry.attr.T;
                    ast.I.decl = entry.attr;
                },
                () -> {
                    reporter.reportError(ErrorMessage.IDENTIFIER_UNDECLARED.getMessage() + ": %", ast.I.spelling, ast.position);
                    ast.type = StdEnvironment.errorType;
                }
        );

        return ast.type;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////// Function Call Arguments /////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitEmptyArgList(EmptyArgList ast, Object o) {
        // if we are at the end of the arglist we shuld be at the end of paralist
        if (!(o instanceof EmptyParaList)) {
            reporter.reportError(ErrorMessage.TOO_FEW_ACTUAL_PARAMETERS.getMessage() + ": %", ast.toString(), ast.position);
        }

        return null;
    }

    @Override
    public Object visitArgList(ArgList ast, Object o) {
        if (o instanceof EmptyParaList) {
            reporter.reportError(ErrorMessage.TOO_MANY_ACTUAL_PARAMETERS.getMessage() + ": %", ast.toString(), ast.position);
            return null;
        }

        if (!(o instanceof ParaList paraList)) {
            throw new Error("arglist must be checking against paralist but found: " + o);
        }

        Type expectedType = paraList.P.T;

        ast.A.visit(this, expectedType);
        ast.AL.visit(this, paraList.PL);

        return null;
    }

    @Override
    public Object visitArg(Arg ast, Object o) {
        if (!(o instanceof Type paraType)) {
            throw new Error("visitArg needs an expected parameter type but found: " + o);
        }

        ast.E.visit(this, o);
        Type argType = ast.E.type;

        if (argType instanceof ArrayType aType && paraType instanceof ArrayType pType) {
            argType = aType.T;
            paraType = pType.T;
        }

        if (!paraType.assignable(argType)) {
            reporter.reportError(ErrorMessage.WRONG_TYPE_FOR_ACTUAL_PARAMETER.getMessage() + ": %" + " - expected: " + paraType.toString() + ", got: " + argType.toString(), ast.E.toString(), ast.position);
            ast.type = StdEnvironment.errorType;
            return null;
        }

        if (paraType.isFloatType() && argType.isIntType()) {
            Operator op = new Operator("i2f", dummyPos);
            UnaryExpr eAST = new UnaryExpr(op, ast.E, dummyPos);
            eAST.type = StdEnvironment.floatType;
            ast.E = eAST;
        }

        return ast.type = paraType;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////////////// Simple Var ////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitSimpleVar(SimpleVar ast, Object o) {
        // Each SimpleVar node is decorated by setting its type field to the type of the expression.
        idTable.retrieve(ast.I.spelling).ifPresentOrElse(
                entry -> {
                    if (entry.attr.isFuncDecl()) {
                        reporter.reportError(ErrorMessage.ARRAY_FUNCTION_AS_SCALAR.getMessage() + ": %", ast.I.spelling, ast.position);
                        ast.type = StdEnvironment.errorType;
                        return;
                    }

                    ast.type = entry.attr.T;
                    ast.I.decl = entry.attr;
                },
                () -> {
                    reporter.reportError(ErrorMessage.IDENTIFIER_UNDECLARED.getMessage() + ": %", ast.I.spelling, ast.position);
                    ast.type = StdEnvironment.errorType;
                }
        );
        return ast.type;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////////// Built-in Functions ////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private FuncDecl declareStdFunc(Type resultType, String id, VC.ASTs.List pl) {
        var binding = new FuncDecl(resultType, new Ident(id, dummyPos), pl, new EmptyStmt(dummyPos), dummyPos);
        idTable.insert(id, binding);
        return binding;
    }

    private final static Ident dummyI = new Ident("x", dummyPos);

    private void establishStdEnvironment() {
        // Define four primitive types
        // errorType is assigned to ill-typed expressions
        StdEnvironment.booleanType = new BooleanType(dummyPos);
        StdEnvironment.intType = new IntType(dummyPos);
        StdEnvironment.floatType = new FloatType(dummyPos);
        StdEnvironment.stringType = new StringType(dummyPos);
        StdEnvironment.voidType = new VoidType(dummyPos);
        StdEnvironment.errorType = new ErrorType(dummyPos);

        // enter into the declarations for built-in functions into the table
        StdEnvironment.getIntDecl = declareStdFunc(StdEnvironment.intType, "getInt", new EmptyParaList(dummyPos));
        StdEnvironment.putIntDecl = declareStdFunc(StdEnvironment.voidType, "putInt", new ParaList(new ParaDecl(StdEnvironment.intType, dummyI, dummyPos), new EmptyParaList(dummyPos), dummyPos));
        StdEnvironment.putIntLnDecl = declareStdFunc(StdEnvironment.voidType, "putIntLn", new ParaList(new ParaDecl(StdEnvironment.intType, dummyI, dummyPos), new EmptyParaList(dummyPos), dummyPos));
        StdEnvironment.getFloatDecl = declareStdFunc(StdEnvironment.floatType, "getFloat", new EmptyParaList(dummyPos));
        StdEnvironment.putFloatDecl = declareStdFunc(StdEnvironment.voidType, "putFloat", new ParaList(new ParaDecl(StdEnvironment.floatType, dummyI, dummyPos), new EmptyParaList(dummyPos), dummyPos));
        StdEnvironment.putFloatLnDecl = declareStdFunc(StdEnvironment.voidType, "putFloatLn", new ParaList(new ParaDecl(StdEnvironment.floatType, dummyI, dummyPos), new EmptyParaList(dummyPos), dummyPos));
        StdEnvironment.putBoolDecl = declareStdFunc(StdEnvironment.voidType, "putBool", new ParaList(new ParaDecl(StdEnvironment.booleanType, dummyI, dummyPos), new EmptyParaList(dummyPos), dummyPos));
        StdEnvironment.putBoolLnDecl = declareStdFunc(StdEnvironment.voidType, "putBoolLn", new ParaList(new ParaDecl(StdEnvironment.booleanType, dummyI, dummyPos), new EmptyParaList(dummyPos), dummyPos));
        StdEnvironment.putStringLnDecl = declareStdFunc(StdEnvironment.voidType, "putStringLn", new ParaList(new ParaDecl(StdEnvironment.stringType, dummyI, dummyPos), new EmptyParaList(dummyPos), dummyPos));
        StdEnvironment.putStringDecl = declareStdFunc(StdEnvironment.voidType, "putString", new ParaList(new ParaDecl(StdEnvironment.stringType, dummyI, dummyPos), new EmptyParaList(dummyPos), dummyPos));
        StdEnvironment.putLnDecl = declareStdFunc(StdEnvironment.voidType, "putLn", new EmptyParaList(dummyPos));
    }
}
