/*
 * Parser.java 
 */
package ricelang.VC.Parser;

import ricelang.VC.ASTs.*;
import ricelang.VC.ErrorReporter;
import ricelang.VC.Scanner.Scanner;
import ricelang.VC.Scanner.SourcePosition;
import ricelang.VC.Scanner.Token;

public class Parser {

    private final Scanner scanner;
    private final ErrorReporter errorReporter;
    private Token currentToken;
    private SourcePosition previousTokenPosition;
    private final SourcePosition dummyPos = new SourcePosition();

    public Parser(Scanner lexer, ErrorReporter reporter) {
        scanner = lexer;
        errorReporter = reporter;

        previousTokenPosition = new SourcePosition();

        currentToken = scanner.getToken();
    }

// match checks to see f the current token matches tokenExpected.
// If so, fetches the next token.
// If not, reports a syntactic error.
    void match(int tokenExpected) throws SyntaxError {
        if (currentToken.kind == tokenExpected) {
            previousTokenPosition = currentToken.position;
            currentToken = scanner.getToken();
        } else {
            syntacticError("\"%\" expected here", Token.spell(tokenExpected));
        }
    }

    void accept() {
        previousTokenPosition = currentToken.position;
        currentToken = scanner.getToken();
    }

    void syntacticError(String messageTemplate, String tokenQuoted) throws SyntaxError {
        SourcePosition pos = currentToken.position;
        errorReporter.reportError(messageTemplate, tokenQuoted, pos);
        throw (new SyntaxError());
    }

// start records the position of the start of a phrase.
// This is defined to be the position of the first
// character of the first token of the phrase.
    void start(SourcePosition position) {
        position.lineStart = currentToken.position.lineStart;
        position.charStart = currentToken.position.charStart;
    }

// finish records the position of the end of a phrase.
// This is defined to be the position of the last
// character of the last token of the phrase.
    void finish(SourcePosition position) {
        position.lineFinish = previousTokenPosition.lineFinish;
        position.charFinish = previousTokenPosition.charFinish;
    }

    void copyStart(SourcePosition from, SourcePosition to) {
        to.lineStart = from.lineStart;
        to.charStart = from.charStart;
    }

// ========================== PROGRAMS ========================
    public Program parseProgram() {
        Program programAST;

        SourcePosition programPos = new SourcePosition();
        start(programPos);

        try {
            // List dlAST = parseFuncDeclList();
            if (currentToken.kind == Token.VOID || currentToken.kind == Token.BOOLEAN || currentToken.kind == Token.INT || currentToken.kind == Token.FLOAT) {
                List dlAST = parseFuncOrVarDeclList(null);
                finish(programPos);
                programAST = new Program(dlAST, programPos);
            } else {
                programAST = new Program(new EmptyDeclList(dummyPos), programPos);
            }

            if (currentToken.kind != Token.EOF) {
                syntacticError("\"%\" unknown type", currentToken.spelling);
            }
        } catch (SyntaxError s) {
            return null;
        }
        return programAST;
    }

// ========================== DECLARATIONS ========================
    List parseFuncOrVarDeclList(Type tAST) throws SyntaxError {
        List dlAST;
        Decl dAST;

        SourcePosition funcOrVarPos = new SourcePosition();
        start(funcOrVarPos);

        // if tAST already exists, then we parsing tail terms of `type id1, id2, id3;`
        if (tAST == null) {
            tAST = parseType();
        } else if (tAST.isArrayType()) {
            tAST = ((ArrayType) tAST).T;
        }
        Ident iAST = parseIdent();

        // could be an array bruv if it is, must be [ INTLITERAL? ]
        if (currentToken.kind == Token.LBRACKET) {
            match(Token.LBRACKET);
            switch (currentToken.kind) {
                case Token.RBRACKET -> {
                    match(Token.RBRACKET);
                    tAST = new ArrayType(tAST, new EmptyExpr(dummyPos), funcOrVarPos);
                }
                case Token.INTLITERAL -> {
                    IntLiteral ilAST = parseIntLiteral();
                    Expr indexAST = new IntExpr(ilAST, funcOrVarPos);
                    match(Token.RBRACKET);
                    tAST = new ArrayType(tAST, indexAST, funcOrVarPos);
                }
                default ->
                    syntacticError("%s illegal array decl index", currentToken.spelling);
            }
        }

        if (currentToken.kind == Token.LPAREN) {
            dAST = parseFuncDecl(tAST, iAST);
        } else {
            SourcePosition varPos = new SourcePosition();
            start(varPos);

            Expr eAST;
            if (currentToken.kind == Token.EQ) {
                match(Token.EQ);
                if (currentToken.kind == Token.LCURLY) {
                    match(Token.LCURLY);
                    eAST = new ArrayInitExpr(parseArrayInitExpr(), varPos);
                    match(Token.RCURLY);
                } else {
                    eAST = parseExpr();
                }
            } else {
                eAST = new EmptyExpr(varPos);
            }

            if (currentToken.kind == Token.COMMA) {
                match(Token.COMMA);
                dlAST = parseFuncOrVarDeclList(tAST);
                finish(varPos);
                dAST = new GlobalVarDecl(tAST, iAST, eAST, varPos);
                return new DeclList(dAST, dlAST, varPos);
            } else {
                match(Token.SEMICOLON);
                finish(varPos);
                dAST = new GlobalVarDecl(tAST, iAST, eAST, varPos);
            }
        }

        if (currentToken.kind == Token.VOID || currentToken.kind == Token.BOOLEAN || currentToken.kind == Token.INT || currentToken.kind == Token.FLOAT) {
            dlAST = parseFuncOrVarDeclList(null);

            finish(funcOrVarPos);
            dlAST = new DeclList(dAST, dlAST, funcOrVarPos);
        } else {
            finish(funcOrVarPos);
            dlAST = new DeclList(dAST, new EmptyDeclList(dummyPos), funcOrVarPos);
        }
        return dlAST;
    }

