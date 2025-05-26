"use client"
import { useRef, useState, useCallback, useMemo, useEffect } from "react";
import { useTeaVM, WasmInstance } from './components/TeaVMProvider';
import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from "@/components/ui/resizable"
import { Toaster } from "@/components/ui/sonner"
import { toast } from "sonner"
import Navbar, { examples } from "./components/NavbarHome";
import Output from "./components/Output";
import CodeEditor from "./components/CodeEditor";
import { BookOpen } from 'lucide-react'
import { buttonVariants } from "@/components/ui/button";
import Link from "next/link";

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

async function getLegacyOutput(route: string, code: string, start: number, isAST = false) {
  let result;
  let output;
  try {
    const res = await fetch(`${backendUrl}${route}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ sourceCode: code })
      }
    )
    if (!res.ok) {
      result = {
        output: `Error with fetch to ${backendUrl}${route}`,
        verbose: "Network error" + String(res),
      }
    } else {
      result = await res.json();
    }
    output = { output: result.output, verbose: result.verbose + `\nCompleted in ${(performance.now() - start).toFixed(2)} ms`, isAST };
  } catch (e) {
    output = {
      output: `Error with fetch to ${backendUrl}${route}`,
      verbose: "Network error" + String(e),
      isAST: false
    }
  }
  return output;
}

export default function Home() {
  const { teavm } = useTeaVM();
  const exportsRef = useRef<WasmInstance["exports"]>(null);

  const [loading, setLoading] = useState(false);
  const [output, setOutput] = useState({
    output: "press Ctrl+S or the Compile button to compile the code!!",
    verbose: "",
    isAST: false
  });
  const [direction, setDirection] = useState<'horizontal' | 'vertical'>('horizontal');

  useEffect(() => {
    const loadWasmExports = async () => {
      try {
        const teavmModule = await teavm;
        exportsRef.current = teavmModule.exports;
      } catch (e) {
        toast.error(`WebAssembly failed: ${e}`, {
          description: `Falling back to legacy backend route for all subsequent requests. Check out the Wasm docs for your browser's compatibility.`,
          action: <Link href="https://developer.mozilla.org/en-US/docs/WebAssembly#browser_compatibility" target="_blank" className={buttonVariants({ variant: 'default' })}><BookOpen />Docs</Link>,
          duration: 20000,
          closeButton: true
        })
        return;
      }
    }
    loadWasmExports();

    const updateDirection = () => {
      setDirection(window.innerWidth < 640 ? 'vertical' : 'horizontal'); // Tailwind's `sm` breakpoint
    };
    updateDirection();
    window.addEventListener('resize', updateDirection);
    return () => window.removeEventListener('resize', updateDirection);
  }, [teavm]);


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

      await toast.promise((async () => {
        if (route == "/run/legacy") {
          setOutput(await getLegacyOutput("/run", code, start));
          setLoading(false);
          return;
        }

        if (!exportsRef.current) {
          setOutput(await getLegacyOutput(route, code, start, route == "/ast"));
          setLoading(false);
          return;
        }

        let result;
        let output: { output: string; verbose: string; isAST: boolean; } = { output: "", verbose: "", isAST: false };
        let isAST = false;

        try {
          switch (route) {
            case "/jasmin":
              result = exportsRef.current.getJasmin(code);
              break;
            case "/javascript":
              result = exportsRef.current.getVanillaJS(code);
              break;
            case "/nodejs":
              result = exportsRef.current.getNodeJS(code);
              break;
            case "/ast":
              result = exportsRef.current.getMermaid(code);
              if (result.error) break;
              isAST = true;
              break;
            case "/run":
              result = exportsRef.current.getVanillaJS(code);
              if (result.error) break;

              // result.verbose += result.output

              // result.output will always have a `console.log(stdout.join('\n'));`

              const n = result.output.length - ("console.log(stdout.join('\\n'));").length
              result.output = Function(result.output.slice(0, n) + "\n return stdout.join('\\n');")();

              // result.output = Function(result.output.replace("console.log(stdout.join('\\n'));", "\nreturn stdout.join('\\n');"))();

              break;
            default:
              result = { output: "Unknown action", verbose: "", error: true };
          }
          output = { output: result.output, verbose: result.verbose + `\nCompleted in ${(performance.now() - start).toFixed(2)} ms`, isAST };
        } catch (e) {
          output = { output: `Error during call:\nCompleted in ${(performance.now() - start).toFixed(2)} ms`, verbose: String(e), isAST: false };
        } finally {
          setOutput(output);
          setLoading(false);
        }
      })(), {
        loading: `Compiling ${(route == "/run/legacy" || !exportsRef.current) ? 'Spring Boot' : 'Wasm'}: ${route}...`,
        success: `Completed ${route}`,
        error: `Error: ${route}`
      });
    },
    []
  );

  return (
    <div className="w-full h-full flex flex-col">
      <Toaster />
      <Navbar setSourceCode={setSourceCode} actions={actions} request={request} />
      <div className="grow max-h-full max-w-full" style={{ height: 'calc(100dvh - 48px)' }}>
        <ResizablePanelGroup direction={direction} className="box-border flex gap-2 p-3">
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
