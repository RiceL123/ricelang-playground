"use client"
import { useRef, useState, useCallback, useMemo } from "react";
import { useTeaVM } from './components/TeaVMProvider';
import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from "@/components/ui/resizable"

import Navbar, { examples } from "./components/NavbarHome";
import Output from "./components/Output";
import CodeEditor from "./components/CodeEditor";

const backendUrl = "https://ricelang-playground.onrender.com";
// const backendUrl = "http://127.0.0.1:8080"

const actions: Record<string, { route: string, desc: string }> = {
  "Run": {
    route: "/run",
    desc: "Transpile to vanilla JavaScript and run it in the browser",
  },
  "Run (Legacy)": {
    route: "/run/legacy",
    desc: "Compile the code to Java byte code and run it on the JVM",
  },
  "Draw AST": {
    route: "/ast",
    desc: "Generate a visual representation of the abstract syntax tree"
  },
  "Compile": {
    route: "/jasmin",
    desc: "Compile to Jasmin assembler (assembly like) code"
  },
  "Transpile JS": {
    route: "/javascript",
    desc: "Transpile to vanilla JavaScript"
  },
  "Transpile NodeJS": {
    route: "/nodejs",
    desc: "Transpile to NodeJS"
  },
}

export default function Home() {
  const { teavm } = useTeaVM();
  const [loading, setLoading] = useState(false);
  const [output, setOutput] = useState({
    output: "press Ctrl+S or the Compile button to compile the code!!",
    verbose: "",
    isAST: false
  });

  const memoizedOutput = useMemo(() => output, [output]);

  const [sourceCode, _setSourceCode] = useState(Object.values(examples)[Math.floor(Math.random() * Object.keys(examples).length)]);
  const sourceCodeRef = useRef(sourceCode);

  const setSourceCode = useCallback((newSourceCode: string) => {
    sourceCodeRef.current = newSourceCode;
    _setSourceCode(newSourceCode);
  }, []);

  const request = useCallback(
    async (route: string) => {
      const start = performance.now();
      setLoading(true);

      const code = sourceCodeRef.current;

      const { exports } = await teavm;

      let isAST = false;

      try {
        let result;
        switch (route) {
          case "/jasmin":
            result = exports.getJasmin(code);
            break;
          case "/javascript":
            result = exports.getVanillaJS(code);
            break;
          case "/nodejs":
            result = exports.getNodeJS(code);
            break;
          case "/ast":
            result = exports.getMermaid(code);
            if (result.error) break;
            console.log(result.output);
            isAST = true;
            break;
          case "/run":
            result = exports.getVanillaJS(code);
            if (result.error) break;

            // result.verbose += result.output

            // result.output will always have a `console.log(stdout.join('\n'));`

            const n = result.output.length - ("console.log(stdout.join('\\n'));").length
            result.output = Function(result.output.slice(0, n) + "\n return stdout.join('\\n');")();

            // result.output = Function(result.output.replace("console.log(stdout.join('\\n'));", "\nreturn stdout.join('\\n');"))();

            break;
          case "/run/legacy":
            const res = await fetch(`${backendUrl}/run`,
              {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ sourceCode: code })
              }
            )
            if (!res.ok) {
              result = {
                output: `Error with fetch to ${backendUrl}/run`,
                verbose: "Network error" + String(res),
                isAST: false
              }
            }
            result = await res.json();
            break;
          default:
            result = { output: "Unknown action", verbose: "", error: true };
        }

        setOutput({
          output: result.output || "",
          verbose: (result.verbose || "") + `\nCompleted in ${(performance.now() - start).toFixed(2)} ms`,
          isAST: isAST,
        });
      } catch (e) {
        setOutput({
          output: `Error during call:\nCompleted in ${(performance.now() - start).toFixed(2)} ms`,
          verbose: String(e),
          isAST: false,
        });
      } finally {
        setLoading(false);
      }
    },
    [teavm]
  );

  return (
    <div className="w-full h-full flex flex-col">
      <Navbar setSourceCode={setSourceCode} actions={actions} request={request} />
      <div className="grow max-h-full max-w-full" style={{ height: 'calc(100dvh - 48px)' }}>
        <ResizablePanelGroup direction="horizontal" className="box-border flex gap-2 p-3">
          <ResizablePanel defaultSize={50} key="editor-panel">
            <CodeEditor setSourceCode={setSourceCode} sourceCode={sourceCode} />
          </ResizablePanel>
          <ResizableHandle className="opacity-0" />
          <ResizablePanel defaultSize={50} key="output-panel">
            <Output output={memoizedOutput} loading={loading} />
          </ResizablePanel>
        </ResizablePanelGroup>
      </div>
    </div>
  );
}
