"use client"
import { useRef, useState, useEffect } from "react";
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
    exitCode: 0
  });
  const [sourceCode, setSourceCode] = useState(Object.values(examples)[Math.floor(Math.random() * Object.keys(examples).length)]);
  const sourceCodeRef = useRef(sourceCode);

  useEffect(() => {
    sourceCodeRef.current = sourceCode;
  }, [sourceCode]);

  const compile = async (srcCode = sourceCode) => {
    setLoading(true);
    try {
      const res = await fetch("http://127.0.0.1:8080/compile",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ sourceCode: srcCode })
        }
      )
      if (!res.ok) {
        throw new Error(`res status: ${res.status}`);
      }
      const data = await res.json();
      setOutput(data);
    } catch (e) {
      setOutput({
        output: "Error with fetch: \n\n" + e,
        exitCode: 1
      });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "s" && e.ctrlKey) {
        e.preventDefault();
        compile(sourceCodeRef.current);
      }
    };

    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, []);

  return (
    <div className="w-full h-full flex flex-col">
      <Navbar setSourceCode={setSourceCode} compile={compile} />
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
