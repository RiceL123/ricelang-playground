"use client";

import Loading from "./Loading";
import Mermaid from "./Mermaid";
import { useAtom } from "jotai";
import { loadingAtom, readOutputAtom } from "@/lib/jotai";

function Separator({ text }: { text: string }) {
  return (
    <div className="relative flex items-center py-3">
      <div className="border-muted-foreground flex-grow border-t"></div>
      <span className="text-muted-foreground mx-4 flex-shrink">{text}</span>
      <div className="border-muted-foreground flex-grow border-t"></div>
    </div>
  );
}

const Output = () => {
  const [loading] = useAtom(loadingAtom);
  const [output] = useAtom(readOutputAtom);

  return (
    <div className="bg-primary-foreground/20 border-accent-foreground hover:bg-primary-foreground/30 flex h-full max-h-full w-full max-w-full overflow-auto rounded-xl border-2 p-4 shadow-sm backdrop-blur-[4px] transition">
      {loading ? (
        <Loading message="Compiling..." />
      ) : output.isAST ? (
        <Mermaid mermaidSrc={output.output} />
      ) : (
        <div className="h-full w-full">
          {output.verbose && output.verbose !== "" && (
            <>
              <Separator text="Verbose" />
              <pre>{output.verbose}</pre>
            </>
          )}
          <Separator text="Output" />
          <pre>{output.output}</pre>
        </div>
      )}
    </div>
  );
};

export default Output;
