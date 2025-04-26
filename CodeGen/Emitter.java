/*
*** Emitter.java 
*
* Sun 30 Mar 2025 14:56:56 AEDT
*
* A new frame object is created for every function just before the
* function is being translated in visitFuncDecl.
*
* All the information about the translation of a function should be
* placed in this Frame object and passed across the AST nodes as the
* 2nd argument of every visitor method in Emitter.java.
*
* All Expression calls are expected to be loads
* if you want a store you must do it manually like for
* visitAssignExpr, visitLocalVarDecl and Global variables
 */
package VC.CodeGen;

import VC.ASTs.*;
import VC.ErrorReporter;
import VC.StdEnvironment;

public final class Emitter implements Visitor {

    private ErrorReporter errorReporter;
    private String inputFilename;
    private String classname;
    private String outputFilename;

    public Emitter(String inputFilename, ErrorReporter reporter) {
        this.inputFilename = inputFilename;
        errorReporter = reporter;

        int i = inputFilename.lastIndexOf('.');
        if (i > 0) {
            classname = inputFilename.substring(0, i);
        } else {
            classname = inputFilename;
        }

    }

    // ast must be a Program node
    public final void gen(AST ast) {
        ast.visit(this, null);
        JVM.dump(classname + ".j");
    }

    // Auxiliary methods for byte code generation
    // The following method appends an instruction directly into the JVM 
    // Code Store. It is called by all other overloaded emit methods.
    private void emit(String s) {
        JVM.append(new Instruction(s));
    }

    private void emit(String s1, String s2) {
        emit(s1 + " " + s2);
    }

    private void emit(String s1, int i) {
        emit(s1 + " " + i);
    }

    private void emit(String s1, float f) {
        emit(s1 + " " + f);
    }

    private void emit(String s1, String s2, int i) {
        emit(s1 + " " + s2 + " " + i);
    }

    private void emit(String s1, String s2, String s3) {
        emit(s1 + " " + s2 + " " + s3);
    }

    private void emitIF_ICMPCOND(String op, Frame frame) {
        String opcode;

        opcode = switch (op) {
            case "i!=" ->
                JVM.IF_ICMPNE;
            case "i==" ->
                JVM.IF_ICMPEQ;
            case "i<" ->
                JVM.IF_ICMPLT;
            case "i<=" ->
                JVM.IF_ICMPLE;
            case "i>" ->
                JVM.IF_ICMPGT;
            default ->
                JVM.IF_ICMPGE;
        }; // if (op.equals("i>="))

        String falseLabel = frame.getNewLabel();
        String nextLabel = frame.getNewLabel();

        emit(opcode, falseLabel);
        frame.pop(2);
        emit("iconst_0");
        emit("goto", nextLabel);
        emit(falseLabel + ":");
        emit(JVM.ICONST_1);
        frame.push();
        emit(nextLabel + ":");
    }

    private void emitFCMP(String op, Frame frame) {
        String opcode;

        opcode = switch (op) {
            case "f!=" ->
                JVM.IFNE;
            case "f==" ->
                JVM.IFEQ;
            case "f<" ->
                JVM.IFLT;
            case "f<=" ->
                JVM.IFLE;
            case "f>" ->
                JVM.IFGT;
            default ->
                JVM.IFGE;
        }; // if (op.equals("f>="))

        String falseLabel = frame.getNewLabel();
        String nextLabel = frame.getNewLabel();

        emit(JVM.FCMPG);
        frame.pop(2);
        emit(opcode, falseLabel);
        emit(JVM.ICONST_0);
        emit("goto", nextLabel);
        emit(falseLabel + ":");
        emit(JVM.ICONST_1);
        frame.push();
        emit(nextLabel + ":");

    }

    private void emitLOAD(int index, Type T) {
        String loadInstruction;
        if (T instanceof IntType || T instanceof BooleanType) {
            loadInstruction = JVM.ILOAD;
        } else if (T instanceof FloatType) {
            loadInstruction = JVM.FLOAD;
        } else if (T instanceof ArrayType) {
            loadInstruction = JVM.ALOAD;
        } else {
            throw new AssertionError("Unsupported type: " + T.getClass().getSimpleName());
        }

        if (index >= 0 && index <= 3) {
            emit(loadInstruction + "_" + index);
        } else {
            emit(loadInstruction, index);
        }
    }

    private void emitGETSTATIC(String T, String I) {
        emit(JVM.GETSTATIC, classname + "/" + I, T);
    }

    private void emitPUTSTATIC(String T, String I) {
        emit(JVM.PUTSTATIC, classname + "/" + I, T);
    }