    Decl parseFuncDecl(Type tAST, Ident iAST) throws SyntaxError {
        Decl fAST;

        SourcePosition funcPos = new SourcePosition();
        start(funcPos);

        // Type tAST = parseType();
        // Ident iAST = parseIdent();
        List fplAST = parseParaList();
        Stmt cAST = parseCompoundStmt();
        finish(funcPos);
        fAST = new FuncDecl(tAST, iAST, fplAST, cAST, funcPos);
        return fAST;
    }

//  ======================== TYPES ==========================
    Type parseType() throws SyntaxError {
        Type typeAST = null;

        SourcePosition typePos = currentToken.position;
        start(typePos);

        switch (currentToken.kind) {
            case Token.VOID -> {
                typeAST = new VoidType(typePos);
                accept();
            }
            case Token.BOOLEAN -> {
                typeAST = new BooleanType(typePos);
                accept();
            }
            case Token.INT -> {
                typeAST = new IntType(typePos);
                accept();
            }
            case Token.FLOAT -> {
                typeAST = new FloatType(typePos);
                accept();
            }
            default ->
                syntacticError("type expected here but got: %", currentToken.spelling);
        }

        finish(typePos);

        return typeAST;
    }

// ======================= STATEMENTS ==============================
    Stmt parseCompoundStmt() throws SyntaxError {
        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);

        match(Token.LCURLY);
        if (currentToken.kind == Token.RCURLY) {
            match(Token.RCURLY);
            finish(stmtPos);
            return new EmptyCompStmt(stmtPos);
        }

        List dlAST = parseLocalVarDeclList(null);
        List slAST = parseStmtList();

        match(Token.RCURLY);
        finish(stmtPos);

