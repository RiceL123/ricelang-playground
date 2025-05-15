package ricelang.VC.CodeGen;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class JVM {
    // Constants
    public static final int MAX_BYTE = 255;
    public static final int MAX_SHORT = 65535;
    public static final int MAX_LOCALVARINDEX = MAX_SHORT;
    public static final int MAX_OPSTACK = MAX_SHORT;

    // Directives
    public static final String SOURCE = ".source";
    public static final String CLASS = ".class";
    public static final String STATIC_FIELD = ".field static";
    public static final String LIMIT = ".limit";
    public static final String METHOD_START = ".method";
    public static final String METHOD_END = ".end";
    public static final String SUPER = ".super";
    public static final String VAR = ".var";
    public static final String LINE = ".line";

    // Array instructions

    /* Add the array instructions used yourself */
    public static final String NEWARRAY = "newarray";
    public static final String IASTORE = "iastore";
    public static final String IALOAD = "iaload";
    public static final String FASTORE = "fastore";
    public static final String FALOAD = "faload";
    public static final String BASTORE = "bastore";
    public static final String BALOAD = "baload";

    // Arithmetic instructions
    public static final String FADD = "fadd";
    public static final String IADD = "iadd";
    public static final String FSUB = "fsub";
    public static final String ISUB = "isub";
    public static final String FMUL = "fmul";
    public static final String IMUL = "imul";
    public static final String FDIV = "fdiv";
    public static final String IDIV = "idiv";
    public static final String FCMPG = "fcmpg";
    public static final String FCMPL = "fcmpl";
    public static final String FNEG = "fneg";
    public static final String INEG = "ineg";
    public static final String IXOR = "ixor";

    // Loading and storing
    public static final String GETSTATIC = "getstatic";
    public static final String PUTSTATIC = "putstatic";
    public static final String ALOAD = "aload";
    public static final String ALOAD_0 = "aload_0";
    public static final String ALOAD_1 = "aload_1";
    public static final String ALOAD_2 = "aload_2";
    public static final String ALOAD_3 = "aload_3";
    public static final String ILOAD = "iload";
    public static final String ILOAD_0 = "iload_0";
    public static final String ILOAD_1 = "iload_1";
    public static final String ILOAD_2 = "iload_2";
    public static final String ILOAD_3 = "iload_3";
    public static final String FLOAD = "fload";
    public static final String FLOAD_0 = "fload_0";
    public static final String FLOAD_1 = "fload_1";
    public static final String FLOAD_2 = "fload_2";
    public static final String FLOAD_3 = "fload_3";
    public static final String ASTORE = "astore";
    public static final String ASTORE_0 = "astore_0";
    public static final String ASTORE_1 = "astore_1";
    public static final String ASTORE_2 = "astore_2";
    public static final String ASTORE_3 = "astore_3";
    public static final String FSTORE = "fstore";
    public static final String FSTORE_0 = "fstore_0";
    public static final String FSTORE_1 = "fstore_1";
    public static final String FSTORE_2 = "fstore_2";
    public static final String FSTORE_3 = "fstore_3";
    public static final String ISTORE = "istore";
    public static final String ISTORE_0 = "istore_0";
    public static final String ISTORE_1 = "istore_1";
    public static final String ISTORE_2 = "istore_2";
    public static final String ISTORE_3 = "istore_3";

    // Constants
    public static final String ICONST = "iconst";
    public static final String ICONST_M1 = "iconst_m1";
    public static final String ICONST_0 = "iconst_0";
    public static final String ICONST_1 = "iconst_1";
    public static final String ICONST_2 = "iconst_2";
    public static final String ICONST_3 = "iconst_3";
    public static final String ICONST_4 = "iconst_4";
    public static final String ICONST_5 = "iconst_5";
    public static final String FCONST_0 = "fconst_0";
    public static final String FCONST_1 = "fconst_1";
    public static final String FCONST_2 = "fconst_2";
    public static final String BIPUSH = "bipush";
    public static final String SIPUSH = "sipush";
    public static final String LDC = "ldc";

    // Method invocation
    public static final String INVOKESTATIC = "invokestatic";
    public static final String INVOKESPECIAL = "invokespecial";
    public static final String INVOKEVIRTUAL = "invokevirtual";
    public static final String FRETURN = "freturn";
    public static final String IRETURN = "ireturn";
    public static final String RETURN = "return";

    // Control transfer
    public static final String GOTO = "goto";
    public static final String IFEQ = "ifeq";
    public static final String IFNE = "ifne";
    public static final String IFLE = "ifle";
    public static final String IFLT = "iflt";
    public static final String IFGE = "ifge";
    public static final String IFGT = "ifgt";
    public static final String IF_ICMPEQ = "if_icmpeq";
    public static final String IF_ICMPNE = "if_icmpne";
    public static final String IF_ICMPLE = "if_icmple";
    public static final String IF_ICMPLT = "if_icmplt";
    public static final String IF_ICMPGE = "if_icmpge";
    public static final String IF_ICMPGT = "if_icmpgt";

    // Type conversion
    public static final String I2F = "i2f";

    // Object creation
    public static final String NEW = "new";

    // Stack management
    public static final String DUP_X2 = "dup_x2";
    public static final String DUP = "dup";
    public static final String POP = "pop";
    public static final String NOP = "nop";

    private static final List<Instruction> instructions = new ArrayList<>();
    // private static int nextInstAddr = 0;

    public static void clearInstructions() {
        instructions.clear();
    }

    public static void append(Instruction inst) {
        instructions.add(inst);
    }

    public static void dump(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(filename))) {
            for (Instruction inst : instructions) {
                inst.write(writer);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error opening object file: " + filename, e);
        }
    }

    public static String dump() {
        String s = "";
        for (Instruction inst : instructions) {
            s += inst.read() + "\n";
        }

        return s;
    }
}