    private void emitICONST(int value) {
        if (value == -1) {
            emit(JVM.ICONST_M1);
        } else if (value >= 0 && value <= 5) {
            emit(JVM.ICONST + "_" + value);
        } else if (value >= -128 && value <= 127) {
            emit(JVM.BIPUSH, value);
        } else if (value >= -32768 && value <= 32767) {
            emit(JVM.SIPUSH, value);
        } else {
            emit(JVM.LDC, value);
        }
    }

    private void emitFCONST(float value) {
        if (value == 0.0) {
            emit(JVM.FCONST_0);
        } else if (value == 1.0) {
            emit(JVM.FCONST_1);
        } else if (value == 2.0) {
            emit(JVM.FCONST_2);
        } else {
            emit(JVM.LDC, value);
        }
    }

    private void emitBCONST(boolean value) {
        if (value) {
            emit(JVM.ICONST_1);
        } else {
            emit(JVM.ICONST_0);
        }
    }

    private String VCtoJavaType(Type t) {
        boolean isArray = t instanceof ArrayType;
        Type baseType = isArray ? ((ArrayType) t).T : t;
        String suffix = isArray ? "[" : "";

        if (baseType.equals(StdEnvironment.booleanType)) {
            return suffix + "Z";
        } else if (baseType.equals(StdEnvironment.intType)) {
            return suffix + "I";
        } else if (baseType.equals(StdEnvironment.floatType)) {
            return suffix + "F";
        } else if (baseType.equals(StdEnvironment.voidType)) {
            return suffix + "V";
        } else {
            throw new Error("VCtoJavaType unexpected " + (isArray ? "array " : "") + "type");
        }
    }

    private void emitArrayStoreAtIndex(Type T) {
        // consumes value, index, array_pointer
        if (T.isIntType()) {
            emit(JVM.IASTORE);
        } else if (T.isFloatType()) {
            emit(JVM.FASTORE);
        } else if (T.isBooleanType()) {
            emit(JVM.BASTORE);
        } else {
            throw new Error("unknown array T for visitArrayExprList");
        }
    }

    private void emitSTORE(AST decl, String storeInstruction) {
        int index;
        if (decl instanceof ParaDecl paraDecl) {
            index = paraDecl.index;
        } else if (decl instanceof LocalVarDecl localVarDecl) {
            index = localVarDecl.index;
        } else {
            throw new Error("Couldn't access index of ast: " + decl);
        }

        if (index >= 0 && index <= 3) {
            emit(storeInstruction + "_" + index);
        } else {
            emit(storeInstruction, index);
        }
    }
    
    private void emitStoreVar(Ident I, Type T) {
        // consumes value
        if (I.decl instanceof GlobalVarDecl globalVarDecl) {
            emitPUTSTATIC(VCtoJavaType(globalVarDecl.T), globalVarDecl.I.spelling);
        } else {
            if (T instanceof IntType || T instanceof BooleanType) {
                emitSTORE(I.decl, JVM.ISTORE);
            } else if (T instanceof FloatType) {
                emitSTORE(I.decl, JVM.FSTORE);
            } else if (T instanceof ArrayType) {
                emitSTORE(I.decl, JVM.ASTORE);
            } else {
                throw new Error("unexpected assignment type....");
            }
        }
    }

    private void emitNewArray(ArrayType arrayType, Frame frame) {
        IntExpr intExpr = (IntExpr) arrayType.E;
        emitICONST(Integer.parseInt(intExpr.IL.spelling)); // size
        frame.push();

        // size + new_array -> array pointer // stack size unchanged
        if (arrayType.T.isIntType()) {
            emit(JVM.NEWARRAY, "int");
        } else if (arrayType.T.isFloatType()) {
            emit(JVM.NEWARRAY, "float");
        } else if (arrayType.T.isBooleanType()) {
            emit(JVM.NEWARRAY, "boolean");
        } else {
            throw new Error("unsupported array type in local var decl....");
        }
    }