        return new CompoundStmt(dlAST, slAST, stmtPos);
    }

    List parseLocalVarDeclList(Type tAST) throws SyntaxError {
        if (currentToken.kind != Token.VOID
                && currentToken.kind != Token.BOOLEAN
                && currentToken.kind != Token.INT
                && currentToken.kind != Token.FLOAT
                && tAST == null) {
            return new EmptyDeclList(dummyPos);
        }

        List dlAST;
        Decl dAST;

        SourcePosition varPos = new SourcePosition();
        start(varPos);

        // if there's already a type, we are parsing tail declarations of a `type id1[2], id2, id3;`
        if (tAST == null) {
            tAST = parseType();
        } else if (tAST.isArrayType()) {
            // extract type from ArrayType cuz subsequent decl mightn't be ArrayType aswell
            tAST = ((ArrayType) tAST).T;
        }
        Ident iAST = parseIdent();

        if (currentToken.kind == Token.LBRACKET) {
            match(Token.LBRACKET);
            switch (currentToken.kind) {
                case Token.RBRACKET -> {
                    match(Token.RBRACKET);
                    tAST = new ArrayType(tAST, new EmptyExpr(dummyPos), varPos);
                }
                case Token.INTLITERAL -> {
                    IntLiteral ilAST = parseIntLiteral();
                    Expr indexAST = new IntExpr(ilAST, varPos);
                    match(Token.RBRACKET);
                    tAST = new ArrayType(tAST, indexAST, varPos);
                }
                default ->
                    syntacticError("%s illegal array decl index", currentToken.spelling);
            }
        }

        Expr eAST;
        if (currentToken.kind == Token.EQ) {
            match(Token.EQ);
            if (currentToken.kind == Token.LCURLY) {
                match(Token.LCURLY);
                eAST = new ArrayInitExpr(parseArrayInitExpr(), varPos);
                match(Token.RCURLY);
            } else {
                eAST = parseExpr();
            }
        } else {
            eAST = new EmptyExpr(varPos);
        }

        if (currentToken.kind == Token.COMMA) {
            match(Token.COMMA);
            finish(varPos);
            dlAST = parseLocalVarDeclList(tAST);
            dAST = new LocalVarDecl(tAST, iAST, eAST, varPos);
            return new DeclList(dAST, dlAST, varPos);
        } else {
            match(Token.SEMICOLON);
            finish(varPos);
            dAST = new LocalVarDecl(tAST, iAST, eAST, varPos);
        }

        if (currentToken.kind == Token.VOID
                || currentToken.kind == Token.BOOLEAN
                || currentToken.kind == Token.INT
                || currentToken.kind == Token.FLOAT) {
            dlAST = parseLocalVarDeclList(null);
            finish(varPos);
            dlAST = new DeclList(dAST, dlAST, varPos);
        } else {
            finish(varPos);
            dlAST = new DeclList(dAST, new EmptyDeclList(dummyPos), varPos);
        }

        return dlAST;
    }

    List parseStmtList() throws SyntaxError {
        List slAST;

        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);

        if (currentToken.kind != Token.RCURLY) {
            Stmt sAST = parseStmt();
            {
                if (currentToken.kind != Token.RCURLY) {
                    slAST = parseStmtList();
                    finish(stmtPos);
                    slAST = new StmtList(sAST, slAST, stmtPos);
                } else {
                    finish(stmtPos);
                    slAST = new StmtList(sAST, new EmptyStmtList(dummyPos), stmtPos);
                }
            }
        } else {
            slAST = new EmptyStmtList(dummyPos);
        }

        return slAST;
    }

    Stmt parseStmt() throws SyntaxError {
        return switch (currentToken.kind) {
            case Token.LCURLY ->
                parseCompoundStmt();
            case Token.IF ->
                parseIfStmt();
            case Token.FOR ->
                parseForStmt();
            case Token.WHILE ->
                parseWhileStmt();
            case Token.BREAK ->
                parseBreakStmt();
            case Token.CONTINUE ->
                parseContinueStmt();
            case Token.RETURN ->
                parseReturnStmt();
            default ->
                parseExprStmt();
        };
    }

    Stmt parseIfStmt() throws SyntaxError {
        Stmt sAST;

        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);

        match(Token.IF);
        match(Token.LPAREN);

        Expr eAST = parseExpr();
        match(Token.RPAREN);
        sAST = parseStmt();
        if (currentToken.kind == Token.ELSE) {
            match(Token.ELSE);
            Stmt s2AST = parseStmt();
            finish(stmtPos);
            return new IfStmt(eAST, sAST, s2AST, stmtPos);
        } else {
            finish(stmtPos);
            return new IfStmt(eAST, sAST, stmtPos);
        }
    }

    Stmt parseWhileStmt() throws SyntaxError {
        Stmt sAST;
        Expr eAST;

        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);

        match(Token.WHILE);
        match(Token.LPAREN);

        eAST = parseExpr();

        match(Token.RPAREN);

        sAST = parseStmt();
        finish(stmtPos);
        return new WhileStmt(eAST, sAST, stmtPos);
    }

    Stmt parseForStmt() throws SyntaxError {
        Stmt sAST;
        Expr e1AST;
        Expr e2AST;
        Expr e3AST;

        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);

        match(Token.FOR);
        match(Token.LPAREN);
        if (currentToken.kind == Token.SEMICOLON) {
            e1AST = new EmptyExpr(dummyPos);
            match(Token.SEMICOLON);
        } else {
            e1AST = parseExpr();
            match(Token.SEMICOLON);
        }

        if (currentToken.kind == Token.SEMICOLON) {
            e2AST = new EmptyExpr(dummyPos);
            match(Token.SEMICOLON);
        } else {
            e2AST = parseExpr();
            match(Token.SEMICOLON);
        }

        if (currentToken.kind == Token.RPAREN) {
            e3AST = new EmptyExpr(dummyPos);
            match(Token.RPAREN);
        } else {
            e3AST = parseExpr();
            match(Token.RPAREN);
        }

        sAST = parseStmt();
        finish(stmtPos);

        return new ForStmt(e1AST, e2AST, e3AST, sAST, stmtPos);
    }

    Stmt parseBreakStmt() throws SyntaxError {
        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);

        match(Token.BREAK);
        match(Token.SEMICOLON);

        finish(stmtPos);

        return new BreakStmt(stmtPos);
    }

    Stmt parseContinueStmt() throws SyntaxError {
        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);

        match(Token.CONTINUE);
        match(Token.SEMICOLON);

        finish(stmtPos);

        return new ContinueStmt(stmtPos);
    }

    Stmt parseReturnStmt() throws SyntaxError {
        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);

        match(Token.RETURN);

        if (currentToken.kind == Token.SEMICOLON) {
            match(Token.SEMICOLON);
            finish(stmtPos);
            return new ReturnStmt(new EmptyExpr(dummyPos), stmtPos);
        } else {
            Expr eAST = parseExpr();
            match(Token.SEMICOLON);
            finish(stmtPos);
            return new ReturnStmt(eAST, stmtPos);
        }
    }

    Stmt parseExprStmt() throws SyntaxError {
        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);

        if (currentToken.kind == Token.SEMICOLON) {
            match(Token.SEMICOLON);
            finish(stmtPos);
            return new ExprStmt(new EmptyExpr(dummyPos), stmtPos);
        } else {
            Expr eAST = parseExpr();
            match(Token.SEMICOLON);
            finish(stmtPos);
            return new ExprStmt(eAST, stmtPos);
        }
    }

