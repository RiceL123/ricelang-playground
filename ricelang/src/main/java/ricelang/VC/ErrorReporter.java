/*
 * ErrorReporter.java     
 */

package ricelang.VC;

import java.util.ArrayList;
import java.util.List;

import ricelang.VC.Scanner.SourcePosition;

public class ErrorReporter {

    private List<String> errors;

    public ErrorReporter() {
        errors = new ArrayList<String>();
    }

    /**
     * Reports an error message with the specified details.
     * 
     * @param message   The error message template
     * @param tokenName The token causing the error
     * @param pos       The position of the error in the source file
     */
    public void reportError(String message, String tokenName, SourcePosition pos) {
        StringBuilder sb = new StringBuilder();

        sb.append("ERROR: ")
                .append(pos.lineStart).append("(").append(pos.charStart).append(")..").append(pos.lineFinish)
                .append("(").append(pos.charFinish).append("): ");

        for (int i = 0; i < message.length(); i++) {
            char currentChar = message.charAt(i);
            if (currentChar == '%') {
                sb.append(tokenName);
            } else {
                sb.append(currentChar);
            }
        }

        String error = sb.toString();

        errors.add(error);
    }

    public int getNumErrors() {
        return errors.size();
    }

    public String getAllErrors() {
        return String.join("\n", errors);
    }
}