    // ===================================================================== //
    /////////////////////////////// Program ///////////////////////////////////
    // ===================================================================== //
    @Override
    public Object visitProgram(Program ast, Object o) {

        /* This method works for scalar variables only. You need to add code
     * to handle all array-related declarations and initialisations.
         */
        // Generates the default constructor initialiser 
        emit(JVM.CLASS, "public", classname);
        emit(JVM.SUPER, "java/lang/Object");

        emit("");

        // Three subpasses:
        // (1) Generate .field definition statements since
        //     these are required to appear before method definitions.
        //
        // This can also be done using a separate visitor.
        List list = ast.FL;
        while (!list.isEmpty()) {
            DeclList dlAST = (DeclList) list;
            if (dlAST.D instanceof GlobalVarDecl vAST) {
                emit(JVM.STATIC_FIELD, vAST.I.spelling, VCtoJavaType(vAST.T));
            }
            list = dlAST.DL;
        }

        emit("");

        // (2) Generate <clinit> for global variables (assumed to be static)
        //
        // This can also be done using a separate visitor.
        emit("; standard class static initializer ");
        emit(JVM.METHOD_START, "static <clinit>()V");
        emit("");

        // create a Frame for <clinit>
        Frame frame = new Frame(false);

        list = ast.FL;
        while (!list.isEmpty()) {
            DeclList dlAST = (DeclList) list;
            if (dlAST.D instanceof GlobalVarDecl vAST) {
                if (vAST.T instanceof ArrayType arrayType) {
                    emitNewArray(arrayType, frame);
                    vAST.E.visit(this, new ArrayExprListAttrs(frame, 0, arrayType.T));
                } else if (!vAST.E.isEmptyExpr()) {
                    vAST.E.visit(this, frame);
                } else {
                    if (vAST.T.equals(StdEnvironment.floatType)) {
                        emit(JVM.FCONST_0);
                    } else if (vAST.T.equals(StdEnvironment.intType) || vAST.T.equals(StdEnvironment.booleanType)) {
                        emit(JVM.ICONST_0);
                    } else {
                        throw new Error("unsupported global variable decl");
                    }

                    frame.push();
                }
                emitPUTSTATIC(VCtoJavaType(vAST.T), vAST.I.spelling);
                frame.pop();
            }
            list = dlAST.DL;
        }

        emit("");
        emit("; set limits used by this method");

        emit(JVM.LIMIT, "locals", frame.getNewIndex());
        emit(JVM.LIMIT, "stack", frame.getMaximumStackSize());

        emit(JVM.RETURN);
        emit(JVM.METHOD_END, "method");

        emit("");

        // (3) Generate Java bytecode for the VC program
        emit("; standard constructor initializer ");
        emit(JVM.METHOD_START, "public <init>()V");
        emit(JVM.LIMIT, "stack 1");
        emit(JVM.LIMIT, "locals 1");
        emit(JVM.ALOAD_0);
        emit(JVM.INVOKESPECIAL, "java/lang/Object/<init>()V");
        emit(JVM.RETURN);
        emit(JVM.METHOD_END, "method");

        return ast.FL.visit(this, o);
    }

    // ===================================================================== //
    /////////////////////////// Declaration List //////////////////////////////
    // ===================================================================== //
    @Override
    public Object visitDeclList(DeclList ast, Object o) {
        ast.D.visit(this, o);
        ast.DL.visit(this, o);
        return null;
    }

    @Override
    public Object visitEmptyDeclList(EmptyDeclList ast, Object o) {
        return null;
    }

    // ===================================================================== //
    ///////////////////////// Function Declaration ////////////////////////////
    // ===================================================================== //
    @Override
    public Object visitFuncDecl(FuncDecl ast, Object o) {

        Frame frame;

        if (ast.I.spelling.equals("main")) {

            frame = new Frame(true);

            // Assume that main has one String parameter and reserve 0 for it
            frame.getNewIndex();

            emit(JVM.METHOD_START, "public static main([Ljava/lang/String;)V");
            // Assume implicitly that
            //      classname vc$; 
            // appears before all local variable declarations.
            // (1) Reserve 1 for this object reference.

            frame.getNewIndex();

        } else {

            frame = new Frame(false);

            // all other programmer-defined functions are treated as if
            // they were instance methods
            frame.getNewIndex(); // reserve 0 for "this"

            String retType = VCtoJavaType(ast.T);

            // The types of the parameters of the called function are not
            // directly available in the FuncDecl node but can be gathered
            // by traversing its field PL.
            StringBuffer argsTypes = new StringBuffer("");
            List fpl = ast.PL;
            while (!fpl.isEmpty()) {
                argsTypes.append(VCtoJavaType(((ParaList) fpl).P.T));
                fpl = ((ParaList) fpl).PL;
            }

            emit(JVM.METHOD_START, ast.I.spelling + "(" + argsTypes + ")" + retType);
        }

        ast.S.visit(this, frame);

        // JVM requires an explicit return in every method. 
        // In VC, a function returning void may not contain a return, and
        // a function returning int or float is not guaranteed to contain
        // a return. Therefore, we add one at the end just to be sure.
        if (ast.T.equals(StdEnvironment.voidType)) {
            emit("");
            emit("; return may not be present in a VC function returning void");
            emit("; The following return inserted by the VC compiler");
            emit(JVM.RETURN);
        } else if (ast.I.spelling.equals("main")) {
            // In case VC's main does not have a return itself
            emit(JVM.RETURN);
        } else {
            emit(JVM.NOP);
        }

        emit("");
        emit("; set limits used by this method");

        emit(JVM.LIMIT, "locals", frame.getNewIndex());
        emit(JVM.LIMIT, "stack", frame.getMaximumStackSize());

        emit(".end method");

        return null;
    }

