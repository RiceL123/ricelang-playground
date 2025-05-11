"use client"
import { useState } from "react";
import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from "@/components/ui/resizable"

import Output from "./components/Output";
import CodeEditor from "./components/CodeEditor";

export default function Home() {
  const [output, setOutput] = useState("");
  const [sourceCode, setSourceCode] = useState(`int main() { putStringLn(\"hello from curl request\");\n putIntLn(1 + 2 + 3 + 4);\n putBoolLn(true);\n return 0; }`);

  const compile = async () => {
    const res = await fetch("http://127.0.0.1:8080/compile",
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ sourceCode })
      }
    )

    const data = await res.json();
    setOutput(JSON.stringify(data));
  }

  function handlePress() {
    console.log(JSON.stringify({ sourceCode }))
    compile();
  }

  return (
    <div className="grow max-h-full max-w-full">
      <ResizablePanelGroup direction="horizontal" className="box-border flex gap-2 p-3">
        <ResizablePanel defaultSize={50}>
          <CodeEditor />
        </ResizablePanel>
        <ResizableHandle className="opacity-0" />
        <ResizablePanel defaultSize={50}>
          <Output />
        </ResizablePanel>
      </ResizablePanelGroup>
    </div>
  );
}
