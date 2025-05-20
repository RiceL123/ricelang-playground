package ricelang.VC.Transpile;

import java.util.ArrayList;
import java.util.List;

public class JS {
    private static final List<String> instructions = new ArrayList<>();
    // private static final String indentStr = "  ";
    private static final String indentStr = "\t";
    private static int indent = 0;

    public static void clearInstructions() {
        indent = 0;
        instructions.clear();
    }

    public static void incrementIndent() { indent++; }

    public static void decrementIndent() { indent--; }

    public static void append(String inst) {
        instructions.add(indentStr.repeat(indent) + inst);
    }

    public static String dump() {
        return String.join("\n", instructions);
    }
}
