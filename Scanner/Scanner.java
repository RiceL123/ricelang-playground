/*
 * Scanner.java                        
 *
 * Sun 09 Feb 2025 13:31:52 AEDT
 *
 * The starter code here is provided as a high-level guide for implementation.
 *
 * You may completely disregard the starter code and develop your own solution, 
 * provided that it maintains the same public interface.
 *
 */
package VC.Scanner;

import VC.ErrorReporter;

public final class Scanner {

    private SourceFile sourceFile;
    private ErrorReporter errorReporter;
    private boolean debug;

    private StringBuilder currentSpelling;
    private char currentChar;
    private SourcePosition sourcePos;

    private int column; // charStart = column - currentSpelling.length()
    private int line;
    // =========================================================

    public Scanner(SourceFile source, ErrorReporter reporter) {
        sourceFile = source;
        errorReporter = reporter;
        debug = false;

        // Initiaise currentChar for the starter code.  Change it if necessary for your full implementation
        currentChar = sourceFile.getNextChar();
        currentSpelling = new StringBuilder();

        // Initialise your counters for counting line and column numbers here
        column = 1;
        line = 1;

        sourcePos = new SourcePosition(line, line, column, column);
    }

    public void enableDebugging() {
        debug = true;
    }

    // accept gets the next character from the source program.
    private void accept() {
        currentSpelling.append(currentChar);

        switch (currentChar) {
            case '\r' -> {
                if (sourceFile.inspectChar(1) == '\n') {
                    sourceFile.getNextChar(); // consume & ignore next char if '\r\n' found
                }
                line += 1;
                column = 1;
            }
            case '\n' -> {
                line += 1;
                column = 1;
            }
            default ->
                column += 1;
        }

        currentChar = sourceFile.getNextChar();
    }

    private boolean isFraction() {
        return currentChar == '.' && Character.isDigit(inspectChar(1));
    }

    private boolean isExponent() {
        char nextChar = inspectChar(1);
        return (currentChar == 'E' || currentChar == 'e') && (nextChar == '+' || nextChar == '-' || Character.isDigit(nextChar));
    }

    // inspectChar returns the n-th character after currentChar in the input stream.  If there are fewer than nthChar characters between currentChar 
    // and the end of file marker, SourceFile.eof is returned.
    // 
    // Both currentChar and the current position in the input stream
    // are *not* changed. , a subsequent call to accept()
    // will always return the next char after currentChar.
    // That is, inspectChar does not change 
    private char inspectChar(int nthChar) {
        return sourceFile.inspectChar(nthChar);
    }

