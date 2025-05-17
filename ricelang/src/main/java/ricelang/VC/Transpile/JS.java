package ricelang.VC.Transpile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
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

    public static void dump(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(filename))) {
            for (String inst : instructions) {
                writer.append(inst + "\n");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error opening object file: " + filename, e);
        }
    }

    public static String dump() {
        return String.join("\n", instructions);
    }
}
