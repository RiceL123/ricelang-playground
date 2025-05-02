/*
 * vc.java           
 *
 * For Assigment 2
 * 
 */

package VC;

import VC.Recogniser.Recogniser;
import VC.Scanner.Scanner;
import VC.Scanner.SourceFile;

public class vc {

    private static Scanner scanner;
    private static ErrorReporter reporter;
    private static Recogniser recogniser;

    private static String inputFilename; 

    public static void main(String[] args) {
        if (args.length != 1) {
          System.out.println("Usage: java VC.vc filename\n");
          System.exit(1); 
        } else
           inputFilename = args[0];

        System.out.println("======= The VC compiler =======");

        SourceFile source = new SourceFile(inputFilename);

        reporter = new ErrorReporter();
        scanner  = new Scanner(source, reporter);
        recogniser = new Recogniser(scanner, reporter);

        recogniser.parseProgram();

        if (reporter.getNumErrors() == 0)
           System.out.println ("Compilation was successful.");
        else
           System.out.println ("Compilation was unsuccessful.");
    }
}
