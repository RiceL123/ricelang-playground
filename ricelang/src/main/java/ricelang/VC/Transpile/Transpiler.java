package ricelang.VC.Transpile;

import java.util.ArrayList;

import ricelang.VC.ASTs.*;

public class Transpiler implements Visitor {
    private static Boolean nodeJS;

    public Transpiler(Boolean vanillaJS) {
        nodeJS = !vanillaJS;
    }

    public final void gen(AST ast) {
        JS.clearInstructions();
        ast.visit(this, null);
        JS.dump("temp.js");
    }

    public final String genString(AST ast) {
        JS.clearInstructions();
        ast.visit(this, null);
        return JS.dump();
    }

    @Override
    public Object visitProgram(Program ast, Object o) {
        if (nodeJS) {
            JS.append("#!/usr/bin/env node");
            JS.append(
                    "const prompt=()=>new Promise(res=>{process.stdin.resume();process.stdin.once('data',x => res(x));});");
        } else {
            JS.append("const stdout=[];");
            JS.append(
                    "const print=x=>stdout.length===0?stdout.push(x):stdout[stdout.length - 1]+=x;");
            JS.append("const println=x=>stdout.length===0?stdout.push(x) : (stdout[stdout.length - 1]+=x) && stdout.push('');");
        }

        ast.FL.visit(this, null);

        if (nodeJS) {
            JS.append("main().finally(()=>process.stdin.pause());");
        } else {
            JS.append("main();\nconsole.log(stdout.join('\\n'));");
        }
        return null;
    }

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

    @Override
    public Object visitFuncDecl(FuncDecl ast, Object o) {
        String name = (String) ast.I.visit(this, null);
        // StringBuilder parameters = new StringBuilder();
        ArrayList<String> parameters = new ArrayList<>();
        List list = ast.PL;
        while (!list.isEmptyParaList()) {
            ParaList pl = (ParaList) list;
            parameters.add((String) pl.P.I.visit(this, null));
            list = pl.PL;
        }

        if (nodeJS && name.equals("main")) {
            JS.append("const main=async()=>");
        } else {
            JS.append("const " + name + "=(" + String.join(",", parameters) + ")=>");
        }
        ast.S.visit(this, null);

        return null;
    }

    private String identity(Type type) {
        if (type.isBooleanType())
            return "false";
        if (type.isFloatType())
            return "0.0";
        return "0";
    }

    private Object varDecl(Ident I, Type T, Expr E) {
        String name = (String) I.visit(this, null);
        if (T instanceof ArrayType arrayType) {
            JS.append("let " + name + "=new Array(" + arrayType.E.visit(this, null) + ").fill("
                    + identity(arrayType.T) + ");");
            if (!E.isEmptyExpr()) {
                // $ is a valid identifier in js but not ricelang so its safe to use
                JS.append(E.visit(this, null) + ".forEach(($, i) => " + name + "[i]=$);");
            }
        } else {
            if (E.isEmptyExpr())
                JS.append("let " + name + "=" + identity(T) + ";");
            else
                JS.append("let " + name + "=" + E.visit(this, null) + ";");
        }
        return null;
    }

    @Override
    public Object visitGlobalVarDecl(GlobalVarDecl ast, Object o) {
        return varDecl(ast.I, ast.T, ast.E);
    }

    @Override
    public Object visitLocalVarDecl(LocalVarDecl ast, Object o) {
        return varDecl(ast.I, ast.T, ast.E);
    }

    @Override
    public Object visitArrayInitExpr(ArrayInitExpr ast, Object o) {
        ArrayList<String> exprs = new ArrayList<>();
        List list = ast.IL;
        while (!list.isEmptyArrayExprList()) {
            ArrayExprList ael = (ArrayExprList) list;
            exprs.add((String) ael.E.visit(this, null));
            list = ael.EL;
        }
        return "[" + String.join(",", exprs) + "]";
    }

    @Override
    public Object visitArrayExprList(ArrayExprList ast, Object o) {
        throw new Error("shouldnt be here: visitArrayExprList");
    }

    @Override
    public Object visitEmptyArrayExprList(EmptyArrayExprList ast, Object o) {
        throw new Error("shouldnt be here: visitEmptyArrayExprList");
    }

    @Override
    public Object visitParaList(ParaList ast, Object o) {
        throw new Error("shouldnt be here: visitParaList");
    }

