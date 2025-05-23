"use client"
import { useRef, useState, useEffect, useCallback } from "react";
import Script from 'next/script'
import { useTeaVM } from './components/TeaVMProvider';
import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from "@/components/ui/resizable"

import Navbar, { examples } from "./components/NavbarHome";
import Output from "./components/Output";
import CodeEditor from "./components/CodeEditor";

const actions: Record<string, { route: string, desc: string }> = {
  "Run!": {
    route: "/compile",
    desc: "Compile the code to Java byte code and run it on the JVM",
  },
  "Draw AST!": {
    route: "/ast",
    desc: "Generate a visual representation of the abstract syntax tree"
  },
  "Compile!": {
    route: "/jasmin",
    desc: "Compile to Jasmin assembler (assembly like) code"
  },
  "Transpile JS!": {
    route: "/javascript",
    desc: "Transpile to vanilla JavaScript"
  },
  "Transpile NodeJS!": {
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
  const [sourceCode, setSourceCode] = useState(Object.values(examples)[Math.floor(Math.random() * Object.keys(examples).length)]);
  const sourceCodeRef = useRef(sourceCode);

  useEffect(() => {
    sourceCodeRef.current = sourceCode;
  }, [sourceCode]);

  const request = useCallback(
    async (route: string, isAST: boolean, srcCode = sourceCode) => {
      const start = performance.now();

      setLoading(true);

      const { exports } = await teavm;
      
      try {
        let result;
        switch (route) {
          case "/jasmin":
            result = exports.getJasmin(srcCode);
            break;
          case "/javascript":
            result = exports.getVanillaJS(srcCode);
            break;
          case "/nodejs":
            result = exports.getNodeJS(srcCode);
            break;
          case "/ast":
            result = exports.getMermaid(srcCode);
            break;
          case "/compile":
            result = exports.getVanillaJS(srcCode);

            // result.verbose += result.output

            // result.output will always have a `console.log(stdout.join('\n'));`

            const n = result.output.length - ("console.log(stdout.join('\\n'));").length
            result.output = Function(result.output.slice(0, n) + "\n return stdout.join('\\n');")();

            // result.output = Function(result.output.replace("console.log(stdout.join('\\n'));", "\nreturn stdout.join('\\n');"))();

            break;
          default:
            result = { output: "Unknown action", verbose: "", error: true };
        }
        const end = performance.now(); // End time
        const timeTaken = (end - start).toFixed(2); // In milliseconds

        setOutput({
          output: result.output || "",
          verbose: (result.verbose || "") + `\nCompleted in ${timeTaken} ms`,
          isAST: isAST,
        });
      } catch (e) {
        setOutput({
          output: "Error during call:\n",
          verbose: String(e),
          isAST: false,
        });
      } finally {
        setLoading(false);
      }
    },
    [sourceCode, teavm]
  );

  return (
    <div className="w-full h-full flex flex-col">
      <Navbar setSourceCode={setSourceCode} actions={actions} request={request} />
      <div className="grow max-h-full max-w-full" style={{ height: 'calc(100dvh - 48px)' }}>
        <ResizablePanelGroup direction="horizontal" className="box-border flex gap-2 p-3">
          <ResizablePanel defaultSize={50}>
            <CodeEditor setSourceCode={setSourceCode} sourceCode={sourceCode} />
          </ResizablePanel>
          <ResizableHandle className="opacity-0" />
          <ResizablePanel defaultSize={50}>
            <Output output={output} loading={loading} />
          </ResizablePanel>
        </ResizablePanelGroup>
      </div>
    </div>
  );
}