    private int nextToken() {
        // Tokens: separators, operators, literals, identifiers, and keywords
        switch (currentChar) {
            case '(' -> {
                accept();
                return Token.LPAREN;
            }
            case ')' -> {
                accept();
                return Token.RPAREN;
            }
            case '{' -> {
                accept();
                return Token.LCURLY;
            }
            case '}' -> {
                accept();
                return Token.RCURLY;
            }
            case '[' -> {
                accept();
                return Token.LBRACKET;
            }
            case ']' -> {
                accept();
                return Token.RBRACKET;
            }
            case ';' -> {
                accept();
                return Token.SEMICOLON;
            }
            case ',' -> {
                accept();
                return Token.COMMA;
            }
            case '+' -> {
                accept();
                return Token.PLUS;
            }
            case '-' -> {
                accept();
                return Token.MINUS;
            }
            case '*' -> {
                accept();
                return Token.MULT;
            }
            case '/' -> {
                accept();
                return Token.DIV;
            }
            case '=' -> {
                accept();
                if (currentChar == '=') {
                    accept();
                    return Token.EQEQ;
                }
                return Token.EQ;
            }
            case '!' -> {
                accept();
                if (currentChar == '=') {
                    accept();
                    return Token.NOTEQ;
                }
                return Token.NOT;
            }
            case '<' -> {
                accept();
                if (currentChar == '=') {
                    accept();
                    return Token.LTEQ;
                }
                return Token.LT;
            }
            case '>' -> {
                accept();
                if (currentChar == '=') {
                    accept();
                    return Token.GTEQ;
                }
                return Token.GT;
            }
            case '&' -> {
                accept();
                if (currentChar == '&') {
                    accept();
                    return Token.ANDAND;
                }
                return Token.ERROR; // there is no bitwise & in VC; only &&
            }
            case '|' -> {
                accept();
                if (currentChar == '|') {
                    accept();
                    return Token.OROR;
                }
                return Token.ERROR; // there is no bitwise | in VC; only ||
            }
            case SourceFile.eof -> {
                currentSpelling.append(Token.spell(Token.EOF));
                return Token.EOF;
            }
        }

        if (isFraction()) {
            accept(); // consume the leading '.'
            boolean has_exponent = false;

            while (true) {
                if (!has_exponent && isExponent()) {
                    // skip the (E|e) and the (+|-)?digit then read rest of digit*
                    accept();
                    accept();
                    has_exponent = true;
                }

                if (!Character.isDigit(currentChar)) {
                    break;
                }

                accept();
            }
            return Token.FLOATLITERAL;

        } else if (Character.isDigit(currentChar)) {
            accept();

            while (true) {
                if (isFraction()) {
                    accept(); // consume the leading '.'
                    boolean has_exponent = false;

                    while (true) {
                        if (!has_exponent && isExponent()) {
                            // skip the (E|e) and the (+|-)?digit then read rest of digit*
                            accept();
                            accept();
                            has_exponent = true;
                        }

                        if (!Character.isDigit(currentChar)) {
                            break;
                        }

                        accept();
                    }
                    return Token.FLOATLITERAL;
                } else if (isExponent()) {
                    // skip the (E|e) and the (+|-)?digit then read rest of digit*
                    accept();
                    accept();
                    while (Character.isDigit(currentChar)) {
                        accept();
                    }
                    return Token.FLOATLITERAL;
                } else if (currentChar == '.') {
                    // can't be a fraction so we are checking if it ends with . or ends with . exponent
                    accept();

                    if (isExponent()) {
                        // skip the (E|e) and the (+|-)?digit then read rest of digit*
                        accept();
                        accept();
                        while (Character.isDigit(currentChar)) {
                            accept();
                        }
                    }
                    return Token.FLOATLITERAL;
                } else if (Character.isDigit(currentChar)) {
                    accept();
                } else {
                    return Token.INTLITERAL;
                }
            }
        } else if (currentChar == '\"') {
            accept();
            while (true) {
                if (currentChar == '\"') {
                    break;
                }
                accept();
            }
            accept();
            currentSpelling = new StringBuilder(currentSpelling.toString().replaceFirst("^\"", "").replaceFirst("\"$", "")); // remove first and last " 
            return Token.STRINGLITERAL;
        } else if (Character.isLetter(currentChar)) {
            // identifier -> letter (letter | digit)*
            while (Character.isLetter(currentChar) || Character.isDigit(currentChar)) {
                accept();
            }
            return switch (currentSpelling.toString()) {
                case "boolean" ->
                    Token.BOOLEAN;
                case "break" ->
                    Token.BREAK;
                case "continue" ->
                    Token.CONTINUE;
                case "else" ->
                    Token.ELSE;
                case "float" ->
                    Token.FLOAT;
                case "for" ->
                    Token.FOR;
                case "if" ->
                    Token.IF;
                case "int" ->
                    Token.INT;
                case "return" ->
                    Token.RETURN;
                case "void" ->
                    Token.VOID;
                case "while" ->
                    Token.WHILE;
                case "true", "false" ->
                    Token.BOOLEANLITERAL;
                default ->
                    Token.ID;
            };
        }

        accept();
        return Token.ERROR;
    }

    private void skipSpaceAndComments() {
        while (Character.isWhitespace(currentChar) || (currentChar == '/' && inspectChar(1) == '*') || (currentChar == '/' && inspectChar(1) == '/')) {
            // skipWhiteSpace
            while (Character.isWhitespace(currentChar)) {
                accept();
            }

            // skipEOFComment
            if (currentChar == '/' && inspectChar(1) == '/') {
                accept();
                accept();
                while (true) {
                    if (currentChar == SourceFile.eof) {
                        errorReporter.reportError(": %", "unterminated comment", sourcePos);
                        return;
                    } else if (currentChar == '\n' || currentChar == '\r') {
                        accept();
                        break;
                    }

                    accept();
                }
            }

            // skipTraditionalComment
            if (currentChar == '/' && inspectChar(1) == '*') {
                accept();
                accept();
                while (true) {
                    if (currentChar == SourceFile.eof) {
                        errorReporter.reportError(": %", "unterminated comment", sourcePos);
                        return;
                    } else if (currentChar == '*' && inspectChar(1) == '/') {
                        accept();
                        accept();
                        break;
                    }

                    accept();
                }
            }
        }
    }

    public Token getToken() {
        Token token;
        int kind;

        // Skip white space and comments and reset thingos
        skipSpaceAndComments();
        currentSpelling = new StringBuilder();
        sourcePos = new SourcePosition();

        kind = nextToken();

        if (kind == Token.EOF) {
            sourcePos = new SourcePosition(line, line, column, column);
        } else if (kind == Token.STRINGLITERAL) {
            // position includes the quotes ever though spelling removes them
            sourcePos = new SourcePosition(line, line, column - currentSpelling.toString().length() - 2, column - 1);
        } else if (currentSpelling.toString().length() == 1) {
            sourcePos = new SourcePosition(line, line, column - 1, column - 1);
        } else {
            sourcePos = new SourcePosition(line, line, column - currentSpelling.toString().length(), column - 1);
        }

        token = new Token(kind, currentSpelling.toString(), sourcePos);

        // * do not remove these three lines below (for debugging purposes)
        if (debug) {
            System.out.println(token);
        }
        return token;
    }
}