    // ===================================================================== //
    ///////////////////////// Variable Declarations ///////////////////////////
    // ===================================================================== //
    @Override
    public Object visitGlobalVarDecl(GlobalVarDecl ast, Object o) {
        // nothing to be done
        return null;
    }

    @Override
    public Object visitLocalVarDecl(LocalVarDecl ast, Object o) {

        /* You need to add code to handle arrays */
        Frame frame = (Frame) o;
        ast.index = frame.getNewIndex();
        String T = VCtoJavaType(ast.T);

        emit(JVM.VAR + " " + ast.index + " is " + ast.I.spelling + " " + T + " from " + (String) frame.scopeStart.peek() + " to " + (String) frame.scopeEnd.peek());

        if (ast.T instanceof ArrayType arrayType) {
            emitNewArray(arrayType, frame); // array pointer
            ast.E.visit(this, new ArrayExprListAttrs(frame, 0, arrayType.T)); // array init expr should consume all its thingos
            emitStoreVar(ast.I, ast.T);
            frame.pop();
        } else if (!ast.E.isEmptyExpr()) {
            ast.E.visit(this, o); // variable value
            emitStoreVar(ast.I, ast.T);
            frame.pop();
        } // else don't even store it - problematic but whateva

        return null;
    }

    // ===================================================================== //
    /////////////////////////// Array Initializer /////////////////////////////
    // ===================================================================== //
    @Override
    public Object visitArrayInitExpr(ArrayInitExpr ast, Object o) {
        ast.IL.visit(this, o);
        return null;
    }

    @Override
    public Object visitArrayExprList(ArrayExprList ast, Object o) {
        if (!(o instanceof ArrayExprListAttrs attrs)) {
            throw new Error("unexpected visitedArrayExprList object");
        }

        emit(JVM.DUP);
        attrs.frame.push();

        emitICONST(attrs.index());
        attrs.frame.push();

        ast.E.visit(this, attrs.frame); // has a push probs.

        emitArrayStoreAtIndex(attrs.type());

        attrs.frame.pop(3);

        ast.EL.visit(this, new ArrayExprListAttrs(attrs.frame, attrs.index() + 1, attrs.type()));

        return null;
    }

    @Override
    public Object visitEmptyArrayExprList(EmptyArrayExprList ast, Object o) {
        return null;
    }

    // ===================================================================== //
    /////////////////////////////// Parameters ////////////////////////////////
    // ===================================================================== //
    @Override
    public Object visitParaList(ParaList ast, Object o) {
        ast.P.visit(this, o);
        ast.PL.visit(this, o);
        return null;
    }

    @Override
    public Object visitParaDecl(ParaDecl ast, Object o) {

        /* You need to add code to handle arrays */
        Frame frame = (Frame) o;
        ast.index = frame.getNewIndex();
        String T = VCtoJavaType(ast.T);

        emit(JVM.VAR + " " + ast.index + " is " + ast.I.spelling + " " + T + " from " + (String) frame.scopeStart.peek() + " to " + (String) frame.scopeEnd.peek());
        return null;
    }

    @Override
    public Object visitEmptyParaList(EmptyParaList ast, Object o) {
        return null;
    }

    // ===================================================================== //
    ////////////////////////////////// Types //////////////////////////////////
    // ===================================================================== //
    @Override
    public Object visitIntType(IntType ast, Object o) {
        return null;
    }

    @Override
    public Object visitFloatType(FloatType ast, Object o) {
        return null;
    }

    @Override
    public Object visitBooleanType(BooleanType ast, Object o) {
        return null;
    }

    @Override
    public Object visitArrayType(ArrayType ast, Object o) {
        return null;
    }

    @Override
    public Object visitStringType(StringType ast, Object o) {
        return null;
    }

    @Override
    public Object visitVoidType(VoidType ast, Object o) {
        return null;
    }

    @Override
    public Object visitErrorType(ErrorType ast, Object o) {
        return null;
    }

    // ===================================================================== //
    /////////////////// Literals, Identifiers and Operators ///////////////////
    // ===================================================================== //
    @Override
    public Object visitIdent(Ident ast, Object o) {
        return null;
    }

    @Override
    public Object visitIntLiteral(IntLiteral ast, Object o) {
        Frame frame = (Frame) o;
        emitICONST(Integer.parseInt(ast.spelling));
        frame.push();
        return null;
    }

    @Override
    public Object visitFloatLiteral(FloatLiteral ast, Object o) {
        Frame frame = (Frame) o;
        emitFCONST(Float.parseFloat(ast.spelling));
        frame.push();
        return null;
    }