// ======================= PARAMETERS =======================
    List parseParaList() throws SyntaxError {
        List formalsAST;

        SourcePosition formalsPos = new SourcePosition();
        start(formalsPos);

        match(Token.LPAREN);
        if (currentToken.kind == Token.RPAREN) {
            match(Token.RPAREN);
            finish(formalsPos);
            formalsAST = new EmptyParaList(formalsPos);
        } else {
            formalsAST = parseProperParaList();
            match(Token.RPAREN);
            finish(formalsPos);
        }

        return formalsAST;
    }

    List parseProperParaList() throws SyntaxError {
        SourcePosition formalsPos = new SourcePosition();
        start(formalsPos);
        ParaDecl pAST;
        Type tAST = parseType();
        Ident idAST = parseIdent();

        if (currentToken.kind == Token.LBRACKET) {
            match(Token.LBRACKET);
            switch (currentToken.kind) {
                case Token.RBRACKET -> {
                    match(Token.RBRACKET);
                    tAST = new ArrayType(tAST, new EmptyExpr(dummyPos), formalsPos);
                }
                case Token.INTLITERAL -> {
                    IntLiteral ilAST = parseIntLiteral();
                    Expr indexAST = new IntExpr(ilAST, formalsPos);
                    match(Token.RBRACKET);
                    tAST = new ArrayType(tAST, indexAST, formalsPos);
                }
                default ->
                    syntacticError("%s illegal array expr index in func params", currentToken.spelling);
            }
        }

        pAST = new ParaDecl(tAST, idAST, formalsPos);
        if (currentToken.kind == Token.COMMA) {
            match(Token.COMMA);
            List plAST = parseProperParaList();
            if (plAST instanceof ParaList) {
                return new ParaList(pAST, plAST, formalsPos);
            }
        }
        finish(formalsPos);
        return new ParaList(pAST, new EmptyParaList(dummyPos), formalsPos);

    }

