package ricelang.VC;

import java.util.Optional;

import ricelang.VC.ASTs.AST;
import ricelang.VC.Checker.Checker;
import ricelang.VC.CodeGen.Emitter;
import ricelang.VC.Parser.Parser;
import ricelang.VC.Scanner.Scanner;
import ricelang.VC.Scanner.SourceFile;
import ricelang.VC.Transpile.Transpiler;
import ricelang.VC.TreeMermaid.Mermaid;

public class vc {

    private static Scanner scanner;
    private static ErrorReporter reporter;
    private static Parser parser;
    private static Checker checker;
    private static Emitter emitter;
    private static Transpiler transpiler;
    private static Mermaid mermaid;
    static boolean transpileToJS = false;
    static boolean isVanillaJS = true;

    private static AST theAST;

    public Optional<String> compile(String outputFileBase, String sourceCode, StringBuilder output) {
        output.append("======== The RiceLang Compiler ========\n");
        SourceFile source = new SourceFile(sourceCode);
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

    public Optional<String> mermaidAST(String sourceCode, StringBuilder output, StringBuilder verbose) {
        verbose.append("======== The RiceLang Compiler ========\n");
        SourceFile source = new SourceFile(sourceCode);
        reporter = new ErrorReporter();

        scanner = new Scanner(source, reporter);
        parser = new Parser(scanner, reporter);
        theAST = parser.parseProgram();
        if (reporter.getNumErrors() > 0) {
            return Optional.of(reporter.getAllErrors() + "\n\nCompilation was unsuccessful due to: lexical / syntactic error");
        }
        verbose.append("Pass 1: Lexical and syntactic Analysis\n");

        checker = new Checker(reporter);
        checker.check(theAST);
        if (reporter.getNumErrors() > 0) {
            return Optional.of(reporter.getAllErrors() + "\n\nCompilation was unsuccessful due to: semantic error");
        }
        verbose.append("Pass 2: Semantic Analysis\n");

        mermaid = new Mermaid();
        String mermaidOutput = mermaid.toString(theAST);
        verbose.append("Pass 3: Mermaid AST generation\n");
        output.append(mermaidOutput);

        return Optional.empty();
    }

    public Optional<String> jasminSrc(String sourceCode, StringBuilder output, StringBuilder verbose) {
        verbose.append("======== The RiceLang Compiler ========\n");
        SourceFile source = new SourceFile(sourceCode);
        reporter = new ErrorReporter();

        scanner = new Scanner(source, reporter);
        parser = new Parser(scanner, reporter);
        theAST = parser.parseProgram();
        if (reporter.getNumErrors() > 0) {
            return Optional.of(reporter.getAllErrors() + "\n\nCompilation was unsuccessful due to: lexical / syntactic error");
        }
        verbose.append("Pass 1: Lexical and syntactic Analysis\n");

        checker = new Checker(reporter);
        checker.check(theAST);
        if (reporter.getNumErrors() > 0) {
            return Optional.of(reporter.getAllErrors() + "\n\nCompilation was unsuccessful due to: semantic error");
        }
        verbose.append("Pass 2: Semantic Analysis\n");

        emitter = new Emitter("temp");
        String jasminSrcString = emitter.genString(theAST);
        if (reporter.getNumErrors() > 0) {
            return Optional.of(reporter.getAllErrors() + "\n\nCompilation was unsuccessful due to: jasmin code generation error");
        }
        verbose.append("Pass 3: Code Generation\n");

        output.append(jasminSrcString);

        return Optional.empty();
    }

    public Optional<String> javascriptSrc(String sourceCode, StringBuilder output, StringBuilder verbose, Boolean vanillaJS) {
        verbose.append("======== The RiceLang Compiler ========\n");
        SourceFile source = new SourceFile(sourceCode);
        reporter = new ErrorReporter();

        scanner = new Scanner(source, reporter);
        parser = new Parser(scanner, reporter);
        theAST = parser.parseProgram();
        if (reporter.getNumErrors() > 0) {
            return Optional.of(reporter.getAllErrors() + "\n\nCompilation was unsuccessful due to: lexical / syntactic error");
        }
        verbose.append("Pass 1: Lexical and syntactic Analysis\n");

        checker = new Checker(reporter);
        checker.check(theAST);
        if (reporter.getNumErrors() > 0) {
            return Optional.of(reporter.getAllErrors() + "\n\nCompilation was unsuccessful due to: semantic error");
        }
        verbose.append("Pass 2: Semantic Analysis\n");

        transpiler = new Transpiler(vanillaJS);
        String javascriptSrcString = transpiler.genString(theAST);
        if (reporter.getNumErrors() > 0) {
            return Optional.of(reporter.getAllErrors() + "\n\nCompilation was unsuccessful due to: jasmin code generation error");
        }
        verbose.append("Pass 3: Code Generation\n");

        output.append(javascriptSrcString);

        return Optional.empty();
    }
}