    @Override
    public Object visitBooleanLiteral(BooleanLiteral ast, Object o) {
        Frame frame = (Frame) o;
        emitBCONST(ast.spelling.equals("true"));
        frame.push();
        return null;
    }

    @Override
    public Object visitStringLiteral(StringLiteral ast, Object o) {
        Frame frame = (Frame) o;
        emit(JVM.LDC, "\"" + ast.spelling.replace("\"", "\\\"") + "\"");
        frame.push();
        return null;
    }

    @Override
    public Object visitOperator(Operator ast, Object o) {
        return null;
    }

    // ===================================================================== //
    /////////////////////////////// Simple Var ////////////////////////////////
    // ===================================================================== //
    @Override
    public Object visitSimpleVar(SimpleVar ast, Object o) {
        Frame frame = (Frame) o;
        Decl decl = (Decl) ast.I.decl;

        if (decl instanceof ParaDecl paraDecl) {
            emitLOAD(paraDecl.index, paraDecl.T);
        } else if (decl instanceof LocalVarDecl localVarDecl) {
            emitLOAD(localVarDecl.index, localVarDecl.T);
        } else if (decl instanceof GlobalVarDecl globalVarDecl) {
            emitGETSTATIC(VCtoJavaType(globalVarDecl.T), globalVarDecl.I.spelling);
        } else {
            throw new Error("unexpected decl type: " + decl);
        }

        frame.push();

        return null;
    }

    // ===================================================================== //
    ///////////////////////////// Statement List //////////////////////////////
    // ===================================================================== //
    @Override
    public Object visitStmtList(StmtList ast, Object o) {
        ast.S.visit(this, o);
        ast.SL.visit(this, o);
        return null;
    }

    @Override
    public Object visitEmptyStmtList(EmptyStmtList ast, Object o) {
        return null;
    }

    // ===================================================================== //
    /////////////////////////////// Statements ////////////////////////////////
    // ===================================================================== //
    @Override
    public Object visitCompoundStmt(CompoundStmt ast, Object o) {
        Frame frame = (Frame) o;

        String scopeStart = frame.getNewLabel();
        String scopeEnd = frame.getNewLabel();
        frame.scopeStart.push(scopeStart);
        frame.scopeEnd.push(scopeEnd);

        emit(scopeStart + ":");
        if (ast.parent instanceof FuncDecl funcDecl) {
            if (funcDecl.I.spelling.equals("main")) {
                emit(JVM.VAR, "0 is argv [Ljava/lang/String; from " + (String) frame.scopeStart.peek() + " to " + (String) frame.scopeEnd.peek());
                emit(JVM.VAR, "1 is vc$ L" + classname + "; from " + (String) frame.scopeStart.peek() + " to " + (String) frame.scopeEnd.peek());
                // Generate code for the initialiser vc$ = new classname();
                emit(JVM.NEW, classname);
                emit(JVM.DUP);
                frame.push(2);
                emit("invokenonvirtual", classname + "/<init>()V");
                frame.pop();
                emit(JVM.ASTORE_1);
                frame.pop();
            } else {
                emit(JVM.VAR, "0 is this L" + classname + "; from " + (String) frame.scopeStart.peek() + " to " + (String) frame.scopeEnd.peek());
                funcDecl.PL.visit(this, o);
            }
        }

        ast.DL.visit(this, o);
        ast.SL.visit(this, o);
        emit(scopeEnd + ":");

        frame.scopeStart.pop();
        frame.scopeEnd.pop();
        return null;
    }

    @Override
    public Object visitEmptyCompStmt(EmptyCompStmt ast, Object o) {
        return null;
    }

    @Override
    public Object visitReturnStmt(ReturnStmt ast, Object o) {
        Frame frame = (Frame) o;

        /*
        int main() { return 0; } must be interpretted as 
        public static void main(String[] args) { return ; }
        Therefore, "return expr", if present in the main of a VC program
        must be translated into a RETURN rather than IRETURN instruction.
         */
        if (frame.isMain()) {
            emit(JVM.RETURN);
            return null;
        }

        /*  Your other code goes here for handling return <Expr>. */
        ast.E.visit(this, o);
        if (ast.E.type instanceof IntType || ast.E.type instanceof BooleanType) {
            emit(JVM.IRETURN);
            frame.pop();
        } else if (ast.E.type instanceof FloatType) {
            emit(JVM.FRETURN);
            frame.pop();
        } else if (ast.E.type.isVoidType()) {
            emit(JVM.RETURN);
        } else {
            throw new Error("Bruh idk how to return this..");
        }

        return null;
    }

