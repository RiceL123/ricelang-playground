/*
 * Recogniser.java            
 *
 * Thur 6 Mar 2025 14:06:17 AEDT
 */
package VC.Recogniser;

import VC.ErrorReporter;
import VC.Scanner.Scanner;
import VC.Scanner.SourcePosition;
import VC.Scanner.Token;

public class Recogniser {

    private Scanner scanner;
    private ErrorReporter errorReporter;
    private Token currentToken;

    public Recogniser(Scanner lexer, ErrorReporter reporter) {
        scanner = lexer;
        errorReporter = reporter;
        currentToken = scanner.getToken();
        // System.out.println("current token: " + currentToken);
    }

    // match checks to see if the current token matches tokenExpected.
    // If so, fetches the next token.
    // If not, reports a syntactic error.
    void match(int tokenExpected) throws SyntaxError {
        if (currentToken.kind == tokenExpected) {
            currentToken = scanner.getToken();
        } else {
            syntacticError("\"%\" expected here but found" + currentToken, Token.spell(tokenExpected));
        }
    }

    // primitive types
    // type -> void | boolean | int | float
    void matchType() throws SyntaxError {
        switch (currentToken.kind) {
            case Token.VOID, Token.BOOLEAN, Token.INT, Token.FLOAT ->
                accept();
            default ->
                syntacticError("\"%\" expected here", "type");
        }
    }

    // accepts the current token and fetches the next
    void accept() {
        currentToken = scanner.getToken();
    }

    // Handles syntactic errors and reports them via the error reporter.
    void syntacticError(String messageTemplate, String tokenQuoted) throws SyntaxError {
        SourcePosition pos = currentToken.position;
        errorReporter.reportError(messageTemplate, tokenQuoted, pos);
        throw new SyntaxError();
    }

    // ========================== PROGRAMS ========================
    public void parseProgram() {
        try {
            // program             ->  ( func-decl | var-decl )*
            while (currentToken.kind != Token.EOF) {
                parseFuncOrVarDecl();
            }
            // if (currentToken.kind != Token.EOF) {
            //     syntacticError("\"%\" wrong result type for a function", currentToken.spelling);
            // }
        } catch (SyntaxError s) {
        }
    }

    // ========================== DECLARATIONS ========================
    // program             ->  ( func-decl | var-decl )*
    void parseFuncOrVarDecl() throws SyntaxError {
        if (currentToken.kind == Token.EOF) {
            return; // breaks out the loop you know
        }

        matchType();
        parseIdent();

        if (currentToken.kind == Token.LPAREN) {
            parseFuncDecl();
        } else {
            parseVarDecl();
        }
    }

    // func-decl           -> type identifier para-list compound-stmt
    // para-list           -> "(" proper-para-list? ")"
    void parseFuncDecl() throws SyntaxError {
        // matchType(); // common prefix with varDecl dw about it
        // parseIdent(); // common prefix with varDecl dw about it
        match(Token.LPAREN);
        if (currentToken.kind == Token.RPAREN) {
            match(Token.RPAREN);
        } else {
            parseProperParaList();
            match(Token.RPAREN);
        }
        parseCompoundStmt();
    }

    // proper-para-list    -> para-decl ( "," para-decl )*
    // para-decl           -> type declarator
    void parseProperParaList() throws SyntaxError {
        matchType();
        parseDeclarator();
        while (currentToken.kind == Token.COMMA) {
            match(Token.COMMA);
            matchType();
            parseDeclarator();
        }
    }

    // declarator          -> identifier 
    //                        |  identifier "[" INTLITERAL? "]"
    void parseDeclarator() throws SyntaxError {
        parseIdent();
        if (currentToken.kind == Token.LBRACKET) {
            match(Token.LBRACKET);
            if (currentToken.kind == Token.INTLITERAL) {
                parseIntLiteral();
            }
            match(Token.RBRACKET);
        }
    }

