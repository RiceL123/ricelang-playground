package ricelang.VC.CodeGen;

import java.io.PrintWriter;

public final class Instruction {
    private final String assembly;

    public Instruction(String assembly) {
        this.assembly = assembly;
    }
    
    public void write(PrintWriter writer) {
        if (!(assembly.startsWith(".") || assembly.endsWith(":"))) {
            writer.print("\t");
        }
        writer.println(assembly);
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
