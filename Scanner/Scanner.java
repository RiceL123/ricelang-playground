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
import java.util.Optional;
import java.util.List;

public final class Scanner {

    private SourceFile sourceFile;
    private ErrorReporter errorReporter;
    private boolean debug;

    private StringBuilder currentSpelling;
    private char currentChar;
    private SourcePosition sourcePos;

    private int startcolumn;
    private int column; // charStart = column - currentSpelling.length()
    private int line;
    // =========================================================

    public Scanner(SourceFile source, ErrorReporter reporter) {
        sourceFile = source;
        errorReporter = reporter;
        debug = false;

        // Initiaise currentChar for the starter code. 
        // Change it if necessary for your full implementation
        currentChar = sourceFile.getNextChar();

        currentSpelling = new StringBuilder();
        // Initialise your counters for counting line and column numbers here
        startcolumn = 1;
        column = 1;
        line = 1;
    }

    public void enableDebugging() {
        debug = true;
    }

    // accept gets the next character from the source program.
    private void accept() {
        currentSpelling.append(currentChar);

        if (currentChar == '\r') {
            if (sourceFile.inspectChar(1) == '\n') {
                sourceFile.getNextChar(); // consume & ignore next char if '\r\n' found
            }
            line += 1;
            column = 1;
        } else if (currentChar == '\n') {
            line += 1;
            column = 1;
        } else {
            column += 1;
        }

     	currentChar = sourceFile.getNextChar();
    }

    // inspectChar returns the n-th character after currentChar in the input stream. 
    // If there are fewer than nthChar characters between currentChar 
    // and the end of file marker, SourceFile.eof is returned.
    // 
    // Both currentChar and the current position in the input stream
    // are *not* changed. Therefore, a subsequent call to accept()
    // will always return the next char after currentChar.

    // That is, inspectChar does not change 

    private char inspectChar(int nthChar) {
        return sourceFile.inspectChar(nthChar);
    }

    private int nextToken() {
        // Tokens: separators, operators, literals, identifiers, and keywords
        switch (currentChar) {
            // Handle separators
            case '(': accept(); return Token.LPAREN;
            case ')': accept(); return Token.RPAREN;
            case '{': accept(); return Token.LCURLY;
            case '}': accept(); return Token.RCURLY;
            case '[': accept(); return Token.LBRACKET;
            case ']': accept(); return Token.RBRACKET;
            case ';': accept(); return Token.SEMICOLON;
            case ',': accept(); return Token.COMMA;
            // Handle operators
            case '+': accept(); return Token.PLUS;
            case '-': accept(); return Token.MINUS;
            case '*': accept(); return Token.MULT;
            case '/': accept(); return Token.DIV;
            case '=': accept();
                if (currentChar == '=') { accept(); return Token.EQEQ; }
                return Token.EQ;
            case '!': accept();
                if (currentChar == '=') { accept(); return Token.NOTEQ; }
                return Token.NOT;
            case '<': accept();
                if (currentChar == '=') { accept(); return Token.LTEQ; }
                return Token.LT;
            case '>': accept();
                if (currentChar == '=') { accept(); return Token.GTEQ; }
                return Token.GT;
            case '&': accept();
                if (currentChar == '&') { accept(); return Token.ANDAND; }
                return Token.ERROR; // there is no bitwise & in VC; only &&
            case '|': accept();
                if (currentChar == '|') { accept(); return Token.OROR; }
                return Token.ERROR; // there is no bitwise | in VC; only ||

            // Handle separators
            case SourceFile.eof:
                currentSpelling.append(Token.spell(Token.EOF));
                return Token.EOF;

            default:
                break;
        }

        // Handle identifiers and nuemric literals
        // ...
        if (currentChar == '.' && Character.isDigit(inspectChar(1))) {
            // could be a fraction
            accept();
            while (Character.isDigit(currentChar)) {
                accept();
            }
            return Token.FLOATLITERAL;

        } else if (Character.isDigit(currentChar)) {
            // try to make a int-literal / or float literal

        } else if (currentChar == '\"') {
            Optional<String> errorMessage = Optional.empty();
            accept();
            while (true) {
                if (currentChar == sourceFile.eof || currentChar == '\n' || currentChar == '\r' ) {
                    // currentSpelling = new StringBuilder(currentSpelling.substring(1, currentSpelling.length()));
                    // errorReporter.reportError(currentSpelling.toString() + ": %", "unterminated string", new SourcePosition(line, line, startcolumn, startcolumn));
                    // return Token.ERROR;
                    errorMessage = Optional.of("unterminated string");
                    break;
                }

                if (currentChar == '\\') {
                    accept(); 
                    if (!List.of('b', 'f', 'n', 'r', 't', '\'', '\"', '\\').contains(currentChar)) {
                        // currentSpelling = new StringBuilder(currentSpelling.substring(1, currentSpelling.length()));
                        errorMessage = Optional.of("illegal escape character");
                    }
                    accept();
                    continue;
                }

                if (currentChar == '"') {
                    break;
                }

                accept();
            }
            accept();
            // ignore "" around the word
            currentSpelling = new StringBuilder(currentSpelling.substring(1, currentSpelling.length() - 1));

            if (errorMessage.isPresent()) {
                errorReporter.reportError(currentSpelling.toString() + ": %", errorMessage.get(), new SourcePosition(line, line, startcolumn, startcolumn));
                return Token.ERROR;
            } else {
                return Token.STRINGLITERAL;
            }
        } else if (Character.isLetter(currentChar)) {
            // identifier -> letter (letter | digit)*
            while (Character.isLetter(currentChar) || Character.isDigit(currentChar)) {
                accept();
            }
            switch (currentSpelling.toString()) {
                case "boolean": return Token.BOOLEAN;
                case "break": return Token.BREAK;
                case "continue": return Token.CONTINUE;
                case "else": return Token.ELSE;
                case "float": return Token.FLOAT;
                case "for": return Token.FOR;
                case "if": return Token.IF;
                case "int": return Token.INT;
                case "return": return Token.RETURN;
                case "void": return Token.VOID;
                case "while": return Token.WHILE;
                case "true": case "false": return Token.BOOLEANLITERAL;
                default: return Token.ID;
            }
        }

        accept();
        return Token.ERROR;
    }


