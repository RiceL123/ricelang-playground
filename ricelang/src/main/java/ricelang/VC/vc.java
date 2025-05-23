package ricelang.VC;

import java.util.Optional;

import ricelang.VC.ASTs.AST;
import ricelang.VC.Checker.Checker;
import ricelang.VC.CodeGen.Emitter;
import ricelang.VC.Parser.Parser;
import ricelang.VC.Scanner.Scanner;
import ricelang.VC.Scanner.SourceFile;

public class vc {

    private static Scanner scanner;
    private static ErrorReporter reporter;
    private static Parser parser;
    private static Checker checker;
    private static Emitter emitter;
    static boolean transpileToJS = false;
    static boolean isVanillaJS = true;

    private static AST theAST;

    public Optional<String> compile(String outputFileBase, String sourceCode, StringBuilder output) {
        output.append("======== The RiceLang Compiler ========\n");
        SourceFile source = new SourceFile(sourceCode, true);
        reporter = new ErrorReporter();

        scanner = new Scanner(source, reporter);
        parser = new Parser(scanner, reporter);
        theAST = parser.parseProgram();
        if (reporter.getNumErrors() > 0) {
            return Optional.of(reporter.getAllErrors() + "\n\nCompilation was unsuccessful due to: lexical / syntactic error");
        }
        output.append("Pass 1: Lexical and syntactic Analysis\n");

        checker = new Checker(reporter);
        checker.check(theAST);
        if (reporter.getNumErrors() > 0) {
            return Optional.of(reporter.getAllErrors() + "\n\nCompilation was unsuccessful due to: semantic error");
        }
        output.append("Pass 2: Semantic Analysis\n");

        emitter = new Emitter(outputFileBase, reporter);
        emitter.gen(theAST);
        if (reporter.getNumErrors() > 0) {
            return Optional.of(reporter.getAllErrors() + "\n\nCompilation was unsuccessful due to: jasmin code generation error");
        }
        output.append("Pass 3: Code Generation\n");

        return Optional.empty();
    }

}
