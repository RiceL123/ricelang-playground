"use client"

import { useState } from "react";

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
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <main className="flex flex-col gap-[32px] row-start-2 items-center sm:items-start">
        <button className="outline p-5" onClick={handlePress}>Compile</button>
        <textarea name="sourceCode" id="sourceCode" onChange={e => setSourceCode(e.target.value)} value={sourceCode}></textarea>
        <hr />
        <pre>{output}</pre>
      </main>
    </div>
  );
}