    @Override
    public Object visitEmptyStmt(EmptyStmt ast, Object o) {
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt ast, Object o) {
        Frame frame = (Frame) o;

        if (ast.S2.isEmptyStmt() || ast.S2.isEmptyCompStmt()) {
            String L1 = frame.getNewLabel();

            ast.E.visit(this, o);
            emit(JVM.IFEQ, L1);
            frame.pop(); // pop condition off the stack after checking it.

            ast.S1.visit(this, o);
            emit(L1 + ":");
        } else {
            String L1 = frame.getNewLabel();
            String L2 = frame.getNewLabel();

            ast.E.visit(this, o);
            emit(JVM.IFEQ, L1);

            ast.S1.visit(this, o);
            emit(JVM.GOTO, L2);
            emit(L1 + ":");
            ast.S2.visit(this, o);
            emit(L2 + ":");
        }

        return null;
    }

    @Override
    public Object visitWhileStmt(WhileStmt ast, Object o) {
        Frame frame = (Frame) o;

        String L1 = frame.getNewLabel();
        String L2 = frame.getNewLabel();

        frame.conStack.push(L1);
        frame.brkStack.push(L2);

        emit(L1 + ":");
        ast.E.visit(this, o);
        emit(JVM.IFEQ, L2);
        frame.pop();
        ast.S.visit(this, o);
        emit(JVM.GOTO, L1);
        emit(L2 + ":");

        frame.conStack.pop();
        frame.brkStack.pop();

        return null;
    }
    
    @Override
    public Object visitForStmt(ForStmt ast, Object o) {
        Frame frame = (Frame) o;

        String L1 = frame.getNewLabel();
        String L2 = frame.getNewLabel();
        String L3 = frame.getNewLabel();

        frame.conStack.push(L2);
        frame.brkStack.push(L3);

        int before = frame.getCurStackSize();
        ast.E1.visit(this, o);
        int after = frame.getCurStackSize();
        if (after > before) {
            frame.pop();
            emit(JVM.POP);
        }

        emit(L1 + ":");
        if (!ast.E2.isEmptyExpr()) { // shouldn't ever be EmptyExpr because ast.E2 is decorated with true
            ast.E2.visit(this, o); // expects a dup if an assignment
            emit(JVM.IFEQ, L3);
            frame.pop();
        }

        ast.S.visit(this, o);

        emit(L2 + ":");

        before = frame.getCurStackSize();
        ast.E3.visit(this, o);
        after = frame.getCurStackSize();
        if (after > before) {
            frame.pop();
            emit(JVM.POP);
        }

        emit(JVM.GOTO, L1);
        emit(L3 + ":");

        frame.conStack.pop();
        frame.brkStack.pop();

        return null;
    }

    @Override
    public Object visitBreakStmt(BreakStmt ast, Object o) {
        Frame frame = (Frame) o;
        emit(JVM.GOTO, frame.brkStack.peek());
        return null;
    }

    @Override
    public Object visitContinueStmt(ContinueStmt ast, Object o) {
        Frame frame = (Frame) o;
        emit(JVM.GOTO, frame.conStack.peek());
        return null;
    }

    @Override
    public Object visitExprStmt(ExprStmt ast, Object o) {
        Frame frame = (Frame) o;

        int before = frame.getCurStackSize();
        ast.E.visit(this, o);
        int after = frame.getCurStackSize();

        if (after > before) {
            System.out.println("after: " + after + " > before: " + before);
            emit(JVM.POP);
            frame.pop();
        }

        return null;
    }