    @Override
    public Object visitParaDecl(ParaDecl ast, Object o) {
        throw new Error("shouldnt be here: visitParaDecl");
    }

    @Override
    public Object visitEmptyParaList(EmptyParaList ast, Object o) {
        throw new Error("shouldnt be here: visitEmptyParaList");
    }

    @Override
    public Object visitStmtList(StmtList ast, Object o) {
        throw new Error("shouldnt be here: visitStmtList");
    }

    @Override
    public Object visitEmptyStmtList(EmptyStmtList ast, Object o) {
        throw new Error("shouldnt be here: visitEmptyStmtList");
    }

    @Override
    public Object visitArgList(ArgList ast, Object o) {
        throw new Error("shouldnt be here: visitArgList");
    }

    @Override
    public Object visitArg(Arg ast, Object o) {
        throw new Error("shouldnt be here: visitArg");
    }

    @Override
    public Object visitEmptyArgList(EmptyArgList ast, Object o) {
        throw new Error("shouldnt be here: visitEmptyArgList");
    }

    public Object visitIntType(IntType ast, Object o) {
        throw new Error("shouldnt be here: visitIntType");
    }

    public Object visitFloatType(FloatType ast, Object o) {
        throw new Error("shouldnt be here: visitFloatType");
    }

    public Object visitBooleanType(BooleanType ast, Object o) {
        throw new Error("shouldnt be here: visitBooleanType");
    }

    public Object visitArrayType(ArrayType ast, Object o) {
        throw new Error("shouldnt be here: visitArrayType");
    }

    public Object visitStringType(StringType ast, Object o) {
        throw new Error("shouldnt be here: visitStringType");
    }

    public Object visitVoidType(VoidType ast, Object o) {
        throw new Error("shouldnt be here: visitVoidType");
    }

    public Object visitErrorType(ErrorType ast, Object o) {
        throw new Error("shouldnt be here: visitErrorType");
    }

    @Override
    public Object visitIdent(Ident ast, Object o) {
        return ast.spelling;
    }

    @Override
    public Object visitIntLiteral(IntLiteral ast, Object o) {
        return ast.spelling;
    }

    @Override
    public Object visitFloatLiteral(FloatLiteral ast, Object o) {
        return ast.spelling;
    }

    @Override
    public Object visitBooleanLiteral(BooleanLiteral ast, Object o) {
        return ast.spelling;
    }

    @Override
    public Object visitStringLiteral(StringLiteral ast, Object o) {
        return "\"" + ast.spelling.replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }

    @Override
    public Object visitOperator(Operator ast, Object o) {
        return ast.spelling;
    }

    @Override
    public Object visitSimpleVar(SimpleVar ast, Object o) {
        return ast.I.visit(this, null);
    }

    @Override
    public Object visitCompoundStmt(CompoundStmt ast, Object o) {
        JS.append("{");
        JS.incrementIndent();
        List declList = ast.DL;
        while (!declList.isEmptyDeclList()) {
            DeclList decl = (DeclList) declList;
            decl.D.visit(this, null);
            declList = decl.DL;
        }

        List stmtList = ast.SL;
        while (!stmtList.isEmptyStmtList()) {
            StmtList stmt = (StmtList) stmtList;
            stmt.S.visit(this, null);
            stmtList = stmt.SL;
        }
        JS.decrementIndent();
        JS.append("}");
        return null;
    }

    @Override
    public Object visitEmptyCompStmt(EmptyCompStmt ast, Object o) {
        JS.append("{ }");
        return null;
    }

    @Override
    public Object visitEmptyStmt(EmptyStmt ast, Object o) {
        return null;
    }

    @Override
    public Object visitReturnStmt(ReturnStmt ast, Object o) {
        String expr = (String) ast.E.visit(this, null);
        JS.append("return " + expr + ";");
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt ast, Object o) {
        String expr = (String) ast.E.visit(this, null);
        if (ast.S2.isEmptyStmt() || ast.S2.isEmptyCompStmt()) {
            JS.append("if (" + expr + ")");
            ast.S1.visit(this, null);
        } else {
            JS.append("if (" + expr + ")");
            ast.S1.visit(this, null);
            JS.append("else");
            ast.S2.visit(this, null);
        }

        return null;
    }

