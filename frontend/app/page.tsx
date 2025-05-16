"use client"
import { useState } from "react";
import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from "@/components/ui/resizable"

import Editor from "./components/Editror";
import Output from "./components/Output";

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
    <div className="h-full">
      <ResizablePanelGroup direction="horizontal">
        <ResizablePanel className="h-full">
          <div className="h-full m-3">
            <Editor />
          </div>
        </ResizablePanel>
        <ResizableHandle className="opacity-0" />
        <ResizablePanel className="h-full">
          <div className="h-full m-3">
            <Output />
          </div>
        </ResizablePanel>
      </ResizablePanelGroup>
    </div>
  );
}