    private Optional<String> skipSpaceAndComments() {
        while (Character.isWhitespace(currentChar) || currentChar == '/') {
            if (currentChar == '/') {
                startcolumn = column;
            }

            if (currentChar == '/' && (inspectChar(1) != '/' && inspectChar(1) != '*')) {
                // this is just a normal division
                return Optional.empty();
            } else if (currentChar == '/' && inspectChar(1) == '/') {
                // end of line comments keep ignoring till termination
                while (true) {
                    if (inspectChar(1) == sourceFile.eof) { return Optional.of(": unterminated comment"); }
                    // System.out.println("skiping end of line comment" + currentChar);
                    if (currentChar == '\n' || currentChar == '\r') {
                        break;
                    }
                    accept();
                }
                accept(); // consume line terminator
            }
            else if (currentChar == '/' && inspectChar(1) == '*') {
                // multiline comments
                while (true) {
                    if (inspectChar(1) == sourceFile.eof) { return Optional.of(": unterminated comment"); }
                    // System.out.println("skiping multiline comment" + currentChar);
                    if (currentChar == '*' && inspectChar(1) == '/') {
                        break;
                    }
                    accept();
                }
                accept();
                accept(); // consume */
            } else {
                // white spaces
                // System.out.println("skiping white space" + currentChar);
                accept();
            }
        }

        return Optional.empty();
    }

    public Token getToken() {
        Token token;
        int kind;

        // Skip white space and comments
        Optional<String> skipError = skipSpaceAndComments();
        if (skipError.isPresent()) {
            // System.out.println("wow there was an error" + skipError);
            // return new Token(Token.ERROR, skipError.get(), sourcePos);
            errorReporter.reportError(skipError.get(), "", new SourcePosition(line, line, startcolumn, startcolumn));
            return new Token(Token.ERROR, skipError.get(), sourcePos);
        }

        currentSpelling = new StringBuilder();

        // You need to record the position of the current token somehow
	
        kind = nextToken();

        if (kind == Token.EOF || kind == Token.ERROR || currentSpelling.length() == 1) {
            sourcePos = new SourcePosition(line, line, column, column);
        } else {
            sourcePos = new SourcePosition(line, line, column - currentSpelling.length(), column);
        }

        token = new Token(kind, currentSpelling.toString(), sourcePos);

   	// * do not remove these three lines below (for debugging purposes)
        if (debug) {
            System.out.println(token);
        }

        startcolumn = column;
        return token;
    }
}
