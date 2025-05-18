package ricelang.VC.TreeMermaid;

import ricelang.VC.ASTs.*;

public class Mermaid implements Visitor {

    private int index;
    private StringBuilder output;

    public Mermaid() {
        index = 0;
        output = new StringBuilder();
    }

    private int getIndex() {
        index++;
        return index;
    }

    public final void print(AST ast) {
        ast.visit(this, null);
        System.out.print(output.toString());
    }

    public final String toString(AST ast) {
        ast.visit(this, null);
        return output.toString();
    }

    @Override
    public Object visitProgram(Program ast, Object o) {
        output.append("flowchart TD\n");

        int index = getIndex();
        output.append(index + "[Program]\n");
        ast.FL.visit(this, index);
        return null;
    }

    @Override
    public Object visitDeclList(DeclList ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[DeclList]\n");
        ast.D.visit(this, index);
        ast.DL.visit(this, index);
        return null;
    }

    @Override
    public Object visitEmptyDeclList(EmptyDeclList ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[EmptyDeclList]\n");
        return null;
    }

    @Override
    public Object visitEmptyStmtList(EmptyStmtList ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[EmptyStmtList]\n");
        return null;
    }

    @Override
    public Object visitEmptyArrayExprList(EmptyArrayExprList ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[EmptyArrayExprList]\n");
        return null;
    }

    @Override
    public Object visitEmptyParaList(EmptyParaList ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[EmptyParaList]\n");
        return null;
    }

    @Override
    public Object visitEmptyArgList(EmptyArgList ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[EmptyArgList]\n");
        return null;
    }

    @Override
    public Object visitFuncDecl(FuncDecl ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[FuncDecl]\n");
        ast.T.visit(this, index);
        ast.I.visit(this, index);
        ast.PL.visit(this, index);
        ast.S.visit(this, index);
        return null;
    }

    @Override
    public Object visitGlobalVarDecl(GlobalVarDecl ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[GlobalVarDecl]\n");
        ast.T.visit(this, index);
        ast.I.visit(this, index);
        ast.E.visit(this, index);
        return null;
    }

    @Override
    public Object visitLocalVarDecl(LocalVarDecl ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[LocalVarDecl]\n");
        ast.T.visit(this, index);
        ast.I.visit(this, index);
        ast.E.visit(this, index);
        return null;
    }

    @Override
    public Object visitStmtList(StmtList ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[StmtList]\n");
        ast.S.visit(this, index);
        ast.SL.visit(this, index);
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[IfStmt]\n");
        ast.E.visit(this, index);
        ast.S1.visit(this, index);
        ast.S2.visit(this, index);
        return null;
    }

    @Override
    public Object visitWhileStmt(WhileStmt ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[WhileStmt]\n");
        ast.E.visit(this, index);
        ast.S.visit(this, index);
        return null;
    }

    @Override
    public Object visitForStmt(ForStmt ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[ForStmt]\n");
        ast.E1.visit(this, index);
        ast.E2.visit(this, index);
        ast.E3.visit(this, index);
        ast.S.visit(this, index);
        return null;
    }

    @Override
    public Object visitBreakStmt(BreakStmt ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[Break]\n");
        return null;
    }

    @Override
    public Object visitContinueStmt(ContinueStmt ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[Continue]\n");
        return null;
    }

    @Override
    public Object visitReturnStmt(ReturnStmt ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[byebye]\n");
        ast.E.visit(this, index);
        return null;
    }

    @Override
    public Object visitCompoundStmt(CompoundStmt ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[CompoundStmt]\n");
        ast.DL.visit(this, index);
        ast.SL.visit(this, index);
        return null;
    }

    @Override
    public Object visitExprStmt(ExprStmt ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[ExprStmt]\n");
        ast.E.visit(this, index);
        return null;
    }

    @Override
    public Object visitEmptyCompStmt(EmptyCompStmt ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[EmptyCompStmt]\n");
        return null;
    }

    @Override
    public Object visitEmptyStmt(EmptyStmt ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[EmptyStmt]\n");
        return null;
    }

    @Override
    public Object visitIntExpr(IntExpr ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[IntExpr]\n");
        ast.IL.visit(this, index);
        return null;
    }

    @Override
    public Object visitFloatExpr(FloatExpr ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[FloatExpr]\n");
        ast.FL.visit(this, index);
        return null;
    }