    // var-decl            -> type init-declarator-list ";"
    void parseVarDecl() throws SyntaxError {
        // matchType(); // common prefex with func dec dw about it
        parseInitDeclaratorList();
        match(Token.SEMICOLON);
    }

    // init-declarator-list-> init-declarator ( "," init-declarator )*
    void parseInitDeclaratorList() throws SyntaxError {
        parseInitDeclaratorNoIdent();
        while (currentToken.kind == Token.COMMA) {
            match(Token.COMMA);
            parseInitDeclarator();
        }
    }

    void parseInitDeclarator() throws SyntaxError {
        parseDeclarator();
        if (currentToken.kind == Token.EQ) {
            match(Token.EQ);
            parseInitaliser();
        }
    }

    // init-declarator     -> declarator ( "=" initialiser )? 
    void parseInitDeclaratorNoIdent() throws SyntaxError {
        parseDeclaratorNoIdent();
        if (currentToken.kind == Token.EQ) {
            match(Token.EQ);
            parseInitaliser();
        }
    }

    // declarator          -> identifier 
    //                     |  identifier "[" INTLITERAL? "]"
    void parseDeclaratorNoIdent() throws SyntaxError {
        // parseIdent(); // common prefix with func decl dw about it
        // or
        if (currentToken.kind == Token.LBRACKET) {
            match(Token.LBRACKET);
            if (currentToken.kind == Token.INTLITERAL) {
                parseIntLiteral();
            }
            match(Token.RBRACKET);
        }
    }

    // initialiser         -> expr 
    //                     |  "{" expr ( "," expr )* "}"
    void parseInitaliser() throws SyntaxError {
        if (currentToken.kind == Token.LCURLY) {
            match(Token.LCURLY);
            parseExpr();
            while (currentToken.kind == Token.COMMA) {
                match(Token.COMMA);
                parseExpr();
            }
            match(Token.RCURLY);
        } else {
            parseExpr();
        }
    }

    // ======================= STATEMENTS ==============================
    // Note: modern languages typically are 
    // compound-stmt       -> "{" ( var-decl | stmt )* "}"
    // but VC is an older language with forces declarations to be at the top
    // compound-stmt       -> "{" var-decl* stmt* "}" 
    void parseCompoundStmt() throws SyntaxError {
        match(Token.LCURLY);

        while (currentToken.kind == Token.VOID
                || currentToken.kind == Token.BOOLEAN
                || currentToken.kind == Token.INT
                || currentToken.kind == Token.FLOAT) {
            matchType();
            parseIdent();
            parseVarDecl(); // assumed type and ident are already parsed
        }

        while (currentToken.kind != Token.RCURLY) {
            parseStmt();
        }

        match(Token.RCURLY);
    }

    // stmt                -> compound-stmt
    //                     |  if-stmt 
    //                     |  for-stmt
    //                     |  while-stmt 
    //                     |  break-stmt
    //                     |  continue-stmt
    //                     |  return-stmt
    //                     |  expr-stmt
    void parseStmt() throws SyntaxError {
        switch (currentToken.kind) {
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
        }
    }

    // if-stmt             -> if "(" expr ")" stmt ( else stmt )?
    void parseIfStmt() throws SyntaxError {
        match(Token.IF);
        match(Token.LPAREN);
        parseExpr();
        match(Token.RPAREN);
        parseStmt();
        if (currentToken.kind == Token.ELSE) {
            match(Token.ELSE);
            parseStmt();
        }
    }

    // for-stmt            -> for "(" expr? ";" expr? ";" expr? ")" stmt
    void parseForStmt() throws SyntaxError {
        match(Token.FOR);
        match(Token.LPAREN);
        if (currentToken.kind == Token.SEMICOLON) {
            match(Token.SEMICOLON);
        } else {
            parseExpr();
            match(Token.SEMICOLON);
        }

        if (currentToken.kind == Token.SEMICOLON) {
            match(Token.SEMICOLON);
        } else {
            parseExpr();
            match(Token.SEMICOLON);
        }

        if (currentToken.kind == Token.RPAREN) {
            match(Token.RPAREN);
        } else {
            parseExpr();
            match(Token.RPAREN);
        }

        parseStmt();
    }