// ======================= EXPRESSIONS ======================
    List parseArrayInitExpr() throws SyntaxError {
        SourcePosition pos = new SourcePosition();
        start(pos);

        if (currentToken.kind == Token.RCURLY) {
            return new EmptyArrayExprList(pos);
        }

        List ilAST;
        Expr eAST = parseExpr();
        switch (currentToken.kind) {
            case Token.COMMA -> {
                match(Token.COMMA);
                ilAST = parseArrayInitExpr();
                ilAST = new ArrayExprList(eAST, ilAST, pos);
            }
            case Token.RCURLY -> {
                ilAST = new ArrayExprList(eAST, new EmptyArrayExprList(dummyPos), pos);
            }
            default -> {
                ilAST = new EmptyArrayExprList(pos);
            }
        }

        return ilAST;
    }

    Expr parseExpr() throws SyntaxError {
        return ParseAssignExpr();
    }

    // assignExpr is right associative so we finna use recursion
    Expr ParseAssignExpr() throws SyntaxError {
        SourcePosition addStartPos = new SourcePosition();
        start(addStartPos);

        Expr leftExprAST = parseCondOrExpr();
        if (currentToken.kind == Token.EQ) {
            acceptOperator();
            Expr rightExprAST = ParseAssignExpr();

            SourcePosition assPos = new SourcePosition();
            copyStart(addStartPos, assPos);
            finish(assPos);
            return new AssignExpr(leftExprAST, rightExprAST, assPos);
        }
        return leftExprAST;
    }

    Expr parseCondOrExpr() throws SyntaxError {
        Expr exprAST;
        SourcePosition addStartPos = new SourcePosition();
        start(addStartPos);

        exprAST = parseCondAndExpr();
        while (currentToken.kind == Token.OROR) {
            Operator opAST = acceptOperator();
            Expr e2AST = parseCondAndExpr();

            SourcePosition condOrPos = new SourcePosition();
            copyStart(addStartPos, condOrPos);
            finish(condOrPos);
            exprAST = new BinaryExpr(exprAST, opAST, e2AST, condOrPos);
        }

        return exprAST;
    }

    Expr parseCondAndExpr() throws SyntaxError {
        Expr exprAST;
        SourcePosition addStartPos = new SourcePosition();
        start(addStartPos);

        exprAST = parseEqualityExpr();
        while (currentToken.kind == Token.ANDAND) {
            Operator opAST = acceptOperator();
            Expr e2AST = parseEqualityExpr();

            SourcePosition condAndPos = new SourcePosition();
            copyStart(addStartPos, condAndPos);
            finish(condAndPos);
            exprAST = new BinaryExpr(exprAST, opAST, e2AST, condAndPos);
        }

        return exprAST;
    }

    Expr parseEqualityExpr() throws SyntaxError {
        Expr exprAST;
        SourcePosition addStartPos = new SourcePosition();
        start(addStartPos);

        exprAST = parseRelExpr();
        while (currentToken.kind == Token.EQEQ || currentToken.kind == Token.NOTEQ) {
            Operator opAST = acceptOperator();
            Expr e2AST = parseRelExpr();

            SourcePosition eqPos = new SourcePosition();
            copyStart(addStartPos, eqPos);
            finish(eqPos);
            exprAST = new BinaryExpr(exprAST, opAST, e2AST, eqPos);
        }

        return exprAST;
    }

    Expr parseRelExpr() throws SyntaxError {
        Expr exprAST;
        SourcePosition addStartPos = new SourcePosition();
        start(addStartPos);

        exprAST = parseAdditiveExpr();
        while (currentToken.kind == Token.LT || currentToken.kind == Token.LTEQ || currentToken.kind == Token.GT || currentToken.kind == Token.GTEQ) {
            Operator opAST = acceptOperator();
            Expr e2AST = parseAdditiveExpr();

            SourcePosition relPos = new SourcePosition();
            copyStart(addStartPos, relPos);
            finish(relPos);
            exprAST = new BinaryExpr(exprAST, opAST, e2AST, relPos);
        }

        return exprAST;
    }

    Expr parseAdditiveExpr() throws SyntaxError {
        Expr exprAST;

        SourcePosition addStartPos = new SourcePosition();
        start(addStartPos);

        exprAST = parseMultiplicativeExpr();
        while (currentToken.kind == Token.PLUS
                || currentToken.kind == Token.MINUS) {
            Operator opAST = acceptOperator();
            Expr e2AST = parseMultiplicativeExpr();

            SourcePosition addPos = new SourcePosition();
            copyStart(addStartPos, addPos);
            finish(addPos);
            exprAST = new BinaryExpr(exprAST, opAST, e2AST, addPos);
        }
        return exprAST;
    }

    Expr parseMultiplicativeExpr() throws SyntaxError {
        Expr exprAST;
        SourcePosition multStartPos = new SourcePosition();
        start(multStartPos);

        exprAST = parseUnaryExpr();
        while (currentToken.kind == Token.MULT
                || currentToken.kind == Token.DIV) {
            Operator opAST = acceptOperator();
            Expr e2AST = parseUnaryExpr();
            SourcePosition multPos = new SourcePosition();
            copyStart(multStartPos, multPos);
            finish(multPos);
            exprAST = new BinaryExpr(exprAST, opAST, e2AST, multPos);
        }
        return exprAST;
    }

    Expr parseUnaryExpr() throws SyntaxError {
        Expr exprAST;

        SourcePosition unaryPos = new SourcePosition();
        start(unaryPos);

        switch (currentToken.kind) {
            case Token.PLUS, Token.MINUS, Token.NOT -> {
                Operator opAST = acceptOperator();
                Expr e2AST = parseUnaryExpr();
                finish(unaryPos);
                exprAST = new UnaryExpr(opAST, e2AST, unaryPos);
            }
            default ->
                exprAST = parsePrimaryExpr();
        }
        return exprAST;
    }

    Expr parsePrimaryExpr() throws SyntaxError {

        Expr exprAST = null;

        SourcePosition primPos = new SourcePosition();
        start(primPos);

        switch (currentToken.kind) {
            case Token.ID -> {
                Ident iAST = parseIdent();

                switch (currentToken.kind) {
                    case (Token.LPAREN) -> {
                        match(Token.LPAREN);
                        if (currentToken.kind == Token.RPAREN) {
                            match(Token.RPAREN);
                            finish(primPos);
                            exprAST = new CallExpr(iAST, new EmptyArgList(dummyPos), primPos);
                        } else {
                            List aplAST = parseProperArgList();
                            match(Token.RPAREN);
                            finish(primPos);
                            exprAST = new CallExpr(iAST, aplAST, primPos);
                        }
                    }
                    case (Token.LBRACKET) -> {
                        match(Token.LBRACKET);
                        Expr indexAST = parseExpr();
                        match(Token.RBRACKET);
                        Var idAST = new SimpleVar(iAST, primPos);
                        finish(primPos);
                        // arrays in expressions can be accessed with any expression
                        exprAST = new ArrayExpr(idAST, indexAST, primPos);
                    }
                    default -> {
                        finish(primPos);
                        Var simVAST = new SimpleVar(iAST, primPos);
                        exprAST = new VarExpr(simVAST, primPos);
                    }
                }
            }
            case Token.LPAREN -> {
                accept();
                exprAST = parseExpr();
                match(Token.RPAREN);
            }
            case Token.INTLITERAL -> {
                IntLiteral ilAST = parseIntLiteral();
                finish(primPos);
                exprAST = new IntExpr(ilAST, primPos);
            }
            case Token.FLOATLITERAL -> {
                FloatLiteral flAST = parseFloatLiteral();
                finish(primPos);
                exprAST = new FloatExpr(flAST, primPos);
            }
            case Token.BOOLEANLITERAL -> {
                BooleanLiteral blAST = parseBooleanLiteral();
                finish(primPos);
                exprAST = new BooleanExpr(blAST, primPos);
            }
            case Token.STRINGLITERAL -> {
                StringLiteral slAST = parseStringLiteral();
                finish(primPos);
                exprAST = new StringExpr(slAST, primPos);
            }

            default ->
                syntacticError("illegal primary expression: %", currentToken.spelling);

        }
        return exprAST;
    }

    List parseProperArgList() throws SyntaxError {
        SourcePosition pos = new SourcePosition();
        start(pos);
        Expr eAST;

        eAST = parseExpr();
        Arg argAST = new Arg(eAST, pos);

        if (currentToken.kind == Token.COMMA) {
            match(Token.COMMA);
            List aplAST = parseProperArgList();
            if (aplAST instanceof ArgList) {
                return new ArgList(argAST, aplAST, pos);
            }
        }
        finish(pos);
        return new ArgList(argAST, new EmptyArgList(dummyPos), pos);
    }

