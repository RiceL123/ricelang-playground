package ricelang.server;

public class SourceCodeBody {
    private String sourceCode;

    public SourceCodeBody(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String toString() {
        return sourceCode;
    }
}
