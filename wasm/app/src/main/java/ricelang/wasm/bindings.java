package ricelang.wasm;

import org.teavm.jso.JSExport;

import ricelang.VC.vc;

import java.util.Optional;

public final class bindings {
    private bindings() {}

    @JSExport
    public static Output getMermaid(String sourceCode) {
        return runCompiler(sourceCode, (v, out, verbose) -> v.mermaidAST(sourceCode, out, verbose));
    }

    @JSExport
    public static Output getJasmin(String sourceCode) {
        return runCompiler(sourceCode, (v, out, verbose) -> v.jasminSrc(sourceCode, out, verbose));
    }

    @JSExport
    public static Output getVanillaJS(String sourceCode) {
        return runCompiler(sourceCode, (v, out, verbose) -> v.javascriptSrc(sourceCode, out, verbose, true));
    }

    @JSExport
    public static Output getNodeJS(String sourceCode) {
        return runCompiler(sourceCode, (v, out, verbose) -> v.javascriptSrc(sourceCode, out, verbose, false));
    }

    private static Output runCompiler(String sourceCode, TriFunction<vc, StringBuilder, StringBuilder, Optional<String>> action) {
        StringBuilder output = new StringBuilder();
        StringBuilder verbose = new StringBuilder();
        vc compiler = new vc();
        Optional<String> error = action.apply(compiler, output, verbose);

        boolean isError = error.isPresent();
        String result = isError ? error.get() : output.toString();
        return Output.create(result, verbose.toString(), isError);
    }

    @FunctionalInterface
    private interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }
}