    @Override
    public Object visitWhileStmt(WhileStmt ast, Object o) {
        String expr = (String) ast.E.visit(this, null);
        JS.append("while (" + expr + ")");
        ast.S.visit(this, null);
        return null;
    }

    @Override
    public Object visitForStmt(ForStmt ast, Object o) {
        String expr1 = (String) ast.E1.visit(this, null);
        String expr2 = (String) ast.E2.visit(this, null);
        String expr3 = (String) ast.E3.visit(this, null);
        JS.append("for (" + expr1 + ";" + expr2 + ";" + expr3 + ")");
        ast.S.visit(this, null);
        return null;
    }

    @Override
    public Object visitBreakStmt(BreakStmt ast, Object o) {
        JS.append("break;");
        return null;
    }

    @Override
    public Object visitContinueStmt(ContinueStmt ast, Object o) {
        JS.append("continue;");
        return null;
    }

    @Override
    public Object visitExprStmt(ExprStmt ast, Object o) {
        String expr = (String) ast.E.visit(this, null);
        JS.append(expr + ";");
        return null;
    }

    @Override
    public Object visitCallExpr(CallExpr ast, Object o) {
        String fname = (String) ast.I.visit(this, null);
        ArrayList<String> exprs = new ArrayList<>();
        List list = ast.AL;
        while (!list.isEmptyArgList()) {
            ArgList argList = (ArgList) list;
            exprs.add((String) argList.A.E.visit(this, null));
            list = argList.AL;
        }

        switch (fname) {
            case "getInt", "getFloat" -> {
                if (nodeJS) {
                    return "Number(await prompt())";
                } else {
                    return "Number(prompt(stdout.join('\\n')))";
                }
            }
            case "putLn" -> {
                if (nodeJS) return "console.log()";
                else return "println('')";
            }
            case "putInt", "putFloat", "putBool", "putString" -> {
                if (nodeJS) {
                    return "process.stdout.write(String(" + String.join(",", exprs) + "))";
                } else {
                    fname = "print";
                }
            }
            case "putIntLn", "putFloatLn", "putBoolLn", "putStringLn" -> {
                if (nodeJS) fname = "console.log";
                else fname = "println";
            }
        }

        return fname + "(" + String.join(",", exprs) + ")";
    }

    @Override
    public Object visitEmptyExpr(EmptyExpr ast, Object o) {
        return null;
    }

    @Override
    public Object visitIntExpr(IntExpr ast, Object o) {
        return ast.IL.visit(this, o);
    }

    @Override
    public Object visitFloatExpr(FloatExpr ast, Object o) {
        return ast.FL.visit(this, o);
    }

    @Override
    public Object visitBooleanExpr(BooleanExpr ast, Object o) {
        return ast.BL.visit(this, o);
    }

    @Override
    public Object visitStringExpr(StringExpr ast, Object o) {
        return ast.SL.visit(this, o);
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr ast, Object o) {
        String op = (String) ast.O.visit(this, null);
        String expr = (String) ast.E.visit(this, null);

        if (op.equals("i2f"))
            return expr;

        op = op.replace("i", "").replace("f", ""); // "i-" and "f-" don't matter in js just do: "-"

        return "(" + op + expr + ")";
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr ast, Object o) {
        String expr1 = (String) ast.E1.visit(this, null);
        String op = (String) ast.O.visit(this, null);
        String expr2 = (String) ast.E2.visit(this, null);

        if (op.equals("i/"))
            return "Math.floor(" + expr1 + "/" + expr2 + ")";

        op = op.replace("i", "").replace("f", ""); // "i==" and "f==" don't matter in js just do: "=="

        return "(" + expr1 + op + expr2 + ")";
    }

    @Override
    public Object visitArrayExpr(ArrayExpr ast, Object o) {
        String aname = (String) ast.V.visit(this, o);
        String expr = (String) ast.E.visit(this, o);
        return aname + "[" + expr + "]";
    }

    @Override
    public Object visitVarExpr(VarExpr ast, Object o) {
        String vname = (String) ast.V.visit(this, o);
        return vname;
    }

    @Override
    public Object visitAssignExpr(AssignExpr ast, Object o) {
        String expr1 = (String) ast.E1.visit(this, null);
        String expr2 = (String) ast.E2.visit(this, null);
        return expr1 + "=" + expr2;
    }
}
