/*
 * SourcePosition.java    
 */

// ====== PLEASE DO NOT MODIFY THIS FILE =====

// This class is used to store the positions of tokens and phrases

package VC.Scanner;

public class SourcePosition {

    public int lineStart, lineFinish;
    public int charStart, charFinish;

    public SourcePosition() {
        this(0, 0, 0, 0);
    }

    // can be called by the parser to store the position of a phrase
    public SourcePosition(int lineStart, int lineFinish) {
        this(lineStart, lineFinish, 0, 0);
    }

    // can be called by the scanner to store the position of a token
    public SourcePosition(int lineStart, int lineFinish, int charStart, int charFinish) {
        this.lineStart = lineStart;
        this.lineFinish = lineFinish;
        this.charStart = charStart;
        this.charFinish = charFinish;
    }

    @Override
    public String toString() {
        return String.format("%d(%d)..%d(%d)", lineStart, charStart, lineFinish, charFinish);
    }
}

