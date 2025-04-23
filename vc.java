/**
 * vc.java       
 * 
 **/

package VC;

import VC.Scanner.Scanner;
import VC.Scanner.SourceFile;
import VC.Scanner.Token;

public class vc {

    private static Scanner scanner;
    private static ErrorReporter reporter;
    private static Token currentToken;
    private static String inputFilename; 

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please provide the source file as an argument.");
            System.exit(1);
        }

        inputFilename = args[0];
        System.out.println("======= The VC compiler =======");

        // Initialise the source file and error reporter
        SourceFile source = new SourceFile(inputFilename);
        reporter = new ErrorReporter();
        scanner  = new Scanner(source, reporter);
        scanner.enableDebugging();

        do 
	  currentToken = scanner.getToken();
        while (currentToken.kind != Token.EOF);
    }
}