    // while-stmt          -> while "(" expr ")" stmt
    void parseWhileStmt() throws SyntaxError {
        match(Token.WHILE);
        match(Token.LPAREN);
        parseExpr();
        match(Token.RPAREN);
        parseStmt();
    }

    // break-stmt          -> break ";"
    void parseBreakStmt() throws SyntaxError {
        match(Token.BREAK);
        match(Token.SEMICOLON);
    }

    // continue-stmt       -> continue ";"
    void parseContinueStmt() throws SyntaxError {
        match(Token.CONTINUE);
        match(Token.SEMICOLON);
    }

    // return-stmt         -> return expr? ";"
    void parseReturnStmt() throws SyntaxError {
        match(Token.RETURN);
        if (currentToken.kind == Token.SEMICOLON) {
            match(Token.SEMICOLON);
        } else {
            parseExpr();
            match(Token.SEMICOLON);
        }
    }

    // expr-stmt           -> expr? ";"
    void parseExprStmt() throws SyntaxError {
        if (currentToken.kind == Token.SEMICOLON) {
            match(Token.SEMICOLON);
        } else {
            parseExpr();
            match(Token.SEMICOLON);
        }
    }

    // ======================= IDENTIFIERS ======================
    // Calls parseIdent rather than match(Token.ID). In future assignments, 
    // an Identifier node will be constructed in this method.
    void parseIdent() throws SyntaxError {
        if (currentToken.kind == Token.ID) {
            accept();
        } else {
            syntacticError("identifier expected here", "");
        }
    }

    // ======================= OPERATORS ======================
    // Calls acceptOperator rather than accept(). In future assignments, 
    // an Operator Node will be constructed in this method.
    void acceptOperator() throws SyntaxError {
        currentToken = scanner.getToken();
    }

    // ======================= EXPRESSIONS ======================
    // expr                -> assignment-expr
    void parseExpr() throws SyntaxError {
        parseAssignExpr();
    }

    // assignment-expr     -> ( cond-or-expr "=" )* cond-or-expr
    void parseAssignExpr() throws SyntaxError {
        parseCondOrExpr();
        while (currentToken.kind == Token.EQ) {
            acceptOperator();
            parseCondOrExpr();
        }
    }

    // cond-or-expr        -> cond-and-expr 
    //                     |  cond-or-expr "||" cond-and-expr
    void parseCondOrExpr() throws SyntaxError {
        parseCondAndExpr();
        while (currentToken.kind == Token.OROR) {
            acceptOperator();
            parseCondAndExpr();
        }
    }

    // cond-and-expr       -> equality-expr 
    //                     |  cond-and-expr "&&" equality-expr
    void parseCondAndExpr() throws SyntaxError {
        parseEqualityExpr();
        while (currentToken.kind == Token.ANDAND) {
            acceptOperator();
            parseEqualityExpr();
        }
    }

    // equality-expr       -> rel-expr
    //                     |  equality-expr "==" rel-expr
    //                     |  equality-expr "!=" rel-expr
    void parseEqualityExpr() throws SyntaxError {
        parseRelExpr();
        while (currentToken.kind == Token.EQEQ || currentToken.kind == Token.NOTEQ) {
            acceptOperator();
            parseRelExpr();
        }
    }

    // rel-expr            -> additive-expr
    //                     |  rel-expr "<" additive-expr
    //                     |  rel-expr "<=" additive-expr
    //                     |  rel-expr ">" additive-expr
    //                     |  rel-expr ">=" additive-expr
    void parseRelExpr() throws SyntaxError {
        parseAdditiveExpr();
        while (currentToken.kind == Token.LT || currentToken.kind == Token.LTEQ || currentToken.kind == Token.GT || currentToken.kind == Token.GTEQ) {
            acceptOperator();
            parseAdditiveExpr();
        }
    }

