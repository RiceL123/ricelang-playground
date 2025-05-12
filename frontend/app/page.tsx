"use client"
import { useRef, useState, useEffect } from "react";
import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from "@/components/ui/resizable"

import Navbar from "./components/NavbarHome";
import Output from "./components/Output";
import CodeEditor from "./components/CodeEditor";
import { defaultSourceCode } from './components/CodeEditor'

export default function Home() {
  const [output, setOutput] = useState({
    output: "press Ctrl+S or the Compile button to compile the code!!",
    exitCode: 0
  });
  const [sourceCode, setSourceCode] = useState(defaultSourceCode);
  const sourceCodeRef = useRef(sourceCode);

  useEffect(() => {
    sourceCodeRef.current = sourceCode;
  }, [sourceCode]);

  const compile = async (srcCode = sourceCode) => {
    const res = await fetch("http://127.0.0.1:8080/compile",
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ sourceCode: srcCode })
      }
    )

    const data = await res.json();
    setOutput(data);
  }

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === "s" && e.ctrlKey) {
        e.preventDefault();
        console.log("Compiling:", sourceCodeRef.current);
        compile(sourceCodeRef.current);
      }
    };

    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, []);

  function handlePress() {
    console.log(JSON.stringify({ sourceCode }))
    compile();
  }

  return (
    <div className="w-full h-full flex flex-col">
    <Navbar setSourceCode={setSourceCode} />
    <div className="grow max-h-full max-w-full" style={{ height: 'calc(100dvh - 48px)' }}>
      <ResizablePanelGroup direction="horizontal" className="box-border flex gap-2 p-3">
        <ResizablePanel defaultSize={50}>
          <CodeEditor setSourceCode={setSourceCode} sourceCode={sourceCode} />
        </ResizablePanel>
        <ResizableHandle className="opacity-0" />
        <ResizablePanel defaultSize={50}>
          <Output output={output}/>
        </ResizablePanel>
      </ResizablePanelGroup>
    </div>
    </div>
  );
}
