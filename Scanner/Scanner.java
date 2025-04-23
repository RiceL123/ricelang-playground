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

    // =========================================================

    public Scanner(SourceFile source, ErrorReporter reporter) {
        sourceFile = source;
        errorReporter = reporter;
        debug = false;

	// Initiaise currentChar for the starter code. 
	// Change it if necessary for your full implementation
	currentChar = sourceFile.getNextChar();

        // Initialise your counters for counting line and column numbers here
    }

    public void enableDebugging() {
        debug = true;
    }

    // accept gets the next character from the source program.
    private void accept() {

     	currentChar = sourceFile.getNextChar();

  	// You may save the lexeme of the current token incrementally here
  	// You may also increment your line and column counters here
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
            case '(':
                accept();
                return Token.LPAREN;
            // ...
            case '.':
       	    //  Handle floats (by calling auxiliary functions)

            // Handle separators
            case '|':
                accept();
                if (currentChar == '|') {
                    accept();
                    return Token.OROR;
                } else {
                    return Token.ERROR;
		}  
	    // ...
            case SourceFile.eof:
                currentSpelling.append(Token.spell(Token.EOF));
                return Token.EOF;

            default:
                break;
        }

        // Handle identifiers and nuemric literals
        // ...

        accept();
        return Token.ERROR;
    }

    private void skipSpaceAndComments() {
	// ...
    }

    public Token getToken() {
        Token token;
        int kind;

        // Skip white space and comments
        skipSpaceAndComments();

        currentSpelling = new StringBuilder();

        sourcePos = new SourcePosition();

        // You need to record the position of the current token somehow
	
        kind = nextToken();

        token = new Token(kind, currentSpelling.toString(), sourcePos);

   	// * do not remove these three lines below (for debugging purposes)
        if (debug) {
            System.out.println(token);
        }
        return token;
    }
}