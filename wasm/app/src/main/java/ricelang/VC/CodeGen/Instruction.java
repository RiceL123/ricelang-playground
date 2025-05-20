package ricelang.VC.CodeGen;

public final class Instruction {
    private final String assembly;

    public Instruction(String assembly) {
        this.assembly = assembly;
    }
    
    public String read() {
        String s = "";

        if (!(assembly.startsWith(".") || assembly.endsWith(":"))) {
            s = "\t";
        }

        s += assembly;
        return s;
    }
}
