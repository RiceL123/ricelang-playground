package ricelang.wasm;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public abstract class Output implements JSObject {
    @JSBody(params = { "output", "verbose", "error" }, script = """
        return {
            output: output,
            verbose: verbose,
            error: error
        };
    """)
    public static native Output create(String output, String verbose, boolean error);

    public abstract String getOutput();
    public abstract String getVerbose();
    public abstract boolean getError();
}
