package ricelang.server;

public class SourceCodeBody {
    private String sourceCode;
    private boolean generateASTImage;
    private boolean generateJasmin;
    private String stdin;

    public SourceCodeBody(String sourceCode) {
        this.sourceCode = sourceCode;
        this.generateASTImage = false;
        this.generateJasmin = false;
        this.stdin = null;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public boolean isGenerateASTImage() {
        return generateASTImage;
    }

    public void setGenerateASTImage(boolean generateASTImage) {
        this.generateASTImage = generateASTImage;
    }

    public boolean isGenerateJasmin() {
        return generateJasmin;
    }

    public void setGenerateJasmin(boolean generateJasmin) {
        this.generateJasmin = generateJasmin;
    }

    public String getStdin() {
        return stdin;
    }

    public void setStdin(String stdin) {
        this.stdin = stdin;
    }

    public String toString() {
        return sourceCode;
    }
}
