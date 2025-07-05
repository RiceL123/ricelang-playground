import Loading from "./Loading";
import Mermaid from "./Mermaid";
import { useAtom } from "jotai";
import { loadingAtom, readOutputAtom } from "@/lib/jotai";

function Separator({ text }: { text: string }) {
  return (
    <div className="relative flex py-3 items-center">
      <div className="flex-grow border-t border-muted-foreground"></div>
      <span className="flex-shrink mx-4 text-muted-foreground">{text}</span>
      <div className="flex-grow border-t border-muted-foreground"></div>
    </div>
  );
}

const Output = () => {
  const [loading] = useAtom(loadingAtom);
  const [output] = useAtom(readOutputAtom);

  return (
    <div className="h-full w-full max-w-full max-h-full flex p-4 overflow-auto rounded-xl bg-primary-foreground/20 backdrop-blur-[4px] border-2 border-accent-foreground shadow-sm hover:bg-primary-foreground/30 transition">
      {loading ? (
        <Loading message="Compiling..." />
      ) : output.isAST ? (
        <Mermaid mermaidSrc={output.output} />
      ) : (
        <div className="w-full h-full">
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