    // additive-expr       -> multiplicative-expr
    //                     |  additive-expr "+" multiplicative-expr
    //                     |  additive-expr "-" multiplicative-expr
    void parseAdditiveExpr() throws SyntaxError {
        parseMultiplicativeExpr();
        while (currentToken.kind == Token.PLUS || currentToken.kind == Token.MINUS) {
            acceptOperator();
            parseMultiplicativeExpr();
        }
    }

    // multiplicative-expr -> unary-expr
    //                     |  multiplicative-expr "*" unary-expr
    //                     |  multiplicative-expr "/" unary-expr
    void parseMultiplicativeExpr() throws SyntaxError {
        parseUnaryExpr();
        while (currentToken.kind == Token.MULT || currentToken.kind == Token.DIV) {
            acceptOperator();
            parseUnaryExpr();
        }
    }

    // unary-expr          -> "+" unary-expr
    //                     |  "-" unary-expr
    //                     |  "!" unary-expr
    //                     |  primary-expr
    void parseUnaryExpr() throws SyntaxError {
        switch (currentToken.kind) {
            case Token.PLUS, Token.MINUS, Token.NOT -> {
                acceptOperator();
                parseUnaryExpr();
            }
            default ->
                parsePrimaryExpr();
        }
    }

    // primary-expr        -> ID arg-list?
    //                     | ID "[" expr "]"
    //                     | "(" expr ")"
    //                     | INTLITERAL
    //                     | FLOATLITERAL
    //                     | BOOLLITERAL
    //                     | STRINGLITERAL
    // arg-list            -> "(" proper-arg-list? ")"
    // proper-arg-list     -> arg ( "," arg )*
    // arg                 -> expr
    void parsePrimaryExpr() throws SyntaxError {
        // System.out.println("parsing expr:" + currentToken);
        switch (currentToken.kind) {
            case Token.ID -> {
                parseIdent();

                // arg-list? // a function call
                if (currentToken.kind == Token.LPAREN) {
                    match(Token.LPAREN);
                    if (currentToken.kind == Token.RPAREN) {
                        match(Token.RPAREN);
                    } else {
                        parseProperArgList();
                        match(Token.RPAREN);
                    }

                    // "[" expr "]"
                } else if (currentToken.kind == Token.LBRACKET) {
                    match(Token.LBRACKET);
                    parseExpr();
                    match(Token.RBRACKET);
                }
            }
            case Token.LPAREN -> {
                accept();
                parseExpr();
                match(Token.RPAREN);
            }
            case Token.INTLITERAL ->
                parseIntLiteral();
            case Token.FLOATLITERAL ->
                parseFloatLiteral();
            case Token.BOOLEANLITERAL ->
                parseBooleanLiteral();
            case Token.STRINGLITERAL ->
                parseStringLiteral();

            default ->
                syntacticError("illegal primary expression" + currentToken, currentToken.spelling);
        }
    }

    void parseProperArgList() throws SyntaxError {
        parseExpr();
        while (currentToken.kind == Token.COMMA) {
            match(Token.COMMA);
            parseExpr();
        }
    }

    // ========================== LITERALS ========================
    // Calls these methods rather than accept(). In future assignments, 
    // literal AST nodes will be constructed inside these methods.
    void parseIntLiteral() throws SyntaxError {
        if (currentToken.kind == Token.INTLITERAL) {
            accept();
        } else {
            syntacticError("integer literal expected here", "");
        }
    }

    void parseFloatLiteral() throws SyntaxError {
        if (currentToken.kind == Token.FLOATLITERAL) {
            accept();
        } else {
            syntacticError("float literal expected here", "");
        }
    }

    void parseBooleanLiteral() throws SyntaxError {
        if (currentToken.kind == Token.BOOLEANLITERAL) {
            accept();
        } else {
            syntacticError("boolean literal expected here", "");
        }
    }

    void parseStringLiteral() throws SyntaxError {
        if (currentToken.kind == Token.STRINGLITERAL) {
            accept();
        } else {
            syntacticError("string literal expected here", "");
        }
    }
}