    // ===================================================================== //
    ////////////////////////////// Function Call //////////////////////////////
    // ===================================================================== //
    @Override
    public Object visitCallExpr(CallExpr ast, Object o) {
        Frame frame = (Frame) o;
        String fname = ast.I.spelling;

        switch (fname) {
            case "getInt" -> {
                ast.AL.visit(this, o); // push args (if any) into the op stack
                emit("invokestatic VC/lang/System.getInt()I");
                frame.push();
            }
            case "putInt" -> {
                ast.AL.visit(this, o); // push args (if any) into the op stack
                emit("invokestatic VC/lang/System.putInt(I)V");
                frame.pop();
            }
            case "putIntLn" -> {
                ast.AL.visit(this, o); // push args (if any) into the op stack
                emit("invokestatic VC/lang/System/putIntLn(I)V");
                frame.pop();
            }
            case "getFloat" -> {
                ast.AL.visit(this, o); // push args (if any) into the op stack
                emit("invokestatic VC/lang/System/getFloat()F");
                frame.push();
            }
            case "putFloat" -> {
                ast.AL.visit(this, o); // push args (if any) into the op stack
                emit("invokestatic VC/lang/System/putFloat(F)V");
                frame.pop();
            }
            case "putFloatLn" -> {
                ast.AL.visit(this, o); // push args (if any) into the op stack
                emit("invokestatic VC/lang/System/putFloatLn(F)V");
                frame.pop();
            }
            case "putBool" -> {
                ast.AL.visit(this, o); // push args (if any) into the op stack
                emit("invokestatic VC/lang/System/putBool(Z)V");
                frame.pop();
            }
            case "putBoolLn" -> {
                ast.AL.visit(this, o); // push args (if any) into the op stack
                emit("invokestatic VC/lang/System/putBoolLn(Z)V");
                frame.pop();
            }
            case "putString" -> {
                ast.AL.visit(this, o);
                emit(JVM.INVOKESTATIC, "VC/lang/System/putString(Ljava/lang/String;)V");
                frame.pop();
            }
            case "putStringLn" -> {
                ast.AL.visit(this, o);
                emit(JVM.INVOKESTATIC, "VC/lang/System/putStringLn(Ljava/lang/String;)V");
                frame.pop();
            }
            case "putLn" -> {
                ast.AL.visit(this, o); // push args (if any) into the op stack
                emit("invokestatic VC/lang/System/putLn()V");
            }
            default -> {
                // programmer-defined functions

                FuncDecl fAST = (FuncDecl) ast.I.decl;
                // all functions except main are assumed to be instance methods
                if (frame.isMain()) {
                    emit("aload_1"); // vc.funcname(...)
                } else {
                    emit("aload_0"); // this.funcname(...)
                }
                frame.push();
                ast.AL.visit(this, o);
                String retType = VCtoJavaType(fAST.T);
                // The types of the parameters of the called function are not
                // directly available in the FuncDecl node but can be gathered
                // by traversing its field PL.
                StringBuffer argsTypes = new StringBuffer("");
                List fpl = fAST.PL;
                int fpl_length = 1;
                while (!fpl.isEmpty()) {
                    argsTypes.append(VCtoJavaType(((ParaList) fpl).P.T));
                    fpl_length += 1;
                    fpl = ((ParaList) fpl).PL;
                }
                emit("invokevirtual", classname + "/" + fname + "(" + argsTypes + ")" + retType);
                frame.pop(fpl_length);
                if (!retType.equals("V")) {
                    frame.push();
                }
            }
        }
        return null;
    }

    // ===================================================================== //
    //////////////////////////////// Arguments ////////////////////////////////
    // ===================================================================== //
    @Override
    public Object visitArgList(ArgList ast, Object o) {
        ast.A.visit(this, o);
        ast.AL.visit(this, o);
        return null;
    }

    @Override
    public Object visitArg(Arg ast, Object o) {
        ast.E.visit(this, o);
        return null;
    }

    @Override
    public Object visitEmptyArgList(EmptyArgList ast, Object o) {
        return null;
    }

    // ===================================================================== //
    /////////////////////////////// Expressions ///////////////////////////////
    // ===================================================================== //
    @Override
    public Object visitEmptyExpr(EmptyExpr ast, Object o) {
        return null;
    }

    @Override
    public Object visitIntExpr(IntExpr ast, Object o) {
        ast.IL.visit(this, o);
        return null;
    }

    @Override
    public Object visitFloatExpr(FloatExpr ast, Object o) {
        ast.FL.visit(this, o);
        return null;
    }

    @Override
    public Object visitBooleanExpr(BooleanExpr ast, Object o) {
        ast.BL.visit(this, o);
        return null;
    }

