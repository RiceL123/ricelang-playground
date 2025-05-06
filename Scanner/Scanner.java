/*
 * Scanner.java
 */
package VC.Scanner;

import VC.ErrorReporter;
import java.util.Optional;

public final class Scanner {

    private SourceFile sourceFile;
    private ErrorReporter errorReporter;
    private boolean debug;

    private StringBuilder currentSpelling;
    private int spellingLengthBonus;
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
        spellingLengthBonus = 0;

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

    private void reject() {
        column += 1;
        currentChar = sourceFile.getNextChar();
    }

    private boolean isFraction() {
        return currentChar == '.' && Character.isDigit(inspectChar(1));
    }

    private boolean isExponent() {
        char nextChar = inspectChar(1);
        return (currentChar == 'E' || currentChar == 'e') && (Character.isDigit(nextChar) || ((nextChar == '+' || nextChar == '-') && Character.isDigit(inspectChar(2))));
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

    private Token nextToken() {
        // Tokens: separators, operators, literals, identifiers, and keywords
        switch (currentChar) {
            case '(' -> {
                accept();
                return new Token(Token.LPAREN, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case ')' -> {
                accept();
                return new Token(Token.RPAREN, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case '{' -> {
                accept();
                return new Token(Token.LCURLY, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case '}' -> {
                accept();
                return new Token(Token.RCURLY, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case '[' -> {
                accept();
                return new Token(Token.LBRACKET, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case ']' -> {
                accept();
                return new Token(Token.RBRACKET, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case ';' -> {
                accept();
                return new Token(Token.SEMICOLON, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case ',' -> {
                accept();
                return new Token(Token.COMMA, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case '+' -> {
                accept();
                return new Token(Token.PLUS, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case '-' -> {
                accept();
                return new Token(Token.MINUS, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case '*' -> {
                accept();
                return new Token(Token.MULT, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case '/' -> {
                accept();
                return new Token(Token.DIV, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case '=' -> {
                accept();
                if (currentChar == '=') {
                    accept();
                    return new Token(Token.EQEQ, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                }
                return new Token(Token.EQ, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case '!' -> {
                accept();
                if (currentChar == '=') {
                    accept();
                    return new Token(Token.NOTEQ, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                }
                return new Token(Token.NOT, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case '<' -> {
                accept();
                if (currentChar == '=') {
                    accept();
                    return new Token(Token.LTEQ, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                }
                return new Token(Token.LT, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case '>' -> {
                accept();
                if (currentChar == '=') {
                    accept();
                    return new Token(Token.GTEQ, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                }
                return new Token(Token.GT, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            }
            case '&' -> {
                accept();
                if (currentChar == '&') {
                    accept();
                    return new Token(Token.ANDAND, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                }
                return new Token(Token.ERROR, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1)); // there is no bitwise & in VC; only &&
            }
            case '|' -> {
                accept();
                if (currentChar == '|') {
                    accept();
                    return new Token(Token.OROR, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                }
                return new Token(Token.ERROR, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1)); // there is no bitwise | in VC; only ||
            }
            case SourceFile.eof -> {
                currentSpelling.append(Token.spell(Token.EOF));
                return new Token(Token.EOF, currentSpelling.toString(), new SourcePosition(line, line, column, column));
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

            return new Token(Token.FLOATLITERAL, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
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
                    return new Token(Token.FLOATLITERAL, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                } else if (isExponent()) {
                    // skip the (E|e) and the (+|-)?digit then read rest of digit*
                    accept();
                    accept();
                    while (Character.isDigit(currentChar)) {
                        accept();
                    }
                    return new Token(Token.FLOATLITERAL, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
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
                    return new Token(Token.FLOATLITERAL, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                } else if (Character.isDigit(currentChar)) {
                    accept();
                } else {
                    return new Token(Token.INTLITERAL, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                }
            }
        } else if (currentChar == '\"') {
            accept();
            while (true) {
                if (currentChar == '\n' || currentChar == '\r') {
                    // strings do not span multiple lines
                    currentSpelling = new StringBuilder(currentSpelling.toString().replaceFirst("^\"", "")); // remove first and last " 
                    errorReporter.reportError("%: unterminated string", currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length() - 1, column - currentSpelling.length() - 1));
                    int temp_line = line;
                    int temp_column = column;
                    reject();
                    column = 1;
                    line += 1;
                    // return Token.STRINGLITERAL;
                    return new Token(Token.STRINGLITERAL, currentSpelling.toString(), new SourcePosition(temp_line, temp_line, temp_column - currentSpelling.length() - 1 - spellingLengthBonus, temp_column - 1));
                } else if (currentChar == '\\') {
                    char nextChar = inspectChar(1);
                    Optional<Character> special = switch (nextChar) {
                        case 'b' ->
                            Optional.of('\b');
                        case 'f' ->
                            Optional.of('\f');
                        case 'n' ->
                            Optional.of('\n');
                        case 'r' ->
                            Optional.of('\r');
                        case 't' ->
                            Optional.of('\t');
                        case '\'' ->
                            Optional.of('\'');
                        case '\"' ->
                            Optional.of('\"');
                        case '\\' ->
                            Optional.of('\\');
                        default ->
                            Optional.empty();
                    };

                    // convert to desired special character and accept without accepting
                    if (special.isPresent()) {
                        currentSpelling.append(special.get());
                        reject();
                        reject();
                        spellingLengthBonus += 1;
                    } else {
                        errorReporter.reportError("%: illegal escape character", "\\" + nextChar, new SourcePosition(line, line, column - currentSpelling.length(), column));
                        accept();
                        accept();
                    }
                } else if (currentChar == '\"') {
                    break;
                } else {
                    accept();
                }

            }
            accept();
            currentSpelling = new StringBuilder(currentSpelling.toString().replaceFirst("^\"", "").replaceFirst("\"$", "")); // remove first and last "

            // althought the first and last "" have disappeared, they still needa be there for position calc 
            return new Token(Token.STRINGLITERAL, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length() - 2 - spellingLengthBonus, column - 1));
        } else if (Character.isLetter(currentChar) || currentChar == '_') {
            // identifier -> letter (letter | digit)*
            while (Character.isLetter(currentChar) || currentChar == '_' || Character.isDigit(currentChar)) {
                accept();
            }
            return switch (currentSpelling.toString()) {
                case "boolean" ->
                    new Token(Token.BOOLEAN, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                case "break" ->
                    new Token(Token.BREAK, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                case "continue" ->
                    new Token(Token.CONTINUE, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                case "else" ->
                    new Token(Token.ELSE, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                case "float" ->
                    new Token(Token.FLOAT, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                case "for" ->
                    new Token(Token.FOR, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                case "if" ->
                    new Token(Token.IF, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                case "int" ->
                    new Token(Token.INT, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                case "return" ->
                    new Token(Token.RETURN, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                case "void" ->
                    new Token(Token.VOID, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                case "while" ->
                    new Token(Token.WHILE, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                case "true", "false" ->
                    new Token(Token.BOOLEANLITERAL, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
                default ->
                    new Token(Token.ID, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
            };
        }

        accept();
        return new Token(Token.ERROR, currentSpelling.toString(), new SourcePosition(line, line, column - currentSpelling.length(), column - 1));
    }

    private void skipSpaceAndComments() {
        while (Character.isWhitespace(currentChar) || (currentChar == '/' && inspectChar(1) == '*') || (currentChar == '/' && inspectChar(1) == '/')) {
            // skip tab but make sure it multiple of 8 aligned (A tab size of 8 characters is assumed)
            while (currentChar == '\t') {
                accept();
                for (int i = column; i % 8 != 0; i++) {
                    column++;
                }
                column++;
            }

            // skipWhiteSpace
            while (Character.isWhitespace(currentChar)) {
                if (currentChar == '\t') {
                    break;
                }
                accept();
            }

            // skipEOFComment
            if (currentChar == '/' && inspectChar(1) == '/') {
                accept();
                accept();
                while (true) {
                    if (currentChar == SourceFile.eof) {
                        errorReporter.reportError(": %", "unterminated comment (please ensure \\n at end of file)", sourcePos);
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
                        errorReporter.reportError(": %", "unterminated comment (please ensure \\n at end of file)", sourcePos);
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

        // Skip white space and comments and reset thingos
        skipSpaceAndComments();
        currentSpelling = new StringBuilder();
        spellingLengthBonus = 0;
        sourcePos = new SourcePosition();

        token = nextToken();

        // * do not remove these three lines below (for debugging purposes)
        if (debug) {
            System.out.println(token);
        }
        return token;
    }
}