// ========================== ID, OPERATOR and LITERALS ========================
    Ident parseIdent() throws SyntaxError {
        Ident I = null;

        if (currentToken.kind == Token.ID) {
            previousTokenPosition = currentToken.position;
            String spelling = currentToken.spelling;
            I = new Ident(spelling, previousTokenPosition);
            currentToken = scanner.getToken();
        } else {
            syntacticError("identifier expected here", "");
        }
        return I;
    }

// acceptOperator parses an operator, and constructs a leaf AST for it
    Operator acceptOperator() throws SyntaxError {
        Operator O;

        previousTokenPosition = currentToken.position;
        String spelling = currentToken.spelling;
        O = new Operator(spelling, previousTokenPosition);
        currentToken = scanner.getToken();
        return O;
    }

    IntLiteral parseIntLiteral() throws SyntaxError {
        IntLiteral IL = null;

        if (currentToken.kind == Token.INTLITERAL) {
            String spelling = currentToken.spelling;
            accept();
            IL = new IntLiteral(spelling, previousTokenPosition);
        } else {
            syntacticError("integer literal expected here", "");
        }
        return IL;
    }

    FloatLiteral parseFloatLiteral() throws SyntaxError {
        FloatLiteral FL = null;

        if (currentToken.kind == Token.FLOATLITERAL) {
            String spelling = currentToken.spelling;
            accept();
            FL = new FloatLiteral(spelling, previousTokenPosition);
        } else {
            syntacticError("float literal expected here", "");
        }
        return FL;
    }

    BooleanLiteral parseBooleanLiteral() throws SyntaxError {
        BooleanLiteral BL = null;

        if (currentToken.kind == Token.BOOLEANLITERAL) {
            String spelling = currentToken.spelling;
            accept();
            BL = new BooleanLiteral(spelling, previousTokenPosition);
        } else {
            syntacticError("boolean literal expected here", "");
        }
        return BL;
    }

    StringLiteral parseStringLiteral() throws SyntaxError {
        StringLiteral SL = null;

        if (currentToken.kind == Token.STRINGLITERAL) {
            String spelling = currentToken.spelling;
            accept();
            SL = new StringLiteral(spelling, previousTokenPosition);
        } else {
            syntacticError("string literal expected here", "");
        }
        return SL;
    }
}
