import { atom } from "jotai";
import { toast } from "sonner";
import examples from "@/lib/examples.json";
import { teavmAtom, WasmOutput } from "../app/components/TeamVM";
import { BACKEND_URL } from "./utils";

export const actions: Record<
  string,
  {
    route:
      | "/run"
      | "/run/legacy"
      | "/ast"
      | "/jasmin"
      | "/javascript"
      | "/nodejs";
    desc: string;
  }
> = {
  Run: {
    route: "/run",
    desc: "Transpile to vanilla JavaScript and run it in the browser",
  },
  "Run (Legacy)": {
    route: "/run/legacy",
    desc: "Compile the code to Java byte code and run it on the JVM",
  },
  "Draw AST": {
    route: "/ast",
    desc: "Generate a visual representation of the abstract syntax tree",
  },
  Compile: {
    route: "/jasmin",
    desc: "Compile to Jasmin assembler (assembly like) code",
  },
  "Transpile JS": {
    route: "/javascript",
    desc: "Transpile to vanilla JavaScript",
  },
  "Transpile NodeJS": {
    route: "/nodejs",
    desc: "Transpile to NodeJS",
  },
};

async function getLegacyOutput(
  route: string,
  code: string,
  start: number,
  isAST = false,
): Promise<[{ output: string; verbose: string; isAST: boolean }, boolean]> {
  let result;
  let output;
  let error;
  try {
    const res = await fetch(`${BACKEND_URL}${route}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ sourceCode: code }),
    });

    if (!res.ok) throw new Error(String(res));

    result = await res.json();
    output = {
      output: result.output,
      verbose:
        result.verbose +
        `\nCompleted in ${(performance.now() - start).toFixed(2)} ms`,
      isAST,
    };
    error = result.error;
  } catch (e) {
    output = {
      output: `Error with fetch to ${BACKEND_URL}${route}`,
      verbose: "Network error" + String(e),
      isAST: false,
    };
    error = true;
  }
  return [output, error];
}

// Loading
export const loadingAtom = atom(false);

// Source Code
export const sourceCodeAtom = atom(
  Object.values(examples)[
    Math.floor(Math.random() * Object.keys(examples).length)
  ],
);

export const writeSourceCodeAtom = atom(null, (_get, set, sourceCode: string) =>
  set(sourceCodeAtom, sourceCode),
);

// Output
export const outputAtom = atom({
  output: "press Ctrl+S or the Compile button to compile the code!!",
  verbose: "",
  isAST: false,
});

export const readOutputAtom = atom((get) => get(outputAtom));

export const writeOutputAtom = atom(
  null,
  async (get, set, action: (typeof actions)[keyof typeof actions]["route"]) => {
    toast.promise(
      (async () => {
        const start = performance.now();
        set(loadingAtom, true);

        const wasm = get(teavmAtom);
        const code = get(sourceCodeAtom);

        if (action == "/run/legacy") {
          const [output, error] = await getLegacyOutput("/run", code, start);
          set(outputAtom, output);
          if (error) throw new Error();
          return;
        }

        if (!wasm) {
          const [output, error] = await getLegacyOutput(action, code, start);
          set(outputAtom, output);
          if (error) throw new Error();
          return;
        }

        let result: WasmOutput;
        let isAST = false;

        switch (action) {
          case "/jasmin":
            result = wasm.getJasmin(code);
            break;
          case "/javascript":
            result = wasm.getVanillaJS(code);
            break;
          case "/nodejs":
            result = wasm.getNodeJS(code);
            break;
          case "/ast":
            result = wasm.getMermaid(code);
            if (result.error) break;
            isAST = true;
            break;
          case "/run":
            result = wasm.getVanillaJS(code);
            if (result.error) break;

            // result.verbose += result.output

            // result.output will always have a `console.log(stdout.join('\n'));`

            const n =
              result.output.length - "console.log(stdout.join('\\n'));".length;
            result.output = Function(
              result.output.slice(0, n) + "\n return stdout.join('\\n');",
            )();

            // result.output = Function(result.output.replace("console.log(stdout.join('\\n'));", "\nreturn stdout.join('\\n');"))();

            break;
          default:
            result = { output: "Unknown action", verbose: "", error: true };
        }

        set(outputAtom, {
          output: result.output,
          verbose:
            result.verbose +
            `\nCompleted in ${(performance.now() - start).toFixed(2)} ms`,
          isAST,
        });

        if (result.error) throw new Error();
      })().finally(() => set(loadingAtom, false)),
      {
        loading: `Compiling ${action == "/run/legacy" || !get(teavmAtom) ? "Spring Boot" : "Wasm"}: ${action}...`,
        success: `Completed: ${action}`,
        error: `Error: ${action}`,
      },
    );
  },
);
