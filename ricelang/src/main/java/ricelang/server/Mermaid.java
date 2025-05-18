package ricelang.server;

public record Mermaid(String mermaidSrc, String verbose, String errors) {
    public Mermaid(String mermaidSrc, String verbose) {
        this(mermaidSrc, verbose, null);
    }
}
