"use client"
import { useRef, useState, useEffect, useCallback } from "react";
import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from "@/components/ui/resizable"

import Navbar, { examples } from "./components/NavbarHome";
import Output from "./components/Output";
import CodeEditor from "./components/CodeEditor";

export default function Home() {
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

  const request = useCallback(async (route: string, isAST: boolean, srcCode = sourceCode) => {
    setLoading(true);
    try {
      const res = await fetch(`http://127.0.0.1:8080${route}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ sourceCode: srcCode })
        }
      )
      if (!res.ok) {
        throw new Error(`res status: ${res.status}`);
      }
      const data: {
        output: string,
        verbose: string,
        error: boolean
      } = await res.json();

      if (data.error) {
        setOutput({
          output: data.output,
          verbose: data.verbose,
          isAST: false
        })
      }
      setOutput({
        output: data.output,
        verbose: data.verbose,
        isAST: isAST
      });
    } catch (e) {
      setOutput({
        output: "Error with fetch: \n",
        verbose: String(e),
        isAST: false
      });
    } finally {
      setLoading(false);
    }
  }, [sourceCode]);

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