    @Override
    public Object visitBooleanExpr(BooleanExpr ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[BooleanExpr]\n");
        ast.BL.visit(this, index);
        return null;
    }

    @Override
    public Object visitStringExpr(StringExpr ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[StringExpr]\n");
        ast.SL.visit(this, index);
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[UnaryExpr]\n");
        ast.O.visit(this, index);
        ast.E.visit(this, index);
        return null;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[BinaryExpr]\n");
        ast.E1.visit(this, index);
        ast.O.visit(this, index);
        ast.E2.visit(this, index);
        return null;
    }

    @Override
    public Object visitArrayInitExpr(ArrayInitExpr ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[ArrayInitExpr]\n");
        ast.IL.visit(this, index);
        return null;
    }

    @Override
    public Object visitArrayExprList(ArrayExprList ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[ArrayExprList]\n");
        ast.E.visit(this, index);
        ast.EL.visit(this, index);
        return null;
    }

    @Override
    public Object visitArrayExpr(ArrayExpr ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[ArrayExpr]\n");
        ast.V.visit(this, index);
        ast.E.visit(this, index);
        return null;
    }

    @Override
    public Object visitVarExpr(VarExpr ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[VarExpr]\n");
        ast.V.visit(this, index);
        return null;
    }

    @Override
    public Object visitCallExpr(CallExpr ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[CallExpr]\n");
        ast.I.visit(this, index);
        ast.AL.visit(this, index);
        return null;
    }

    @Override
    public Object visitAssignExpr(AssignExpr ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[AssignExpr]\n");
        ast.E1.visit(this, index);
        ast.E2.visit(this, index);
        return null;
    }

    @Override
    public Object visitEmptyExpr(EmptyExpr ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[EmptyExpr]\n");
        return null;
    }

    @Override
    public Object visitIntLiteral(IntLiteral ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[" + ast.spelling + "]\n");
        return null;
    }

    @Override
    public Object visitFloatLiteral(FloatLiteral ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[" + ast.spelling + "]\n");
        return null;
    }

    @Override
    public Object visitBooleanLiteral(BooleanLiteral ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[" + ast.spelling + "]\n");
        return null;
    }

    @Override
    public Object visitStringLiteral(StringLiteral ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[\"" + ast.spelling + "\"]\n");
        return null;
    }

    @Override
    public Object visitIdent(Ident ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[" + ast.spelling + "]\n");
        return null;
    }

    @Override
    public Object visitOperator(Operator ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[\"" + ast.spelling + "\"]\n");
        return null;
    }

    @Override
    public Object visitParaList(ParaList ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[ParaList]\n");
        ast.P.visit(this, index);
        ast.PL.visit(this, index);
        return null;
    }

    @Override
    public Object visitParaDecl(ParaDecl ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[ParaDecl]\n");
        ast.T.visit(this, index);
        ast.I.visit(this, index);
        return null;
    }

    @Override
    public Object visitArgList(ArgList ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[ArgList]\n");
        ast.A.visit(this, index);
        ast.AL.visit(this, index);
        return null;
    }

    @Override
    public Object visitArg(Arg ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[Arg]\n");
        ast.E.visit(this, index);
        return null;
    }

    @Override
    public Object visitVoidType(VoidType ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[void]\n");
        return null;
    }

    @Override
    public Object visitBooleanType(BooleanType ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[boolean]\n");
        return null;
    }

    @Override
    public Object visitIntType(IntType ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[int]\n");
        return null;
    }

    @Override
    public Object visitFloatType(FloatType ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[float]\n");
        return null;
    }

    @Override
    public Object visitStringType(StringType ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[string]\n");
        return null;
    }

    @Override
    public Object visitArrayType(ArrayType ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[ArrayType]\n");
        ast.T.visit(this, index);
        ast.E.visit(this, index);
        return null;
    }

    @Override
    public Object visitErrorType(ErrorType ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[ErrorType]\n");
        return null;
    }

    @Override
    public Object visitSimpleVar(SimpleVar ast, Object o) {
        int parentIndex = (int) o;
        int index = getIndex();
        output.append(parentIndex + " --> " + index + "[SimpleVar]\n");
        ast.I.visit(this, index);
        return null;
    }
}