    @Override
    public Object visitStringExpr(StringExpr ast, Object o) {
        ast.SL.visit(this, o);
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr ast, Object o) {
        Frame frame = (Frame) o;
        String op = ast.O.spelling;

        ast.E.visit(this, o);

        switch (op) {
            case "i!" -> {
                // if its 0, make it 1 else make it 0 (xor kinda...)
                String L1 = frame.getNewLabel();
                String L2 = frame.getNewLabel();
                emit(JVM.IFEQ, L1);
                emit(JVM.ICONST_0);
                emit(JVM.GOTO, L2);
                emit(L1 + ":");
                emit(JVM.ICONST_1);
                emit(L2 + ":");
            }
            case "i+", "f+" -> {
                // do i even do anything?
            }
            case "i-" -> {
                emit(JVM.INEG);
            }
            case "f-" -> {
                emit(JVM.FNEG);
            }
            case "i2f" -> {
                emit(JVM.I2F);
            }
            default ->
                throw new Error("bad unary operator?");
        }

        return null;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr ast, Object o) {
        Frame frame = (Frame) o;
        String op = ast.O.spelling;

        if (op.equals("i&&")) {
            String L1 = frame.getNewLabel();
            String L2 = frame.getNewLabel();
            ast.E1.visit(this, o);
            emit(JVM.IFEQ, L1); // if 0 then entire expr is false
            frame.pop();
            ast.E2.visit(this, o); // if we get here the result of E2 is result of the binaryExpr
            frame.pop(); // just frame.pop cuz we have a push later (we just leave E2 on stack)
            emit(JVM.GOTO, L2);
            emit(L1 + ":");
            emit(JVM.ICONST_0); // short circuit false
            emit(L2 + ":");
            frame.push();
            return null;
        } else if (op.equals("i||")) {
            String L1 = frame.getNewLabel();
            String L2 = frame.getNewLabel();
            ast.E1.visit(this, o);
            emit(JVM.IFNE, L1); // if not 0 then entire expr is true
            frame.pop();
            ast.E2.visit(this, o);  // if we get here the result of E2 is result of the binaryExpr
            frame.pop(); // just frame.pop cuz we have a push later (we just leave E2 on stack)
            emit(JVM.GOTO, L2);
            emit(L1 + ":");
            emit(JVM.ICONST_1); // short circuit to true
            emit(L2 + ":");
            frame.push();
            return null;
        }

        ast.E1.visit(this, o);
        ast.E2.visit(this, o);

        switch (op) {
            case "i!=", "i==", "i<=", "i<", "i>=", "i>" -> {
                emitIF_ICMPCOND(op, frame);
            }
            case "f!=", "f==", "f<=", "f<", "f>=", "f>" -> {
                emitFCMP(op, frame);
            }
            case "i+" -> {
                emit(JVM.IADD);
                frame.pop();
            }
            case "i-" -> {
                emit(JVM.ISUB);
                frame.pop();
            }
            case "i*" -> {
                emit(JVM.IMUL);
                frame.pop();
            }
            case "i/" -> {
                emit(JVM.IDIV);
                frame.pop();
            }
            case "f+" -> {
                emit(JVM.FADD);
                frame.pop();
            }
            case "f-" -> {
                emit(JVM.FSUB);
                frame.pop();
            }
            case "f*" -> {
                emit(JVM.FMUL);
                frame.pop();
            }
            case "f/" -> {
                emit(JVM.FDIV);
                frame.pop();
            }
            default ->
                throw new Error("unsupported operation");
        }

        return null;
    }

    private record ArrayExprListAttrs(Frame frame, int index, Type type) {

    }

    @Override
    public Object visitArrayExpr(ArrayExpr ast, Object o) {
        Frame frame = (Frame) o;

        // array
        ast.V.visit(this, o);

        // index
        ast.E.visit(this, o);

        // load 
        if (ast.type.isIntType()) {
            emit(JVM.IALOAD);
        } else if (ast.type.isFloatType()) {
            emit(JVM.FALOAD);
        } else if (ast.type.isBooleanType()) {
            emit(JVM.BALOAD);
        } else {
            throw new Error("unknown array type for visitArrayExpr: " + ast.type);
        }

        frame.pop(); // array, index ---after load---> value

        return null;
    }

    @Override
    public Object visitVarExpr(VarExpr ast, Object o) {
        if (ast.parent instanceof AssignExpr assignExpr && assignExpr.E1.equals(ast)) {
            throw new Error("Trying to load from [[LHS]] of assign");
        }

        ast.V.visit(this, o); // loads the variable

        return null;
    }

    private boolean requiresDup(AssignExpr ast) {
        return ast.parent instanceof Expr
                || ast.parent instanceof ArrayExprList
                || ast.parent instanceof IfStmt
                || ast.parent instanceof WhileStmt
                || ast.parent instanceof ForStmt forStmt && forStmt.E2.equals(ast);
    }

    @Override
    public Object visitAssignExpr(AssignExpr ast, Object o) {
        Frame frame = (Frame) o;

        if (ast.E1 instanceof ArrayExpr arrayExpr) {
            // [[LHS]]
            arrayExpr.V.visit(this, o); // array_pointer
            arrayExpr.E.visit(this, o); // index

            // [[RHS]]
            ast.E2.visit(this, o); // value

            if (requiresDup(ast)) {
                emit(JVM.DUP_X2);
                frame.push();
            }

            // store instruction
            emitArrayStoreAtIndex(arrayExpr.type);
            frame.pop(3); // consume value index and array_pointer

        } else if (ast.E1 instanceof VarExpr varExpr && varExpr.V instanceof SimpleVar simpleVar) {
            // [[LHS]] (no need for simple var)
            // [[RHS]]
            ast.E2.visit(this, o);

            if (requiresDup(ast)) {
                emit(JVM.DUP);
                frame.push();
            }

            // store instruction
            emitStoreVar(simpleVar.I, simpleVar.type);
            frame.pop();
        } else {
            throw new Error("Assigning to wha???: " + ast.E1.toString());
        }

        return null;
    }
}
